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

package com.arsdigita.db;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import org.apache.log4j.Category;

/**
 * A simple implementation of the java.sql.ResultSet interface that
 * wraps a "real" implementation of java.sql.ResultSet
 *
 * <ul><lh>aD specific features:</lh>
 * <li>Supports notifying listeners when the ResultSet is closed,
 * thereby enabling close-after-use Statements.</li>
 * </ul>
 * 
 * @author <a href="mailto:mthomas@arsdigita.com">Mark Thomas</a>
 * @version $Revision: #2 $ $Date: 2002/07/18 $
 * @since 4.5
 */
public class ResultSet implements java.sql.ResultSet {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/ResultSet.java#2 $ $Author: dennis $ $Date: 2002/07/18 $";

    private static Category s_cat = Category.getInstance(com.arsdigita.db.ResultSet.class.getName());

    private static final java.util.Set dbgResultSets = new java.util.HashSet();

    // the ResultSet object that we wrap
    private java.sql.ResultSet m_rset;

    // the AdStatement object that called our constructor
    private com.arsdigita.db.Statement m_stmt;

    private java.util.List eventListeners = new java.util.LinkedList();

    // Constructor: use the "wrap" class method to create instances
    ResultSet(com.arsdigita.db.Statement stmt, java.sql.ResultSet rset) {
        m_stmt = stmt;
        m_rset = rset;
        if (s_cat.isDebugEnabled()) {
            synchronized (dbgResultSets) {
                dbgResultSets.add(rset);
                s_cat.debug("ResultSet constructor: created " + this + ", " + 
                            "ResultSet count is now " + dbgResultSets.size(), 
                            new Throwable("Stack trace"));
            }
        }
    }

