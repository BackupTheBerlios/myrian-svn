/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.metadata;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;


/**
 * This class provides an implementation that automatically generates DDL
 * statements based on the information passed in.  The primary use for
 * this class is to provide DDL to create and alter tables used by
 * {@link com.arsdigita.persistence.metadata.DynamicObjectType}.
 *
 * Note that the DDLGenerator does not support dropping tables and
 * columns.  This is to avoid data loss and allow rolling back of UDCT
 * operations.
 *
 * @author <a href="mailto:randyg@alum.mit.edu">Randy Graebner</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/PostgresDDLGenerator.java#8 $
 * @since 4.6.3 */

final class PostgresDDLGenerator extends BaseDDLGenerator {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/PostgresDDLGenerator.java#8 $ by $Author: rhs $, $DateTime: 2002/10/14 16:12:17 $";

    private static final Logger s_log =
        Logger.getLogger(PostgresDDLGenerator.class);

    private static final int MAX_COLUMN_NAME_LEN = 26;

    private static ArrayList constraints = new ArrayList();

    /**
     *  This method returns a boolean indicating whether the database contains
     *  a table with the passed in proposed name.  If no table exists then
     *  this returns false.  If there is a table, this returns true.
     */
    protected boolean tableExists(String proposedName) {
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.persistence.getPostgresTableNames");
        query.addEqualsFilter("tableName", proposedName.toLowerCase());
        return (query.size() > 0);
    }


    /**
     *  This method returns a boolean indicating whether the database table
     *  contains the passed in column name.  If the column does not exist
     *  in the table then this returns false.  If the column is already
     *  part of the table then this returns true.
     */
    protected boolean columnExists(String tableName, String proposedColumnName) {
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.persistence.getPostgresColumnNames");
        query.addEqualsFilter("tableName", tableName.toLowerCase());
        query.addEqualsFilter("columnName", proposedColumnName.toLowerCase());
        return (query.size() > 0);
    }


    /**
     * Takes an object type, a primary key property, and a collection of
     * additional properties.  Returns either a collection of "create table"
     * or a collection of "alter table" statements to add the properties
     * to the object type's table.  If there is nothing to modify, this
     * returns null.
     *
     * @param type the ObjectType
     * @param keyColumn the key column of the object type,
     * @param properties additional properties to add
     * @param defaultValueMap mapping from property name to default value
     * @return a DDL statement to create or alter the object type table
     */
    public Collection generateTable(ObjectType type,
                                    Column keyColumn,
                                    Collection properties,
                                    Map defaultValueMap) {
        StringBuffer ddl = new StringBuffer();
        ArrayList statements = new ArrayList();
        String tableName = type.getColumn().getTableName();
        boolean tableExists = tableExists(tableName);

        if (!tableExists) {
            // create table
            return super.generateTable(type, keyColumn,
                                       properties, defaultValueMap);
        } else {
            // alter table
            ArrayList list = new ArrayList();
            if (properties == null) {
                return list;
            }

            Iterator props = properties.iterator();

            while (props.hasNext()) {
                Property property = (Property)props.next();

                // collections are handled by mapping tables later
                if (!property.isCollection()) {
                    Object defaultValue = null;

                    Column propCol;

                    if (property.isAttribute()) {
                        propCol = property.getColumn();
                    } else {
                        propCol = ((JoinElement)property.getJoinPath()
                                   .getPath().get(0)).getFrom();
                    }

                    String columnType =
                        getJDBCTypeString(property, propCol);
                    String columnName =
                        alterStringForSQL(propCol.getColumnName());

                    StringBuffer sb = new StringBuffer();

                    list.add("alter table " + tableName + " add " +
                             columnName + " " + columnType);

                    if (defaultValueMap != null) {
                        Object value = defaultValueMap.get(property.getName());
                        if (value != null) {
                            list.add("alter table " + tableName + " alter " +
                                     columnName + " set " +
                                     getDefaultString(value));
                        }
                    }

                    if (property.getMultiplicity() == Property.REQUIRED) {
                        String constraintName =
                            getConstraintName(tableName, columnName, "nn");
                        list.add("alter table " + tableName + " add " +
                                 "constraint " + constraintName + " " +
                                 "check (" + columnName + " notnull)");
                    }
                }
            }

            if (list.size() == 0) {
                return null;
            }

            return list;
        }
    }


