/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.metadata;

import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.util.StringUtils;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.sql.Types;
import java.sql.DatabaseMetaData;

/**
 * This class provides an implementationthat automatically generates DDL
 * statements based on the information passed in.  The primary use for
 * this class is to provide DDL to create and alter tables used by
 * {@link com.arsdigita.persistence.metadata.DynamicObjectType}.
 *
 * @author <a href="mailto:randyg@alum.mit.edu">Randy Graebner</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/OracleDDLGenerator.java#1 $
 * @since 4.6.3 */

final class OracleDDLGenerator implements DDLGenerator {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/OracleDDLGenerator.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";
    
    // this is used to keep track of which tables have been
    // returned.  This helps avoid duplicate tables between
    // when the user asks for the table and when the table
    // is actually created
    private static List tables = new ArrayList();
    private static List reservedWords = new ArrayList();
    private final static String [] RESERVED_WORDS_ARRAY = {
        "ACCESS", "ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC",
        "AUDIT", "BETWEEN", "BY", "CHAR", "CHECK", "CLUSTER",
        "COLUMN", "COMMENT", "COMPRESS", "CONNECT", "CREATE",
        "CURRENT", "DATE", "DECIMAL", "DEFAULT", "DELETE", "DESC",
        "DISTINCT", "DROP", "ELSE", "EXCLUSIVE", "EXISTS", "FILE",
        "FLOAT", "FOR", "FROM", "GRANT", "GROUP", "HAVING",
        "IDENTIFIED", "IMMEDIATE", "IN", "INCREMENT", "INDEX",
        "INITIAL", "INSERT", "INTEGER", "INTERSECT", "INTO", "IS",
        "LEVEL", "LIKE", "LOCK", "LONG", "MAXEXTENTS", "MINUS",
        "MLSLABEL", "MODE", "MODIFY", "NOAUDIT", "NOCOMPRESS", "NOT",
        "NOWAIT", "NULL", "NUMBER", "OF", "OFFLINE", "ON", "ONLINE",
        "OPTION", "OR", "ORDER", "PCTFREE", "PRIOR", "PRIVILEGES",
        "PUBLIC", "RAW", "RENAME", "RESOURCE", "REVOKE", "ROW",
        "ROWID", "ROWNUM", "ROWS", "SELECT", "SESSION", "SET",
        "SHARE", "SIZE", "SMALLINT", "START", "SUCCESSFUL", "SYNONYM",
        "SYSDATE", "TABLE", "THEN", "TO", "TRIGGER", "UID", "UNION",
        "UNIQUE", "UPDATE", "USER", "VALIDATE", "VALUES", "VARCHAR",
        "VARCHAR2", "VIEW", "WHENEVER", "WHERE", "WITH" 
    };

    // add Oracle reserved words to the list of unavailable table names
    static {
        for ( int i = 0; i < RESERVED_WORDS_ARRAY.length; i++ ) {
            reservedWords.add(RESERVED_WORDS_ARRAY[i]);
        }
        tables.addAll(reservedWords);
    }
        
    // used to keep track of which columns have been returned.
    private static Map columns = new HashMap();

    // This should probably be exposed via a method on DDLGenerator's interface,
    // as it is needed within DynamicObjectType.
    private static final int MAX_COLUMN_NAME_LEN = 26;
    private String findUniqueTableName(String proposedName) {
        // now we make sure that the proposed name does not already exist
        boolean checkDuplicateName = true;
        int count = 0;

        while (checkDuplicateName) {
            // TODO: This should eventually be replaced by calls using the 
            // metadata in OracleDatabaseMetadata
            // this should probably also have a better algorighm
            String upperName = proposedName.toUpperCase();
            DataQuery query = SessionManager.getSession().retrieveQuery(
                              "com.arsdigita.persistence.getOracleTableNames");
            query.addEqualsFilter("tableName", upperName);
            if (!tables.contains(upperName) && query.size() == 0) {
                // we synchronize this so that we make sure that
                // two threads are not modifying the variable at once
                synchronized(tables) {
                    if (!tables.contains(upperName)) {
                        tables.add(upperName);
                        checkDuplicateName = false;
                    } else {
                        count++;
                        proposedName = incrementName(proposedName, count);
                    }
                }
            } else {
                proposedName = incrementName(proposedName, count);
                count++;
            }
        }

        return proposedName;
    }

