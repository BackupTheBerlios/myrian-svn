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

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Category;


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
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/PostgresDDLGenerator.java#4 $
 * @since 4.6.3 */

final class PostgresDDLGenerator extends BaseDDLGenerator {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/PostgresDDLGenerator.java#4 $ by $Author: randyg $, $DateTime: 2002/07/18 15:00:29 $";

    private static Category s_log = 
        Category.getInstance(PostgresDDLGenerator.class);

    private static final int MAX_COLUMN_NAME_LEN = 26;

    /**
     *  This method returns a boolean indicating whether the database contains
     *  a table with the passed in proposed name.  If no table exists then
     *  this returns false.  If there is a table, this returns true.
     */
    protected boolean tableExists(String proposedName) {
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.persistence.getPostgresTableNames");
        query.addEqualsFilter("tableName", proposedName.toUpperCase());
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
        query.addEqualsFilter("tableName", tableName.toUpperCase());
        query.addEqualsFilter("columnName", proposedColumnName.toUpperCase());
        return (query.size() > 0);
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
            throw new Error("Not Yet Implemented");
            //return "blob";
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
        throw new Error("Not Yet Implemented");
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
