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
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/OracleDDLGenerator.java#4 $
 * @since 4.6.3 */

final class OracleDDLGenerator extends BaseDDLGenerator {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/OracleDDLGenerator.java#4 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    private static final int MAX_COLUMN_NAME_LEN = 26;


    /**
     *  This method returns a boolean indicating whether the database contains
     *  a table with the passed in proposed name.  If no table exists then
     *  this returns false.  If there is a table, this returns true.
     */
    protected boolean tableExists(String proposedName) {
        String upperName = proposedName.toUpperCase();
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.persistence.getOracleTableNames");
        query.addEqualsFilter("tableName", upperName);
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
            ("com.arsdigita.persistence.getOracleColumnNames");
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
     *  This method takes the default value for the date and creates
     *  the correct syntax so that the database column will default
     *  to the correct date/time.
     */
    protected String getDefaultDateSyntax(Date defaultDate) {
        long time = (new Date()).getTime() - defaultDate.getTime();

        // we append the default sysdate and then we may append
        // more on later if the time asked for is not this time
        StringBuffer sb = new StringBuffer(" default sysdate");

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