    /**
     *  This takes and object type and generates a table name that
     *  can be used to store the information about the object type
     *
     *  @param modelName This is the name of the model.  This will 
     *                   typically be used as the prefix of the table name.
     *  @param objectName This is the name of the object type that
     *                    the system is storing.  This will typically
     *                    be the suffix of the table name
     */
    public String generateTableName(String modelName, String objectName) {
        // if the model starts with "com.arsdigita" we strip that off
        int index = modelName.indexOf("com.arsdigita.");
        if (index > -1) {
            modelName = modelName.substring(14);
        }

        String proposedName = alterStringForSQL(modelName) + "_" + 
            objectName.toLowerCase();

        if (proposedName.length() > MAX_COLUMN_NAME_LEN) {
            proposedName = proposedName.substring(0, MAX_COLUMN_NAME_LEN - 1);
        }

        return findUniqueTableName(proposedName);
    }

    /**
     * Determines a unique name for a mapping table for a particular 
     * role reference and object type.
     * 
     * @param type the objecttype that owns the property
     * @param name the property name
     * @return a unique name for a mapping table
     */
    public String generateMappingTableName(ObjectType type, String name) {
        String proposedName = type.getName().toLowerCase() + "_" +
                              name.toLowerCase();

        if (proposedName.length() > MAX_COLUMN_NAME_LEN) {
            proposedName = proposedName.substring(0, MAX_COLUMN_NAME_LEN - 1);
        }

        return findUniqueTableName(proposedName);
    }

    /**
     *  This takes in the tableName and a proposedName and returns 
     *  a name that can be used for a new column name
     *
     *  @param objectType The type for this column to be added to.  This
     *                    is useful to checking to make sure that none
     *                    of the other proposed columns have this problem
     *  @param proposedName The "ideal" name for the column.  This string
     *                      will be lowercased and returned if there is not
     *                      already a column by that name (in which case
     *                      a column name will be generated).
     */
    public String generateColumnName(ObjectType objectType, String proposedName) {
        proposedName = alterStringForSQL(proposedName.toLowerCase());

        Column key = Utilities.getColumn(objectType);
        if (key == null) { 
            throw new PersistenceException(objectType.getName() + " does not " +
                                           "support MDSQL.");
        }

        String tableName = key.getTableName();
        String originalName = proposedName;

        if (proposedName.length() > MAX_COLUMN_NAME_LEN) {
            proposedName = proposedName.substring(0, MAX_COLUMN_NAME_LEN - 1);
        }

        // now we make sure that the proposed name does not already exist
        boolean checkDuplicateName = true;
        int count = 0;

        while (checkDuplicateName) {
            // let's make sure that the objectType does not already plan
            // on using this column name
            boolean conflictingProperty = true;
            while (conflictingProperty) {
                if (reservedWords.contains(proposedName.toUpperCase())) {
                    proposedName = incrementName(proposedName, count);
                    count++;
                    conflictingProperty = true;
                } else {
                    Iterator prop = objectType.getProperties();
                    
                    if (!prop.hasNext()) {
                        break;
                    }
                    
                    int detectStop = 0;
                    
                    while (prop.hasNext()) {
                        Property property = (Property)prop.next();
                        
                        if (!property.isAttribute()) {
                            continue;
                        }
                        
                        Column col = property.getColumn();
                        if (col != null) {
                            if (proposedName.equalsIgnoreCase(col.getColumnName())) {
                                proposedName = incrementName(proposedName, count);
                                count++;
                                conflictingProperty = true;
                                break;
                            } else {
                                conflictingProperty = false;
                            }
                        } else {
                            detectStop++;
                            if (objectType.getSupertype() != null) {
                                ObjectType st = objectType.getSupertype();
                                
                                if (st.hasProperty(property.getName())) {
                                    throw new PersistenceException(
                                        objectType.getName() +
                                        " is not MDSQL-ready and " +
                                        "therefore cannot be extended dynamically.  " +
                                        property.getName() + " has no defined column");
                                }
                            }
                        }
                    }
                    if ((detectStop > 1) && (!prop.hasNext())) {
                        conflictingProperty = false;
                    }
                }

            }

            // now that we know it is not a planned name, we should
            // check the database and make sure it is not already in use.
            // TODO: This should eventually be replaced by calls using the 
            // metadata in OracleDatabaseMetadata
            // this should probably also have a better algorighm
            DataQuery query = SessionManager.getSession().retrieveQuery(
                              "com.arsdigita.persistence.getOracleColumnNames");
            query.addEqualsFilter("tableName", tableName.toUpperCase());
            query.addEqualsFilter("columnName", proposedName.toUpperCase());
            if (query.size() == 0) {
                checkDuplicateName = false;
            } else {
                proposedName = incrementName(proposedName, count);
                count++;
            }
        }

        return proposedName;
    }


