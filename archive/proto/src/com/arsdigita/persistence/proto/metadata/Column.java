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

package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.db.DbHelper;

import java.io.PrintStream;
import java.sql.Types;
import java.util.*;

/**
 * The Column class is used to keep information about the physical schema in
 * the database.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/02/17 $
 */

public class Column extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Column.java#5 $ by $Author: rhs $, $DateTime: 2003/02/17 13:30:53 $";

    /**
     * The name of this Column.
     **/
    private String m_name;

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
     * The precision of this Column or -1 if the Column has no precision.
     **/
    private int m_precision;

    private boolean m_isNullable = true;
    private Set m_constraints = new HashSet();

    /**
     * Constructs a new Column with the given table and columnName.
     *
     * @param table The name of the table this Column belongs to.
     * @param columnName The name of this Column.
     *
     * @pre (table != null && columnName != null)
     **/

    public Column(String columnName) {
        this(columnName, Integer.MIN_VALUE);
    }

    /**
     * Constructs a new Column with the given table, columnName, and JDBC
     * integer type code.
     *
     * @param columnName The name of this Column.
     * @param type The JDBC integer type code for this Column.
     *
     * @pre (columnName != null)
     * @pre Utilities.isJDBCType(type)
     **/

    public Column(String columnName, int type) {
        this(columnName, type, -1);
    }


    /**
     * Constructs a new column with the given columnName, type and size.
     *
     * @param columnName The name of this Column.
     * @param type The JDBC integer type code for this Column.
     * @param size The size of this Column.
     *
     * @pre (columnName != null)
     * @pre Utilities.isJDBCType(type)
     **/

    public Column(String columnName, int type, int size) {
        this(columnName, type, size, -1);
    }


    /**
     * Constructs a new Column with the given table, columnName, JDBC
     * integer type code, and size.
     *
     * @param columnName The name of this Column.
     * @param type The JDBC integer type code for this Column.
     * @param size The size of this Column.
     *
     * @pre (columnName != null)
     * @pre Utilities.isJDBCType(type)
     * @pre size >= -1
     **/

    public Column(String columnName, int type, int size, int precision) {
        this(columnName, type, size, precision, true);
    }

    /**
     * Constructs a new Column with the given table, columnName, JDBC
     * integer type code, and size.
     *
     * @param columnName The name of this Column.
     * @param type The JDBC integer type code for this Column.
     * @param size The size of this Column.
     * @param precision The precision of this Column.
     * @param isNullable True if the column is nullable.
     *
     * @pre (columnName != null)
     * @pre Utilities.isJDBCType(type)
     * @pre size >= -1
     **/

    public Column(String name, int type, int size, int precision,
                  boolean isNullable) {
        m_name = name;
        m_type = type;
        m_size = size;
        m_precision = precision;
        m_isNullable = isNullable;

        if (m_size == 0) {
            throw new IllegalArgumentException
                ("Size cannot be zero");
        }
    }


    void addConstraint(Constraint constraint) {
        m_constraints.add(constraint);
    }


    /**
     * Returns the table that this Column belongs to.
     *
     * @return The table that this Column belongs to.
     **/

    public Table getTable() {
        return (Table) getParent();
    }


    /**
     * Returns the name of this Column.
     *
     * @return The name of this Column.
     **/

    public String getName() {
        return m_name;
    }

    /**
     * Returns the name of the table that this Column belongs to.
     *
     * @return The name of the table that this Column belongs to.
     **/

    public String getTableName() {
        return getTable().getName();
    }


    /**
     * Returns the name of this Column.
     *
     * @return The name of this Column.
     **/

    public String getColumnName() {
        return getName();
    }

    public boolean isNullable() {
        return m_isNullable;
    }

    public void setNullable(boolean value) {
        m_isNullable = value;
    }

    /**
     * Returns the type of this Column.
     *
     * @return The type of this Column.
     **/

    public int getType() {
        return m_type;
    }

    public void setType(int type) {
        m_type = type;
    }

    /**
     * @return the table name and the column name, joined by a period.
     **/
    public String getQualifiedName() {
        return getTableName() + "." + getColumnName();
    }

    public boolean isPrimaryKey() {
        return getTable().getPrimaryKey() == getTable().getUniqueKey(this);
    }

    public boolean isUniqueKey() {
        return getTable().getUniqueKey(this) != null;
    }

    public boolean isForeignKey() {
        return getTable().getForeignKey(this) != null;
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
        out.print(getTable().getName() + "." + m_name);
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

    public void setSize(int size) {
        m_size = size;
    }

    public int getPrecision() {
        return m_precision;
    }

    public void setPrecision(int precision) {
        m_precision = precision;
    }

    private static final Map DEFAULT = new HashMap();
    private static final Map POSTGRES = new HashMap();
    private static final Map ORACLE = new HashMap();

    static {
        DEFAULT.put(new Integer(Types.ARRAY), "ARRAY");
        DEFAULT.put(new Integer(Types.BIGINT), "BIGINT");
        ORACLE.put(new Integer(Types.BIGINT), "integer");
        DEFAULT.put(new Integer(Types.BINARY), "BINARY");
        DEFAULT.put(new Integer(Types.BIT), "BIT");
        ORACLE.put(new Integer(Types.BIT), "CHAR(1)");
        POSTGRES.put(new Integer(Types.BIT), "BOOLEAN");
        DEFAULT.put(new Integer(Types.BLOB), "BLOB");
        POSTGRES.put(new Integer(Types.BLOB), "BYTEA");
        DEFAULT.put(new Integer(Types.CHAR), "CHAR");
        DEFAULT.put(new Integer(Types.CLOB), "CLOB");
        POSTGRES.put(new Integer(Types.CLOB), "TEXT");
        DEFAULT.put(new Integer(Types.DATE), "DATE");
        DEFAULT.put(new Integer(Types.DECIMAL), "DECIMAL");
        DEFAULT.put(new Integer(Types.DISTINCT), "DISTINCT");
        DEFAULT.put(new Integer(Types.DOUBLE), "DOUBLE");
        ORACLE.put(new Integer(Types.DOUBLE), "integer");
        DEFAULT.put(new Integer(Types.FLOAT), "FLOAT");
        DEFAULT.put(new Integer(Types.INTEGER), "INTEGER");
        DEFAULT.put(new Integer(Types.JAVA_OBJECT), "JAVA_OBJECT");
        DEFAULT.put(new Integer(Types.LONGVARBINARY), "LONGVARBINARY");
        DEFAULT.put(new Integer(Types.LONGVARCHAR), "LONGVARCHAR");
        DEFAULT.put(new Integer(Types.NULL), "NULL");
        DEFAULT.put(new Integer(Types.NUMERIC), "NUMERIC");
        DEFAULT.put(new Integer(Types.OTHER), "OTHER");
        DEFAULT.put(new Integer(Types.REAL), "REAL");
        DEFAULT.put(new Integer(Types.REF), "REF");
        DEFAULT.put(new Integer(Types.SMALLINT), "SMALLINT");
        DEFAULT.put(new Integer(Types.STRUCT), "STRUCT");
        DEFAULT.put(new Integer(Types.TIME), "TIME");
        DEFAULT.put(new Integer(Types.TIMESTAMP), "TIMESTAMP");
        ORACLE.put(new Integer(Types.TIMESTAMP), "DATE");
        DEFAULT.put(new Integer(Types.TINYINT), "TINYINT");
        DEFAULT.put(new Integer(Types.VARBINARY), "VARBINARY");
        DEFAULT.put(new Integer(Types.VARCHAR), "VARCHAR");
    }

    private static final String getDatabaseType(int type) {
        Integer key = new Integer(type);

        switch (DbHelper.getDatabase()) {
        case DbHelper.DB_POSTGRES:
            if (POSTGRES.containsKey(key)) {
                return (String) POSTGRES.get(key);
            }
            break;
        case DbHelper.DB_ORACLE:
        default:
            if (ORACLE.containsKey(key)) {
                return (String) ORACLE.get(key);
            }
            break;
        }

        String result = (String) DEFAULT.get(key);
        if (result == null) {
            throw new Error("Don't know how to translate " +
                            getTypeName(type) + " to database specific type.");
        }
        return result;
    }

    String getInlineSQL(boolean defer) {
        StringBuffer result = new StringBuffer();

        result.append("    " + m_name + " ");

        if (m_type != Integer.MIN_VALUE) {
            result.append(getDatabaseType(m_type));
        } else {
            result.append("<unknown>");
        }

        if (m_size > -1) {
            result.append("(" + m_size + ")");
        } else if (m_type == Types.VARCHAR) {
            if (hasUniqueKey()) {
                result.append("(700)");
            } else {
                result.append("(4000)");
            }
        }

        if (!m_isNullable) {
            result.append(" not null");
        }

        for (Iterator it = m_constraints.iterator(); it.hasNext(); ) {
            Constraint con = (Constraint) it.next();
            if (con.getColumns().length == 1 && (!defer ||
                                                 !con.isDeferred())) {
                result.append("\n");
                result.append(con.getColumnSQL());
            }
        }

        return result.toString();
    }

    boolean hasPrimaryKey() {
        for (Iterator it = m_constraints.iterator(); it.hasNext(); ) {
            Constraint con = (Constraint) it.next();
            if (con instanceof UniqueKey) {
                UniqueKey uk = (UniqueKey) con;
                if (uk.isPrimaryKey()) {
                    return true;
                }
            }
        }

        return false;
    }

    boolean hasUniqueKey() {
        for (Iterator it = m_constraints.iterator(); it.hasNext(); ) {
            if (it.next() instanceof UniqueKey) {
                return true;
            }
        }

        return false;
    }

    boolean hasDeferredConstraints() {
        for (Iterator it = m_constraints.iterator(); it.hasNext(); ) {
            Constraint con = (Constraint) it.next();
            if (con.getColumns().length == 1) {
                if (con.isDeferred()) {
                    return true;
                }
            }
        }

        return false;
    }

    Object getKey() {
        return getName();
    }

    public String getSQL() {
        return "alter table " + getTable().getName() + " (\n" +
            getInlineSQL(false) + "\n);";
    }

    public String toString() {
        return getTable() + "." + m_name;
    }

}
