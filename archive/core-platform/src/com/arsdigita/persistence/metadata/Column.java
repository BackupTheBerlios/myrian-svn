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

import java.io.PrintStream;
import java.sql.Types;

/**
 * The Column class is used to keep information about the physical schema in
 * the database.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */

public class Column extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/Column.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    /**
     * The name of the table this Column belongs to.
     **/
    private String m_tableName;

    /**
     * The name of this Column.
     **/
    private String m_columnName;

    /**
     * The jdbc type code for this Column.  If not jdbc type is provided
     * the Integer.MIN_VALUE is used. 
     **/
    private int m_type;

    /**
     * The size of this Column, or -1 if the Column has no size.
     **/
    private int m_size;

    /**
     * Constructs a new Column with the given tableName and columnName.
     *
     * @param tableName The name of the table this Column belongs to.
     * @param columnName The name of this Column.
     *
     * @pre !(null == tableName || null == columnName)
     **/

    public Column(String tableName, String columnName) {
        this(tableName, columnName, Integer.MIN_VALUE, -1);
    }


    /**
     * Constructs a new Column with the given tableName, columnName, and JDBC
     * integer type code.
     *
     * @param tableName The name of the table this Column belongs to.
     * @param columnName The name of this Column.
     * @param type The JDBC integer type code for this Column.
     *
     * @pre !(null == tableName || null == columnName)
     * @pre Utilities.isJDBCType(type)
     **/

    public Column(String tableName, String columnName, int type) {
        this(tableName, columnName, type, -1);
    }


    /**
     * Constructs a new Column with the given tableName, columnName, JDBC
     * integer type code, and size.
     *
     * @param tableName The name of the table this Column belongs to.
     * @param columnName The name of this Column.
     * @param type The JDBC integer type code for this Column.
     * @param size The size of this Column.
     *
     * @pre !(null == tableName || null == columnName)
     * @pre Utilities.isJDBCType(type)
     * @pre size >= -1
     **/

    public Column(String tableName, String columnName, int type, int size) {
        m_tableName = tableName;
        m_columnName = columnName;
        m_type = type;
        m_size = size;
    }


    /**
     * Returns the name of the table that this Column belongs to.
     *
     * @return The name of the table that this Column belongs to.
     **/

    public String getTableName() {
        return m_tableName;
    }


    /**
     * Returns the name of this Column.
     *
     * @return The name of this Column.
     **/

    public String getColumnName() {
        return m_columnName;
    }

    /**
     * Returns the type of this Column.
     *
     * @return The type of this Column.
     **/

    public int getType() {
        return m_type;
    }

    /**
     * @return the tableName and the columnName, joined by a period.
     **/
    public String getQualifiedName() {
        return getTableName() + "." + getColumnName();
    }

    /**
     * Outputs a serialized version of this Column on the given PrintStream.
     *
     * The format used:
     *
     * <pre>
     * &lt;tableName&gt; "." &lt;columnName&gt; &lt;type&gt; [ "(" &lt;size&gt; ")" ]
     * </pre>
     *
     * @param out The PrintStream to use for output.
     **/

    void outputPDL(PrintStream out) {
        out.print(m_tableName + "." + m_columnName);
        if (m_type != Integer.MIN_VALUE) {
            out.print(" " + getTypeName(m_type));
        }

        if (m_size > -1) {
            out.print("(" + m_size + ")");
        }
    }

    private static String getTypeName(int type) {
        switch (type) {
        case Types.ARRAY:
            return "ARRAY";
        case Types.BIGINT:
            return "BIGINT";
        case Types.BINARY:
            return "BINARY";
        case Types.BIT:
            return "BIT";
        case Types.BLOB:
            return "BLOB";
        case Types.CHAR:
            return "CHAR";
        case Types.CLOB:
            return "CLOB";
        case Types.DATE:
            return "DATE";
        case Types.DECIMAL:
            return "DECIMAL";
        case Types.DISTINCT:
            return "DISTINCT";
        case Types.DOUBLE:
            return "DOUBLE";
        case Types.FLOAT:
            return "FLOAT";
        case Types.INTEGER:
            return "INTEGER";
        case Types.JAVA_OBJECT:
            return "JAVA_OBJECT";
        case Types.LONGVARBINARY:
            return "LONGVARBINARY";
        case Types.LONGVARCHAR:
            return "LONGVARCHAR";
        case Types.NULL:
            return "NULL";
        case Types.NUMERIC:
            return "NUMERIC";
        case Types.OTHER:
            return "OTHER";
        case Types.REAL:
            return "REAL";
        case Types.REF:
            return "REF";
        case Types.SMALLINT:
            return "SMALLINT";
        case Types.STRUCT:
            return "STRUCT";
        case Types.TIME:
            return "TIME";
        case Types.TIMESTAMP:
            return "TIMESTAMP";
        case Types.TINYINT:
            return "TINYINT";
        case Types.VARBINARY:
            return "VARBINARY";
        case Types.VARCHAR:
            return "VARCHAR";
        default:
            throw new IllegalArgumentException("No such jdbcType: " + type);
        }
    }

    /**
     *  This returns the size of the column
     */
    public int getSize() {
        return m_size;
    }
}