    /**
     *  This returns a unique constraint name that can be used when
     *  added a constraint to a table.
     *  @param tableName The name of the table to add the constraint
     *  @param columnName The name of the column to add the constraint
     *  @param suffix The suffix of the constraint name.  This is often
     *  something like "nn" for "not null" or "fk" for foreign key.
     */
    private String getConstraintName(String tableName, String columnName,
                                     String suffix) {
        boolean findNewName = true;
        String constraintName = tableName + "_" + columnName;
        if (constraintName.length() > 20) {
            constraintName = constraintName.substring(0, 19);
        }
        constraintName = (constraintName + "_" + suffix).toLowerCase();
        int count = 0;

        synchronized(constraints) {
            if (constraints.size() == 0) {
                // populate the variable
                DataQuery query = SessionManager.getSession().retrieveQuery
                    ("com.arsdigita.persistence.getPostgresConstraintNames");
                while (query.next()) {
                    constraints.add(((String)query.get("name")).toLowerCase());
                }
                query.close();
            }
        }

        while (findNewName) {
            synchronized(constraints) {
                if (!constraints.contains(constraintName)) {
                    constraints.add(constraintName);
                    findNewName = false;
                }
            }
            if (findNewName) {
                constraintName = incrementName(constraintName, count)
                    .toLowerCase();
                count++;
            }
        }
        return constraintName;
    }


    /**
     * Returns a SQL declaration for the jdbctype and size specified
     *
     * @param jdbcType the type
     * @param size the size
     * @return a SQL declaration for the jdbctype and size specified
     */
    protected String getTypeDeclaration(int jdbcType, int size) {
        if (size < 1) {
            size = 32;
        }

        switch (jdbcType) {
        case Types.BIGINT:
            return "BigInt(" + size + ")";
        case Types.BINARY:
        case Types.BIT:
            return "char(1)";
        case Types.BLOB:
            return "bytea";
        case Types.CHAR:
            return "char(" + size + ")";
        case Types.CLOB:
            return "text";
        case Types.DATE:
        case Types.TIMESTAMP:
            return "timestamp";
        case Types.DECIMAL:
        case Types.NUMERIC:
            return "numeric(" + size + ")";
        case Types.DOUBLE:
        case Types.FLOAT:
            return "double precision";
        case Types.INTEGER:
            return "integer";
        case Types.LONGVARCHAR:
            if (size > 4000) {
                return "text";
            } else {
                return "varchar(" + size + ")";
            }
        case Types.REAL:
            return "real";
        case Types.SMALLINT:
            return "smallint";
        case Types.VARCHAR:
            return "varchar(" + size + ")";
        default:
            throw new IllegalArgumentException("The passed in type {" +
                                               jdbcType + "} is not supported");
        }
    }


    /**
     *  This method takes the default value for the date and creates
     *  the correct syntax so that the database column will default
     *  to the correct date/time.
     */
    protected String getDefaultDateSyntax(Date defaultDate) {
        long time = (new Date()).getTime() - defaultDate.getTime();

        // we append the default now() and then we may append
        // more on later if the time asked for is not this time
        StringBuffer sb = new StringBuffer(" default now()");

        // if the difference in time between NOW and when the time
        // was created is more than 1 minute then we add an offset to
        // sysdate.  To do this, we convert milleseconds (the java
        // way of keeping time) to days (the way that oracle wants
        // the time to be expressed)
        if (time > 6000 || time < -6000) {
            float fullTime = Math.round(time*10/(60*60));
            float offset = fullTime/10000;
            if (offset > 0) {
                sb.append(" - reltime('" + offset + " hours'::timespan)");
            } else {
                sb.append(" + reltime('" + offset*(-1) + " hours'::timespan)");
            }
        }
        return sb.toString();
    }


    /**
     * Database systems have varying restrictions on the length of
     * column names.
     * This method obtains the max length for the particular implementation.
     *
     * @return The maximum length
     * @post return > 0
     */
    public int getMaxColumnNameLength() {
        return MAX_COLUMN_NAME_LEN;
    }
}
