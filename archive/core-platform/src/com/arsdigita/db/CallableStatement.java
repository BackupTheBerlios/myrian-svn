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

package com.arsdigita.db;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;


/**
 * A simple implementation of the java.sql.CallableStatement interface
 * that wraps a "real" implementation of java.sql.CallableStatement
 *
 * @author <a href="mailto:mthomas@arsdigita.com">Mark Thomas</a>
 * @version $Revision: #3 $ $Date: 2002/08/14 $
 * @since 4.5
 */
public class CallableStatement extends com.arsdigita.db.PreparedStatement
    implements java.sql.CallableStatement {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/CallableStatement.java#3 $ $Author: dennis $ $Date: 2002/08/14 $";

    // Constructor: use the "wrap" class method to create instances
    private CallableStatement(com.arsdigita.db.Connection conn,
                              String sql, java.sql.CallableStatement stmt) {
        super(conn, sql, stmt);
    }

    /**
     * Gets the value of a JDBC ARRAY parameter as an Array object in
     * the Java programming language.
     */
    public Array getArray(int i) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getArray(i);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Gets the value of a JDBC NUMERIC parameter as a
     * java.math.BigDecimal object with as many digits to the right of
     * the decimal point as the value contains.
     */
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getBigDecimal(parameterIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * @deprecated
     */
    public BigDecimal getBigDecimal(int parameterIndex, int scale)
        throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getBigDecimal(parameterIndex, scale);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Gets the value of a JDBC BLOB parameter as a Blob object in
     * the Java programming language.
     */
    public Blob getBlob(int i) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getBlob(i);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Gets the value of a JDBC BIT parameter as a boolean in the
     * Java programming language.
     */
    public boolean getBoolean(int parameterIndex) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getBoolean(parameterIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Gets the value of a JDBC TINYINT parameter as a byte in the
     * Java programming language.
     */
    public byte getByte(int parameterIndex) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getByte(parameterIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Gets the value of a JDBC BINARY or VARBINARY parameter as an
     * array of byte values in the Java programming language.
     */
    public byte[] getBytes(int parameterIndex) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getBytes(parameterIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Gets the value of a JDBC CLOB parameter as a Clob object in
     * the Java programming language.
     */
    public Clob getClob(int i) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getClob(i);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Gets the value of a JDBC DATE parameter as a java.sql.Date
     * object.
     */
    public Date getDate(int parameterIndex) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getDate(parameterIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Gets the value of a JDBC DATE parameter as a java.sql.Date
     * object, using the given Calendar object to construct the
     * date.
     */
    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getDate(parameterIndex, cal);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Gets the value of a JDBC DOUBLE parameter as a double in the
     * Java programming language.
     */
    public double getDouble(int parameterIndex) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getDouble(parameterIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Gets the value of a JDBC FLOAT parameter as a float in the
     * Java programming language.
     */
    public float getFloat(int parameterIndex) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getFloat(parameterIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Gets the value of a JDBC INTEGER parameter as an int in the
     * Java programming language.
     */
    public int getInt(int parameterIndex) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getInt(parameterIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Gets the value of a JDBC BIGINT parameter as a long in the
     * Java programming language.
     */
    public long getLong(int parameterIndex) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getLong(parameterIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Gets the value of a parameter as an Object in the Java
     * programming language.
     */
    public Object getObject(int parameterIndex) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getObject(parameterIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Returns an object representing the value of OUT parameter i
     * and uses map for the custom mapping of the parameter value.
     */
    public Object getObject(int i, Map map) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getObject(i, map);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Gets the value of a JDBC REF(<structured-type>) parameter as a
     * Ref object in the Java programming language.
     */
    public Ref getRef(int i) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getRef(i);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Gets the value of a JDBC SMALLINT parameter as a short in the
     * Java programming language.
     */
    public short getShort(int parameterIndex) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getShort(parameterIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Retrieves the value of a JDBC CHAR, VARCHAR, or LONGVARCHAR
     * parameter as a String in the Java programming language.
     */
    public String getString(int parameterIndex) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getString(parameterIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Get the value of a JDBC TIME parameter as a java.sql.Time
     * object.
     */
    public Time getTime(int parameterIndex) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getTime(parameterIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Gets the value of a JDBC TIME parameter as a java.sql.Time
     * object, using the given Calendar object to construct the
     * time.
     */
    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getTime(parameterIndex, cal);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Gets the value of a JDBC TIMESTAMP parameter as a
     * java.sql.Timestamp object.
     */
    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getTimestamp(parameterIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Gets the value of a JDBC TIMESTAMP parameter as a
     * java.sql.Timestamp object, using the given Calendar object to
     * construct the Timestamp object.
     */
    public Timestamp getTimestamp(int parameterIndex, Calendar cal)
        throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).getTimestamp(parameterIndex, cal);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Registers the OUT parameter in ordinal position parameterIndex
     * to the JDBC type sqlType.
     */
    public void registerOutParameter(int parameterIndex, int sqlType)
        throws SQLException {
        try {
            if (isCloseAfterUseEnabled()) {
                setCloseAfterUse(false);
            }
            ((java.sql.CallableStatement)m_stmt).registerOutParameter(parameterIndex, sqlType);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Registers the parameter in ordinal position parameterIndex to
     * be of JDBC type sqlType.
     */
    public void registerOutParameter(int parameterIndex, int sqlType,
                                     int scale)
        throws SQLException {
        try {
            ((java.sql.CallableStatement)m_stmt).registerOutParameter(parameterIndex, sqlType, scale);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Registers the designated output parameter.
     */
    public void registerOutParameter(int paramIndex, int sqlType,
                                     String typeName)
        throws SQLException {
        try {
            ((java.sql.CallableStatement)m_stmt).registerOutParameter(paramIndex, sqlType, typeName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Indicates whether or not the last OUT parameter read had the
     * value of SQL NULL.
     */
    public boolean wasNull() throws SQLException {
        try {
            return ((java.sql.CallableStatement)m_stmt).wasNull();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }

    }

    /**
     * Wraps a CallableStatement instance.
     */
    static com.arsdigita.db.CallableStatement wrap(com.arsdigita.db.Connection conn,
                                                   String sql,
                                                   java.sql.CallableStatement stmt) {

        if (null == stmt) {
            return null;
        }
        if (stmt instanceof com.arsdigita.db.CallableStatement) {
            return (com.arsdigita.db.CallableStatement) stmt;
        } else {
            return new com.arsdigita.db.CallableStatement(conn, sql, stmt);
        }

    }

}