    /** 
     * Moves the cursor to the given row number in this ResultSet 
     * object. 
     */
    public synchronized boolean absolute(int row) throws SQLException {
        try {
            return m_rset.absolute(row);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Moves the cursor to the end of this ResultSet object, just
     * after the last row. 
     */
    public synchronized void afterLast() throws SQLException {
        try {
            m_rset.afterLast();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Moves the cursor to the front of this ResultSet object, just
     * before the first row. 
     */
    public synchronized void beforeFirst() throws SQLException {
        try {
            m_rset.beforeFirst();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Cancels the updates made to the current row in this ResultSet
     * object. 
     */
    public synchronized void cancelRowUpdates() throws SQLException {
        try {
            m_rset.cancelRowUpdates();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Clears all warnings reported on this ResultSet object. 
     */
    public synchronized void clearWarnings() throws SQLException {
        try {
            m_rset.clearWarnings();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Releases this ResultSet object's database and JDBC resources
     * immediately instead of waiting for this to happen when it is
     * automatically closed. 
     *
     * NOTE that this may be run by a finalizer thread, so any
     * listeners with side effects should be threadsafe or 
     * guarantee that they will not be needed while this resultset
     * is being closed (e.g. a close-after-use statement could not be
     * used while this resultset was being closed, so it doesn't
     * need to synchronize when responding to this close.  The 
     * connection, however, does need to synchronize, since
     * there is no guarantee that it won't be used at the same
     * time as the finalizer).
     */
    public synchronized void close() throws SQLException {
        try {
            m_rset.close();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
        fireClosedListeners();
        if (s_cat.isDebugEnabled()) {
            synchronized (dbgResultSets) {
                if (!dbgResultSets.contains(m_rset)) {
                    s_cat.warn("ResultSet close: closing resultset that " +
                               "was never opened or has already " +
                               "been closed: " + m_rset, 
                               new Throwable("Stack Trace"));
                } else {
                    dbgResultSets.remove(m_rset);
                }

                s_cat.debug("ResultSet close: ResultSet count is now " + 
                           dbgResultSets.size());
            }
        }
        m_rset = null;
    }

    protected synchronized void finalize() throws Throwable {
        try {
            if (m_rset != null) {
                s_cat.warn("ResultSet was not closed by programmer: " + this + 
                           ", closing in garbage collection.  Lots of these " + 
                           "messages can indicate the cause of an out " +
                           "of cursors error.");
                try {
                    close();
                } catch (SQLException e) {
                    s_cat.error(null, e);
                }
            }
        } finally {
            super.finalize();
        }
    }

    /** 
     * Deletes the current row from this ResultSet object and from
     * the underlying database. 
     */
    public synchronized void deleteRow() throws SQLException {
        try {
            m_rset.deleteRow();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Maps the given ResultSet column name to its ResultSet column
     * index. 
     */
    public synchronized int findColumn(String columnName) throws SQLException {
        try {
            return m_rset.findColumn(columnName);
        } catch (SQLException e) {
            s_cat.warn("Could not retrieve the column named: \"" +
                       columnName + "\"");
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Moves the cursor to the first row in this ResultSet object. 
     */
    public synchronized boolean first() throws SQLException {
        try {
            return m_rset.first();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Returns the value of the designated column in the current row
     * of this ResultSet object as an Array object in the Java
     * programming language. 
     */
    public synchronized Array getArray(int i) throws SQLException {
        try {
            return m_rset.getArray(i);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Returns the value of the designated column in the current row
     * of this ResultSet object as an Array object in the Java
     * programming language. 
     */
    public synchronized Array getArray(String colName) throws SQLException {
        try {
            return m_rset.getArray(colName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a stream of ASCII characters. 
     */
    public synchronized InputStream getAsciiStream(int columnIndex) throws SQLException {
        try {
            return m_rset.getAsciiStream(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a stream of ASCII characters. 
     */
    public synchronized InputStream getAsciiStream(String columnName) throws SQLException {
        try {
            return m_rset.getAsciiStream(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a java.math.BigDecimal with full
     * precision. 
     */
    public synchronized BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        try {
            return m_rset.getBigDecimal(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * @deprecated
     */
    public synchronized BigDecimal getBigDecimal(int columnIndex, int scale)
            throws SQLException {
        try {
            return m_rset.getBigDecimal(columnIndex, scale);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a java.math.BigDecimal with full
     * precision. 
     */
    public synchronized BigDecimal getBigDecimal(String columnName) throws SQLException {
        try {
            return m_rset.getBigDecimal(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * @deprecated
     */
    public synchronized BigDecimal getBigDecimal(String columnName, int scale)
            throws SQLException {
        try {
            return m_rset.getBigDecimal(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of a column in the current row as a stream of
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a binary stream of uninterpreted
     * bytes. 
     */
    public synchronized InputStream getBinaryStream(int columnIndex) throws SQLException {
        try {
            return m_rset.getBinaryStream(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a stream of uninterpreted bytes. 
     */
    public synchronized InputStream getBinaryStream(String columnName) throws SQLException {
        try {
            return m_rset.getBinaryStream(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Returns the value of the designated column in the current row
     * of this ResultSet object as a Blob object in the Java
     * programming language. 
     */
    public synchronized Blob getBlob(int i) throws SQLException {
        try {
            return m_rset.getBlob(i);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Returns the value of the designated column in the current row
     * of this ResultSet object as a Blob object in the Java
     * programming language. 
     */
    public synchronized Blob getBlob(String colName) throws SQLException {
        try {
            return m_rset.getBlob(colName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a boolean in the Java programming
     * language. 
     */
    public synchronized boolean getBoolean(int columnIndex) throws SQLException {
        try {
            return m_rset.getBoolean(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a boolean in the Java programming
     * language. 
     */
    public synchronized boolean getBoolean(String columnName) throws SQLException {
        try {
            return m_rset.getBoolean(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a byte in the Java programming
     * language. 
     */
    public synchronized byte getByte(int columnIndex) throws SQLException {
        try {
            return m_rset.getByte(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a byte in the Java programming
     * language. 
     */
    public synchronized byte getByte(String columnName) throws SQLException {
        try {
            return m_rset.getByte(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a byte array in the Java programming
     * language. 
     */
    public synchronized byte[] getBytes(int columnIndex) throws SQLException {
        try {
            return m_rset.getBytes(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a byte array in the Java programming
     * language. 
     */
    public synchronized byte[] getBytes(String columnName) throws SQLException {
        try {
            return m_rset.getBytes(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a java.io.Reader object. 
     */
    public synchronized Reader getCharacterStream(int columnIndex) throws SQLException {
        try {
            return m_rset.getCharacterStream(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a java.io.Reader object. 
     */
    public synchronized Reader getCharacterStream(String columnName) throws SQLException {
        try {
            return m_rset.getCharacterStream(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Returns the value of the designated column in the current row
     * of this ResultSet object as a Clob object in the Java
     * programming language. 
     */
    public synchronized Clob getClob(int i) throws SQLException {
        try {
            return m_rset.getClob(i);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Returns the value of the designated column in the current row
     * of this ResultSet object as a Clob object in the Java
     * programming language. 
     */
    public synchronized Clob getClob(String colName) throws SQLException {
        try {
            return m_rset.getClob(colName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Returns the concurrency mode of this ResultSet object. 
     */
    public synchronized int getConcurrency() throws SQLException {
        try {
            return m_rset.getConcurrency();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the name of the SQL cursor used by this ResultSet
     * object. 
     */
    public synchronized String getCursorName() throws SQLException {
        try {
            return m_rset.getCursorName();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a java.sql.Date object in the Java
     * programming language. 
     */
    public synchronized Date getDate(int columnIndex) throws SQLException {
        try {
            return m_rset.getDate(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Returns the value of the designated column in the current row
     * of this ResultSet object as a java.sql.Date object in the Java
     * programming language. 
     */
    public synchronized Date getDate(int columnIndex, Calendar cal) throws SQLException {
        try {
            return m_rset.getDate(columnIndex, cal);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a java.sql.Date object in the Java
     * programming language. 
     */
    public synchronized Date getDate(String columnName) throws SQLException {
        try {
            return m_rset.getDate(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Returns the value of the designated column in the current row
     * of this ResultSet object as a java.sql.Date object in the Java
     * programming language. 
     */
    public synchronized Date getDate(String columnName, Calendar cal) throws SQLException {
        try {
            return m_rset.getDate(columnName, cal);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a double in the Java programming
     * language. 
     */
    public synchronized double getDouble(int columnIndex) throws SQLException {
        try {
            return m_rset.getDouble(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a double in the Java programming
     * language. 
     */
    public synchronized double getDouble(String columnName) throws SQLException {
        try {
            return m_rset.getDouble(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Returns the fetch direction for this ResultSet object. 
     */
    public synchronized int getFetchDirection() throws SQLException {
        try {
            return m_rset.getFetchDirection();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Returns the fetch size for this ResultSet object. 
     */
    public synchronized int getFetchSize() throws SQLException {
        try {
            return m_rset.getFetchSize();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a float in the Java programming
     * language. 
     */
    public synchronized float getFloat(int columnIndex) throws SQLException {
        try {
            return m_rset.getFloat(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a float in the Java programming
     * language. 
     */
    public synchronized float getFloat(String columnName) throws SQLException {
        try {
            return m_rset.getFloat(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as an int in the Java programming
     * language. 
     */
    public synchronized int getInt(int columnIndex) throws SQLException {
        try {
            return m_rset.getInt(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as an int in the Java programming
     * language. 
     */
    public synchronized int getInt(String columnName) throws SQLException {
        try {
            return m_rset.getInt(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a long in the Java programming
     * language. 
     */
    public synchronized long getLong(int columnIndex) throws SQLException {
        try {
            return m_rset.getLong(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a long in the Java programming
     * language. 
     */
    public synchronized long getLong(String columnName) throws SQLException {
        try {
            return m_rset.getLong(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Retrieves the number, types and properties of this ResultSet
     * object's columns. 
     */
    public synchronized ResultSetMetaData getMetaData() throws SQLException {
        try {
            return m_rset.getMetaData();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Gets the value of the designated column in the current row of
     * this ResultSet object as an Object in the Java programming
     * language. 
     */
    public synchronized Object getObject(int columnIndex) throws SQLException {
        try {
            return m_rset.getObject(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Returns the value of the designated column in the current row
     * of this ResultSet object as an Object in the Java programming
     * language. 
     */
    public synchronized Object getObject(int i, Map map) throws SQLException {
        try {
            return m_rset.getObject(i, map);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Gets the value of the designated column in the current row of
     * this ResultSet object as an Object in the Java programming
     * language. 
     */
    public synchronized Object getObject(String columnName) throws SQLException {
        try {
            return m_rset.getObject(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Returns the value of the designated column in the current row
     * of this ResultSet object as an Object in the Java programming
     * language. 
     */
    public synchronized Object getObject(String colName, Map map) throws SQLException {
        try {
            return m_rset.getObject(colName, map);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Returns the value of the designated column in the current row
     * of this ResultSet object as a Ref object in the Java
     * programming language. 
     */
    public synchronized Ref getRef(int i) throws SQLException {
        try {
            return m_rset.getRef(i);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Returns the value of the designated column in the current row
     * of this ResultSet object as a Ref object in the Java
     * programming language. 
     */
    public synchronized Ref getRef(String colName) throws SQLException {
        try {
            return m_rset.getRef(colName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** Retrieves the current row number. */
    public synchronized int getRow() throws SQLException {
        try {
            return m_rset.getRow();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a short in the Java programming
     * language. 
     */
    public synchronized short getShort(int columnIndex) throws SQLException {
        try {
            return m_rset.getShort(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a short in the Java programming
     * language. 
     */
    public synchronized short getShort(String columnName) throws SQLException {
        try {
            return m_rset.getShort(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Returns the Statement object that produced this ResultSet
     * object. 
     */
    public java.sql.Statement getStatement() throws SQLException {
        return m_stmt;
    }

    /**
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a String in the Java programming
     * language. 
     */
    public synchronized String getString(int columnIndex) throws SQLException {
        try {
            return m_rset.getString(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a String in the Java programming
     * language. 
     */
    public synchronized String getString(String columnName) throws SQLException {
        try {
            return m_rset.getString(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a java.sql.Time object in the Java
     * programming language. 
     */
    public synchronized Time getTime(int columnIndex) throws SQLException {
        try {
            return m_rset.getTime(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Returns the value of the designated column in the current row
     * of this ResultSet object as a java.sql.Time object in the Java
     * programming language. 
     */
    public synchronized Time getTime(int columnIndex, Calendar cal) throws SQLException {
        try {
            return m_rset.getTime(columnIndex, cal);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a java.sql.Time object in the Java
     * programming language. 
     */	   
    public synchronized Time getTime(String columnName) throws SQLException {
        try {
            return m_rset.getTime(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Returns the value of the designated column in the current row
     * of this ResultSet object as a java.sql.Time object in the Java
     * programming language. 
     */
    public synchronized Time getTime(String columnName, Calendar cal) throws SQLException {
        try {
            return m_rset.getTime(columnName, cal);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a java.sql.Timestamp object in the
     * Java programming language. 
     */
    public synchronized Timestamp getTimestamp(int columnIndex) throws SQLException {
        try {
            return m_rset.getTimestamp(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Returns the value of the designated column in the current row
     * of this ResultSet object as a java.sql.Timestamp object in the
     * Java programming language.
     */
    public synchronized Timestamp getTimestamp(int columnIndex, Calendar cal)
        throws SQLException
    {
        try {
            return m_rset.getTimestamp(columnIndex, cal);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Gets the value of the designated column in the current row of
     * this ResultSet object as a java.sql.Timestamp object. 
     */
    public synchronized Timestamp getTimestamp(String columnName) throws SQLException {
        try {
            return m_rset.getTimestamp(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Returns the value of the designated column in the current row
     * of this ResultSet object as a java.sql.Timestamp object in the
     * Java programming language. 
     */
    public synchronized Timestamp getTimestamp(String columnName, Calendar cal)
        throws SQLException
    {
        try {
            return m_rset.getTimestamp(columnName, cal);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /** 
     * Returns the type of this ResultSet object. 
     */
    public synchronized int getType() throws SQLException {
        try {
            return m_rset.getType();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * @deprecated Use getCharacterStream in place of getUnicodeStream 
     */
    public synchronized InputStream getUnicodeStream(int columnIndex) throws SQLException {
        try {
            return m_rset.getUnicodeStream(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * @deprecated
     */
    public synchronized InputStream getUnicodeStream(String columnName)
            throws SQLException {
        try {
            return m_rset.getUnicodeStream(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Returns the first warning reported by calls on this ResultSet
     * object.
     */
    public synchronized SQLWarning getWarnings() throws SQLException {
        try {
            return m_rset.getWarnings();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Inserts the contents of the insert row into this ResultSet
     * object and into the database. 
     */
    public synchronized void insertRow() throws SQLException {
        try {
            m_rset.insertRow();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Indicates whether the cursor is after the last row in this
     * ResultSet object. 
     */
    public synchronized boolean isAfterLast() throws SQLException {
        try {
            return m_rset.isAfterLast();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  //code should never get here, but just in case 
        }
    }

    /**
     * Indicates whether the cursor is before the first row in this
     * ResultSet object.
     */
    public synchronized boolean isBeforeFirst() throws SQLException {
        try {
            return m_rset.isBeforeFirst();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Indicates whether the cursor is on the first row of this
     * ResultSet object. 
     */
    public synchronized boolean isFirst() throws SQLException {
        try {
            return m_rset.isFirst();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Indicates whether the cursor is on the last row of this
     * ResultSet object. 
     */
    public synchronized boolean isLast() throws SQLException {
        try {
            return m_rset.isLast();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Moves the cursor to the last row in this ResultSet object. 
     */
    public synchronized boolean last() throws SQLException {
        try {
            return m_rset.last();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Moves the cursor to the remembered cursor position, usually
     * the current row. 
     */
    public synchronized void moveToCurrentRow() throws SQLException {
        try {
            m_rset.moveToCurrentRow();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Moves the cursor to the insert row. 
     */
    public synchronized void moveToInsertRow() throws SQLException {
        try {
            m_rset.moveToInsertRow();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Moves the cursor down one row from its current position. 
     */
    public synchronized boolean next() throws SQLException {
        try {
            return m_rset.next();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Moves the cursor to the previous row in this ResultSet
     * object. 
     */
    public synchronized boolean previous() throws SQLException {
        try {
            return m_rset.previous();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Refreshes the current row with its most recent value in the
     * database. 
     */
    public synchronized void refreshRow() throws SQLException {
        try {
            m_rset.refreshRow();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Moves the cursor a relative number of rows, either positive or
     * negative. 
     */
    public synchronized boolean relative(int rows) throws SQLException {
        try {
            return m_rset.relative(rows);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Indicates whether a row has been deleted. 
     */
    public synchronized boolean rowDeleted() throws SQLException {
        try {
            return m_rset.rowDeleted();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Indicates whether the current row has had an insertion. 
     */
    public synchronized boolean rowInserted() throws SQLException {
        try {
            return m_rset.rowInserted();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Indicates whether the current row has been updated. 
     */
    public synchronized boolean rowUpdated() throws SQLException {
        try {
            return m_rset.rowUpdated();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Gives a hint as to the direction in which the rows in this
     * ResultSet object will be processed. 
     */
    public synchronized void setFetchDirection(int direction) throws SQLException {
        try {
            m_rset.setFetchDirection(direction);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Gives the JDBC driver a hint as to the number of rows that
     * should be fetched from the database when more rows are needed
     * for this ResultSet object. 
     */
    public synchronized void setFetchSize(int rows) throws SQLException {
        try {
            m_rset.setFetchSize(rows);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with an ascii stream value. 
     */
    public synchronized void updateAsciiStream(int columnIndex, InputStream x, int length)
            throws SQLException {
        try {
            m_rset.updateAsciiStream(columnIndex, x, length);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with an ascii stream value. 
     */
    public synchronized void updateAsciiStream(String columnName, InputStream x, int length)
            throws SQLException {
        try {
            m_rset.updateAsciiStream(columnName, x, length);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a java.math.BigDecimal
     * value.
     */
    public synchronized void updateBigDecimal(int columnIndex, BigDecimal x)
            throws SQLException {
        try {
            m_rset.updateBigDecimal(columnIndex, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a java.sql.BigDecimal
     * value. 
     */
    public synchronized void updateBigDecimal(String columnName, BigDecimal x)
            throws SQLException {
        try {
            m_rset.updateBigDecimal(columnName, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a binary stream value. 
     */
    public synchronized void updateBinaryStream(int columnIndex, InputStream x, int length)
            throws SQLException {
        try {
            m_rset.updateBinaryStream(columnIndex, x, length);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**  
     * Updates the designated column with a binary stream value. 
     */
    public synchronized void updateBinaryStream(String columnName, InputStream x,int length)
            throws SQLException {
        try {
            m_rset.updateBinaryStream(columnName, x, length);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a boolean value. 
     */
    public synchronized void updateBoolean(int columnIndex, boolean x) throws SQLException {
        try {
            m_rset.updateBoolean(columnIndex, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a boolean value. 
     */
    public synchronized void updateBoolean(String columnName, boolean x)
            throws SQLException {
        try {
            m_rset.updateBoolean(columnName, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a byte value. 
     */
    public synchronized void updateByte(int columnIndex, byte x) throws SQLException {
        try {
            m_rset.updateByte(columnIndex, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a byte value. 
     */
    public synchronized void updateByte(String columnName, byte x) throws SQLException {
        try {
            m_rset.updateByte(columnName, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a byte array value. 
     */
    public synchronized void updateBytes(int columnIndex, byte[] x) throws SQLException {
        try {
            m_rset.updateBytes(columnIndex, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a boolean value. 
     */
    public synchronized void updateBytes(String columnName, byte[] x) throws SQLException {
        try {
            m_rset.updateBytes(columnName, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a character stream
     * value. 
     */
    public synchronized void updateCharacterStream(int columnIndex, Reader x, int length)
            throws SQLException {
        try {
            m_rset.updateCharacterStream(columnIndex, x, length);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a character stream
     * value. 
     */
    public synchronized void updateCharacterStream(String columnName, Reader reader,
                                      int length) 
            throws SQLException {
        try {
            m_rset.updateCharacterStream(columnName, reader, length);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /** 
     * Updates the designated column with a java.sql.Date value. 
     */
    public synchronized void updateDate(int columnIndex, Date x) throws SQLException {
        try {
            m_rset.updateDate(columnIndex, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a java.sql.Date value. 
     */
    public synchronized void updateDate(String columnName, Date x) throws SQLException {
        try {
            m_rset.updateDate(columnName, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a double value. 
     */
    public synchronized void updateDouble(int columnIndex, double x) throws SQLException {
        try {
            m_rset.updateDouble(columnIndex, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a double value. 
     */
    public synchronized void updateDouble(String columnName, double x) throws SQLException {
        try {
            m_rset.updateDouble(columnName, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a float value. 
     */
    public synchronized void updateFloat(int columnIndex, float x) throws SQLException {
        try {
            m_rset.updateFloat(columnIndex, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a float value. 
     */
    public synchronized void updateFloat(String columnName, float x) throws SQLException {
        try {
            m_rset.updateFloat(columnName, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with an int value. 
     */
    public synchronized void updateInt(int columnIndex, int x) throws SQLException {
        try {
            m_rset.updateInt(columnIndex, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with an int value. 
     */
    public synchronized void updateInt(String columnName, int x) throws SQLException {
        try {
            m_rset.updateInt(columnName, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a long value. 
     */
    public synchronized void updateLong(int columnIndex, long x) throws SQLException {
        try {
            m_rset.updateLong(columnIndex, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a long value. 
     */
    public synchronized void updateLong(String columnName, long x) throws SQLException {
        try {
            m_rset.updateLong(columnName, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Gives a nullable column a null value. 
     */
    public synchronized void updateNull(int columnIndex) throws SQLException {
        try {
            m_rset.updateNull(columnIndex);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a null value. 
     */
    public synchronized void updateNull(String columnName) throws SQLException {
        try {
            m_rset.updateNull(columnName);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with an Object value. 
     */
    public synchronized void updateObject(int columnIndex, Object x) throws SQLException {
        try {
            m_rset.updateObject(columnIndex, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with an Object value. 
     */
    public synchronized void updateObject(int columnIndex, Object x, int scale)
            throws SQLException {
        try {
            m_rset.updateObject(columnIndex, x, scale);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with an Object value. 
     */
    public synchronized void updateObject(String columnName, Object x) throws SQLException {
        try {
            m_rset.updateObject(columnName, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with an Object value. 
     */
    public synchronized void updateObject(String columnName, Object x, int scale)
            throws SQLException {
        try {
            m_rset.updateObject(columnName, x, scale);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the underlying database with the new contents of the
     * current row of this ResultSet object. 
     */
    public synchronized void updateRow() throws SQLException {
        try {
            m_rset.updateRow();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a short value. 
     */
    public synchronized void updateShort(int columnIndex, short x) throws SQLException {
        try {
            m_rset.updateShort(columnIndex, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a short value. 
     */
    public synchronized void updateShort(String columnName, short x) throws SQLException {
        try {
            m_rset.updateShort(columnName, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a String value. 
     */
    public synchronized void updateString(int columnIndex, String x) throws SQLException {
        try {
            m_rset.updateString(columnIndex, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a String value. 
     */
    public synchronized void updateString(String columnName, String x) throws SQLException {
        try {
            m_rset.updateString(columnName, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a java.sql.Time value. 
     */
    public synchronized void updateTime(int columnIndex, Time x) throws SQLException {
        try {
            m_rset.updateTime(columnIndex, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a java.sql.Time value. 
     */
    public synchronized void updateTime(String columnName, Time x) throws SQLException {
        try {
            m_rset.updateTime(columnName, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a java.sql.Timestamp
     * value. 
     */
    public synchronized void updateTimestamp(int columnIndex, Timestamp x)
            throws SQLException {
        try {
            m_rset.updateTimestamp(columnIndex, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Updates the designated column with a java.sql.Timestamp
     * value. 
     */
    public synchronized void updateTimestamp(String columnName, Timestamp x)
            throws SQLException {
        try {
            m_rset.updateTimestamp(columnName, x);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Reports whether the last column read had a value of SQL
     * NULL. 
     */
    public synchronized boolean wasNull() throws SQLException {
        try {
            return m_rset.wasNull();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Wraps a ResultSet instance.
     */
    static synchronized ResultSet wrap(com.arsdigita.db.Connection conn,
                          java.sql.ResultSet rset)
            throws SQLException {
        return wrap(conn, null, rset);
    }

    /** 
     * Wraps a ResultSet instance.
     */
    static synchronized ResultSet wrap(com.arsdigita.db.Statement stmt,
                          java.sql.ResultSet rset)
            throws SQLException {
        return wrap(null, stmt, rset);
    }

    /** 
     * Wraps a ResultSet instance.  Work
     * routine for previous package methods.
     * */
    private synchronized static ResultSet wrap(com.arsdigita.db.Connection conn,
                                  com.arsdigita.db.Statement stmt,
                                  java.sql.ResultSet rset)
            throws SQLException {
        try {
            if (null == rset) {
                return null;
            }
            if (null == stmt) {
                stmt = Statement.wrap(conn, rset.getStatement());
            }
            if (rset instanceof com.arsdigita.db.ResultSet) {
                return (com.arsdigita.db.ResultSet) rset;
            } else {
                return new com.arsdigita.db.ResultSet(stmt, rset);
            }
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * NOTE that the listeners may be called by a finalizer thread, so any
     * listeners with side effects should be threadsafe or 
     * guarantee that they will not be needed while this resultset
     * is being closed.
     */
    public void addResultSetEventListener(com.arsdigita.db.ResultSetEventListener l) {
        eventListeners.add(l);
    }

    public void removeResultSetEventListener(com.arsdigita.db.ResultSetEventListener l) {
        eventListeners.remove(l);
    }

    protected void fireClosedListeners() throws java.sql.SQLException {
        try {
            java.util.Iterator i = eventListeners.iterator();
            ResultSetEvent event = new ResultSetEvent(this);
            while (i.hasNext()) {
                ResultSetEventListener l = ((ResultSetEventListener)i.next());
                l.resultSetClosed(event);
            }
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }
}