    /** 
     *  This takes a string and increments the name to be a new,
     *  hopefully unique, string.  
     *
     *  @param nameToIncrement The name to slightly alter
     *  @param count The key to help the string be altered
     */
    private String incrementName(String nameToIncrement, int count) {
        if (count > 0) {
            // we assume the string has been through before so lets remove
            // the final three characters
            nameToIncrement = nameToIncrement.substring(0, 
                                                   nameToIncrement.length() -3);
        }

        if (nameToIncrement.length() > 23) {
            nameToIncrement = nameToIncrement.substring(0,22);
        }

        String suffix = Integer.toString(count);
        if (count < 10) {
            suffix = "00" + suffix;
        } else if (count < 100) {
            suffix = "0" + suffix;
        }
        return nameToIncrement = nameToIncrement + suffix;
    }

    /**
     * Returns a SQL declaration for the jdbctype and size specified
     *
     * @param jdbcType the type
     * @param size the size
     * @return a SQL declaration for the jdbctype and size specified
     */
    public String getTypeDeclaration(int jdbcType, int size) {
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
            return "blob";
        case Types.CHAR:
            return "char(" + size + ")";
        case Types.CLOB:
            return "clob";
        case Types.DATE:
        case Types.TIMESTAMP:
            return "date";
        case Types.DECIMAL:
            return "decimal(" + size + ")";
        case Types.DOUBLE:
            return "double precision";
        case Types.FLOAT:
            return "float(" + size + ")";
        case Types.INTEGER:
            return "integer";
        case Types.LONGVARCHAR:
            if (size > 4000) {
                return "clob";
            } else {
                return "varchar(" + size + ")";
            }
        case Types.NUMERIC:
            return "number";
        case Types.REAL:
            return "real";
        case Types.SMALLINT:
            return "smallint(" + size + ")";
        case Types.VARCHAR:
            return "varchar(" + size + ")";
        default:
            throw new IllegalArgumentException("The passed in type {" +
                                               jdbcType + "} is not supported");
        }
    }

    /**
     *  Given a metadata column, this generates the syntax to create
     *  a single column within a database table.  Before being executed,
     *  the returned string needs to have a "prefix" appened to it
     *  and then be followed by a close "(".  For example:
     *  <pre><code>
     *  StringBuffer sb = new StringBuffer(alterTablePrefixAddColumns(myTable));
     *  sb.append(generateColumnToAdd(myProperty, myDefaultValue));
     *  sb.append(")");
     *  statement.executeUpdate(sb.toString());
     *  </code></pre>
     *  
     *  @param property The property to use to generate the DDL
     *  @param defaultValue The default for this column.  This may be null
     *  @pre property != null
     */
    public String generateColumnToAdd(Property property, Object defaultValue) {
        Column propCol;

        if (property.isAttribute()) {
            propCol = property.getColumn();
        } else {
            propCol = ((JoinElement)property.getJoinPath().getPath().get(0))
                        .getFrom();
        }

        int size = propCol.getSize();
        int jdbcType;

        jdbcType = propCol.getType();
        if (property.getType() instanceof SimpleType && 
            jdbcType == Integer.MIN_VALUE) {

            jdbcType = ((SimpleType)property.getType()).getJDBCtype();
        }

        StringBuffer sb = new StringBuffer();
        sb.append(alterStringForSQL(propCol.getColumnName()) + " ");

        sb.append(getTypeDeclaration(jdbcType, size));

        if (defaultValue != null) {
            if (defaultValue instanceof Date) {
                Date value = (Date)defaultValue;
                long time = (new Date()).getTime() - value.getTime();

                // we append the default sysdate and then we may append
                // more on later if the time asked for is not this time
                sb.append(" default sysdate");

                // if the difference in time between NOW and when the time
                // was created is more than 1 minute then we add an offset to
                // sysdate.  To do this, we convert milleseconds (the java
                // way of keeping time) to days (the way that oracle wants
                // the time to be expressed)
                if (time > 6000 || time < -6000) {
                    float fullTime = Math.round(time*10/(60*60*24));
                    float offset = fullTime/10000;
                    if (offset > 0) {
                        sb.append(" - " + offset);
                    } else {
                        sb.append(" + " + offset*(-1));
                    }
                } 
            } else if (defaultValue instanceof Boolean) {
                if (((Boolean)defaultValue).booleanValue()) {
                    sb.append(" default '1'");
                } else {
                    sb.append(" default '0'");
                }
            } else {
                sb.append(" default '" + defaultValue.toString() + "'");
            }
        }

        if (property.getMultiplicity() == Property.REQUIRED) {
            sb.append(" not null");
        } 

        if (!property.isAttribute()) {
            Column foreignKey =
                ((JoinElement)property.getJoinPath().getPath().get(0)).getTo();

            sb.append(" references ")
              .append(foreignKey.getTableName())
              .append("(")
              .append(foreignKey.getColumnName())
              .append(") ");
        }

        return sb.toString();
    }


    /**
     *  This takes a string and replaces any/all characters 
     *  not allowed by SQL with "_"
     *
     *  @param stringToAlter This is the string that is used as the
     *                       input to this system
     */
    // TODO: needs to be tested
    private String alterStringForSQL(String stringToAlter) {
        // TODO test this next section with the while loop
        StringTokenizer tokens = new StringTokenizer(stringToAlter, 
                                                     ". ;()", true);
        StringBuffer sb = new StringBuffer();
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (token.equals(".") || token.equals(" ") || 
                token.equals("(") || token.equals(")")) {
                sb.append("_");
            } else {
                sb.append(token);
            }
        }
        return sb.toString();
    }

    /**
     * Takes an object type, a primary key property, and a collection of
     * additional properties.  Returns either a "create table" or an "alter
     * table" statement to add the properties to the object type's table.
     *
     * @param type the ObjectType
     * @param keyColumn the key column of the object type,
     * @param properties additional properties to add
     * @param defaultValueMap mapping from property name to default value
     * @return a DDL statement to create or alter the object type table
     */
    public String generateTable(ObjectType type,
                                Column keyColumn,
                                Collection properties,
                                Map defaultValueMap) {
        StringBuffer ddl = new StringBuffer();
        List statements = new ArrayList();
        String tableName = Utilities.getColumn(type).getTableName();
        DataQuery query = SessionManager.getSession().retrieveQuery(
                          "com.arsdigita.persistence.getOracleTableNames");

        query.addEqualsFilter("tableName", tableName.toUpperCase());

        if (keyColumn != null) {
            ddl.append(keyColumn.getColumnName());

            if (type.getSupertype() != null) {
                Column superKey = Utilities.getColumn(type.getSupertype());

                ddl.append(" ")
                   .append(getTypeDeclaration(superKey.getType(),
                                              superKey.getSize()))
                   .append(" primary key references ")
                   .append(superKey.getTableName())
                   .append("(")
                   .append(superKey.getColumnName())
                   .append(")");
            } else {
                ddl.append(" integer primary key");
            }

            statements.add(ddl.toString());
        }

        if (properties != null) {

            Iterator props = properties.iterator();

            while (props.hasNext()) {
                Property prop = (Property)props.next();

                // collections are handled by mapping tables later
                if (!prop.isCollection()) {
                    Object defaultValue = null;

                    if (defaultValueMap != null) {
                        defaultValue = defaultValueMap.get(prop.getName());
                    }

                    statements.add(generateColumnToAdd(prop, defaultValue));
                }
            }
        }

        ddl = new StringBuffer();

        if (query.size() != 0) {
            ddl.append("alter table ").append(tableName).append(" add (\n");
        } else {
            ddl.append("create table ").append(tableName).append(" (\n");
        }

        if (statements.size() == 0) {
            return null;
        }

        ddl.append(StringUtils.join(statements, ",\n"))
           .append(")\n");

        return ddl.toString();
    }

    /**
     * Takes an object type, a primary key property, and a collection of
     * additional properties.  Returns either a "create table" or an "alter
     * table" statement to add the properties to the object type's table.
     *
     * @param type the ObjectType
     * @param keyColumn the key property of the object type, must not be
     *                    on a joinpath.
     * @param properties additional properties to add
     * @return a DDL statement to create or alter the object type table
     */
    public String generateTable(ObjectType type,
                                Column keyColumn,
                                Collection properties) {
        return generateTable(type, keyColumn, properties, null);
    }

    /**
     * Takes an object type and a collection of additional properties. 
     * Returns either a "create table" or an "alter table" statement to add
     * the properties to the object type's table.
     *                              
     * @param type the ObjectType
     * @param properties additional properties to add
     * @return a DDL statement to create or alter the object type table
     */
    public String generateTable(ObjectType type,
                                Collection properties) {
        return generateTable(type, null, properties, null);
    }
    /**
     * Takes an object type and a collection of additional properties. 
     * Returns either a "create table" or an "alter table" statement to add
     * the properties to the object type's table.
     *                              
     * @param type the ObjectType
     * @param properties additional properties to add
     * @param defaultValueMap mapping from property name to default value
     * @return a DDL statement to create or alter the object type table
     */
    public String generateTable(ObjectType type,
                                Collection properties,
                                Map defaultValueMap) {
        return generateTable(type, null, properties, defaultValueMap);
    }

    /**
     * Creates the DDL to generate a mapping table from one object type to
     * another.
     *
     * @param type the dynamically generated object type
     * @param property the role reference
     * @return the DDL statement to create the mapping table
     */
    public String generateMappingTable(ObjectType type, 
                                       Property property) {
        StringBuffer ddl = new StringBuffer();

        if (property.isAttribute()) {
            throw new IllegalArgumentException("Property " +
                                               property.getName() +
                                               " is not a role reference");
        }

        JoinElement je0 = (JoinElement)property.getJoinPath().getPath().get(0);
        JoinElement je1 = (JoinElement)property.getJoinPath().getPath().get(1);

        ddl.append("create table ")
           .append(je0.getTo().getTableName())
           .append(" ( \n")
           .append(je0.getTo().getColumnName())
           .append(" ")
           .append(getTypeDeclaration(je0.getFrom().getType(),
                                      je0.getFrom().getSize()))
           .append(" references ")
           .append(je0.getFrom().getTableName())
           .append("(")
           .append(je0.getFrom().getColumnName())
           .append("),\n")
           .append(je1.getFrom().getColumnName())
           .append(" ")
           .append(getTypeDeclaration(je1.getTo().getType(),
                                      je1.getTo().getSize()))
           .append(" references ")
           .append(je1.getTo().getTableName())
           .append("(")
           .append(je1.getTo().getColumnName())
           .append("),\n")
           .append("primary key (")
           .append(je0.getTo().getColumnName())
           .append(", ")
           .append(je1.getFrom().getColumnName())
           .append(")\n)\n");

        return ddl.toString();
    }

    public int getMaxColumnNameLength() {
        return MAX_COLUMN_NAME_LEN;
    }
}
