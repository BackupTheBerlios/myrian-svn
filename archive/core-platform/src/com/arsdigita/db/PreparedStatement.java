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

import com.arsdigita.developersupport.DeveloperSupport;

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
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * A simple implementation of the java.sql.CallableStatement interface
 * that wraps a "real" implementation of java.sql.CallableStatement
 *
 * @author <a href="mailto:mthomas@arsdigita.com">Mark Thomas</a>
 * @version $Revision: #6 $ $Date: 2002/11/01 $
 * @since 4.5
 */
public class PreparedStatement extends com.arsdigita.db.Statement
    implements java.sql.PreparedStatement {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/PreparedStatement.java#6 $ $Author: vadim $ $Date: 2002/11/01 $";

    private static final Logger s_cat =
        Logger.getLogger(PreparedStatement.class);

    // the statement object that we wrap
    // is in the base class.  This will lead to a lot
    // of annoying casting, but it is necessary
    // to avoid shadowing and have inheritance work correctly.

    // sql string we are executing, for logging purposes.
    private String m_sql;
    // bind variables value. For logging purpose.
    private HashMap m_bindVars = new HashMap();

    // Constructor: use the "wrap" class method to create instances
    PreparedStatement(com.arsdigita.db.Connection conn,
                      String sql,
                      java.sql.PreparedStatement stmt) {
        super(conn, stmt);
        m_sql = sql;
    }

    // Methods

    /**
     * Adds a set of parameters to this PreparedStatement object's
     * batch of commands.
     */
    public void addBatch() throws SQLException {
        try {
            ((java.sql.PreparedStatement)m_stmt).addBatch();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Clears the current parameter values immediately.
     */
    public void clearParameters() throws SQLException {
        try {
            m_bindVars.clear();
            ((java.sql.PreparedStatement)m_stmt).clearParameters();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * This one just hits the log file, not DS.  It should be called before
     * running the query, and the other logQuery called after running.
     */
    private void logQuery(String type) throws SQLException {
        try {
            if (s_cat.isInfoEnabled()) {
                int conn_id = m_stmt.getConnection().hashCode();
                s_cat.info("Connection["+conn_id+"]::"+type+"::");
                s_cat.info(m_sql);
            }
            if (m_bindVars != null) {
                printBindVars(m_bindVars, false);
            }
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * This one just hits DS, not the log file.  It should be called after
     * running the query.
     */
    private void logQuery(String type, long time) throws SQLException {
        try {
            int conn_id = m_stmt.getConnection().hashCode();
            DeveloperSupport.logQuery(conn_id,
                                      type,
                                      m_sql,
                                      m_bindVars,
                                      time,
                                      null);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Hits log file and DS
     */
    private void logException(String type, SQLException sqle) throws SQLException {
        try {
            int conn_id = m_stmt.getConnection().hashCode();
            s_cat.warn("Connection["+conn_id+"]::"+type+"::");
            s_cat.warn(m_sql);
            if (m_bindVars != null) {
                printBindVars(m_bindVars, true);
            }
            if (sqle != null) {
                s_cat.warn("threw exception: ", sqle);
            }
            DeveloperSupport.logQuery(conn_id,
                                      type,
                                      m_sql,
                                      m_bindVars,
                                      0,
                                      sqle);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    private synchronized void printBindVars(HashMap bindVars, boolean warnlevel) {
        if (s_cat.isInfoEnabled() || warnlevel) {
            StringBuffer strBuf = new StringBuffer();
            Object key[] = bindVars.keySet().toArray();
            strBuf.append("[");
            for (int i = 0; i < key.length; i++) {
                strBuf.append(":").append(key[i]).append(" = ").
                    append(bindVars.get(key[i])).append(",");
            }
            strBuf.append("]");
            if (key.length > 0) {
                if (warnlevel) {
                    s_cat.warn("Bind Vars:: " + strBuf);
                } else {
                    s_cat.info("Bind Vars:: " + strBuf);
                }
            }
        }
    }

    /**
     * Executes any kind of SQL statement.
     */
    public synchronized boolean execute() throws SQLException {
        if (!m_closeAfterUse) {
            return doExecute();
        } else {
            boolean retval = false;
            resetLastUpdateCount();
            try {
                retval = doExecute();
            } catch (SQLException e) {
                close();
                throw e;
            }
            if (!retval) {
                // getUpdateCount will close this statement
                getUpdateCount();
            }
            return retval;
        }
    }

    /**
     * The actual execute; wrapped to implement the closeAfterUse
     * functionality.
     */
    private synchronized boolean doExecute() throws SQLException {
        try {
            logQuery("execute");
            long time = System.currentTimeMillis();
            if (getNeedsAutoCommitOff()) {
                ((com.arsdigita.db.Connection)getConnection()).flagNeedsAutoCommitOff();
            }
            boolean ret = ((java.sql.PreparedStatement)m_stmt).execute();
            time = System.currentTimeMillis() - time;
            logQuery("execute", time);

            return ret;
        } catch (SQLException e) {
            // Log SQLException and rethrow exception.
            logException("execute", e);
            throw m_conn.wrap(e);
        }
    }

    /**
     * Executes the SQL query in this PreparedStatement object and
     * returns the result set generated by the query.
     *
     * This is really a com.arsdigita.db.ResultSet, but the
     * interface doesn't let us change the return type.
     */
    public synchronized java.sql.ResultSet executeQuery() throws SQLException {
        if (!m_closeAfterUse) {
            return doExecuteQuery();
        } else {
            resetLastUpdateCount();
            ResultSet rs;
            try {
                rs = doExecuteQuery();
            } catch (SQLException e) {
                close();
                throw e;
            }
            rs.addResultSetEventListener(this);
            return rs;
        }
    }

    /**
     * The actual executeQuery; wrapped to implement the closeAfterUse
     * functionality.
     */
    private synchronized ResultSet doExecuteQuery() throws SQLException {
        try {
            logQuery("executeQuery");
            long time = System.currentTimeMillis();
            if (getNeedsAutoCommitOff()) {
                ((com.arsdigita.db.Connection)getConnection()).flagNeedsAutoCommitOff();
            }
            ResultSet rs = ResultSet.wrap(this,
                                          ((java.sql.PreparedStatement)m_stmt).executeQuery());
            time = System.currentTimeMillis() - time;
            logQuery("executeQuery", time);
            return rs;
        } catch (SQLException e) {
            // Log SQLException and rethrow exception.
            logException("executeQuery", e);
            throw m_conn.wrap(e);
        }
    }

    /**
     * Executes the SQL INSERT, UPDATE or DELETE statement in this
     * PreparedStatement object.
     */
    public synchronized int executeUpdate() throws SQLException {
        if (!m_closeAfterUse) {
            return doExecuteUpdate();
        } else {
            resetLastUpdateCount();
            try {
                return doExecuteUpdate();
            } finally {
                close();
            }
        }
    }

    /**
     * The actual executeUpdate; wrapped to implement the closeAfterUse
     * functionality.
     */
    private synchronized int doExecuteUpdate() throws SQLException {
        try {
            logQuery("executeUpdate");
            long time = System.currentTimeMillis();
            if (getNeedsAutoCommitOff()) {
                ((com.arsdigita.db.Connection)getConnection()).flagNeedsAutoCommitOff();
            }
            int ret = ((java.sql.PreparedStatement)m_stmt).executeUpdate();
            time = System.currentTimeMillis() - time;
            logQuery("executeUpdate", time);
            return ret;
        } catch (SQLException e) {
            // Log SQLException and rethrow exception.
            logException("executeQuery", e);
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gets the number, types and properties of a ResultSet object's
     * columns.
     */
    public synchronized ResultSetMetaData getMetaData() throws SQLException {
        try {
            return ((java.sql.PreparedStatement)m_stmt).getMetaData();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to the given Array object.
     */
    public synchronized void setArray(int i, Array x) throws SQLException {
        try {
            m_bindVars.put(new Integer(i), x);
            ((java.sql.PreparedStatement)m_stmt).setArray(i, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to the given input stream, which
     * will have the specified number of bytes.
     */
    public synchronized void setAsciiStream(int parameterIndex, InputStream x, int length)
        throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setAsciiStream(parameterIndex, x, length);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to a java.math.BigDecimal
     * value.
     */
    public synchronized void setBigDecimal(int parameterIndex, BigDecimal x)
        throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setBigDecimal(parameterIndex, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to the given input stream, which
     * will have the specified number of bytes.
     */
    public synchronized void setBinaryStream(int parameterIndex, InputStream x, int length)
        throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setBinaryStream(parameterIndex, x, length);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to the given Blob object.
     */
    public synchronized void setBlob(int i, Blob x) throws SQLException {
        try {
            m_bindVars.put(new Integer(i), x);
            ((java.sql.PreparedStatement)m_stmt).setBlob(i, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to a Java boolean value.
     */
    public synchronized void setBoolean(int parameterIndex, boolean x) throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), new Boolean(x));
            ((java.sql.PreparedStatement)m_stmt).setBoolean(parameterIndex, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to a Java byte value.
     */
    public synchronized void setByte(int parameterIndex, byte x) throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), new Byte(x));
            ((java.sql.PreparedStatement)m_stmt).setByte(parameterIndex, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to a Java array of bytes.
     */
    public synchronized void setBytes(int parameterIndex, byte[] x) throws SQLException {
        try {
            //m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setBytes(parameterIndex, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to the given Reader object,
     * which is the given number of characters long.
     */
    public synchronized void setCharacterStream(int parameterIndex, Reader reader,
                                                int length)
        throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), reader);
            ((java.sql.PreparedStatement)m_stmt).setCharacterStream(parameterIndex, reader, length);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to the given Clob object.
     */
    public synchronized void setClob(int i, Clob x) throws SQLException {
        try {
            m_bindVars.put(new Integer(i), x);
            ((java.sql.PreparedStatement)m_stmt).setClob(i, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to a value.
     */
    public synchronized void setDate(int parameterIndex, Date x) throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setDate(parameterIndex, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to the given java.sql.Date
     * value, using the given Calendar object.
     */
    public synchronized void setDate(int parameterIndex, Date x, Calendar cal)
        throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setDate(parameterIndex, x, cal);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to a Java double value.
     */
    public synchronized void setDouble(int parameterIndex, double x) throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), new Double(x));
            ((java.sql.PreparedStatement)m_stmt).setDouble(parameterIndex, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to a Java float value.
     */
    public synchronized void setFloat(int parameterIndex, float x) throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), new Float(x));
            ((java.sql.PreparedStatement)m_stmt).setFloat(parameterIndex, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to a Java int value.
     */
    public synchronized void setInt(int parameterIndex, int x) throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), new Integer(x));
            ((java.sql.PreparedStatement)m_stmt).setInt(parameterIndex, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to a Java long value.
     */
    public synchronized void setLong(int parameterIndex, long x) throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), new Long(x));
            ((java.sql.PreparedStatement)m_stmt).setLong(parameterIndex, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to SQL NULL.
     */
    public synchronized void setNull(int parameterIndex, int sqlType) throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), "NULL");
            ((java.sql.PreparedStatement)m_stmt).setNull(parameterIndex, sqlType);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to SQL NULL.
     */
    public synchronized void setNull(int paramIndex, int sqlType, String typeName)
        throws SQLException {
        try {
            m_bindVars.put(new Integer(paramIndex), "NULL");
            ((java.sql.PreparedStatement)m_stmt).setNull(paramIndex, sqlType, typeName);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the value of the designated parameter using the given
     * object.
     */
    public synchronized void setObject(int parameterIndex, Object x) throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setObject(parameterIndex, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the value of the designated parameter with the given
     * object.
     */
    public synchronized void setObject(int parameterIndex, Object x, int targetSqlType)
        throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setObject(parameterIndex, x, targetSqlType);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the value of the designated parameter with the given
     * object.
     */
    public synchronized void setObject(int parameterIndex, Object x, int targetSqlType,
                                       int scale)
        throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setObject(parameterIndex, x, targetSqlType, scale);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to the given
     * REF(<structured-type>) value.
     */
    public synchronized void setRef(int i, Ref x) throws SQLException {
        try {
            m_bindVars.put(new Integer(i), x);
            ((java.sql.PreparedStatement)m_stmt).setRef(i, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     *  Sets the designated parameter to a Java short value.
     */
    public synchronized void setShort(int parameterIndex, short x) throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), new Short(x));
            ((java.sql.PreparedStatement)m_stmt).setShort(parameterIndex, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to a Java String value.
     */
    public synchronized void setString(int parameterIndex, String x) throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setString(parameterIndex, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to a java.sql.Time value.
     */
    public synchronized void setTime(int parameterIndex, Time x) throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setTime(parameterIndex, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to the given java.sql.Time
     * value, using the given Calendar object.
     */
    public synchronized void setTime(int parameterIndex, Time x, Calendar cal)
        throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setTime(parameterIndex, x, cal);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to a java.sql.Timestamp
     * value.
     */
    public synchronized void setTimestamp(int parameterIndex, Timestamp x)
        throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setTimestamp(parameterIndex, x);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the designated parameter to the given java.sql.Timestamp
     * value, using the given Calendar object.
     */
    public synchronized void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
        throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setTimestamp(parameterIndex, x, cal);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * @deprecated
     */
    public synchronized void setUnicodeStream(int parameterIndex, InputStream x,
                                              int length)
        throws SQLException {
        try {
            m_bindVars.put(new Integer(parameterIndex), x);
            ((java.sql.PreparedStatement)m_stmt).setUnicodeStream(parameterIndex, x, length);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }


    /**
     * Wraps a PreparedStatement instance.
     */
    static com.arsdigita.db.PreparedStatement wrap(com.arsdigita.db.Connection conn,
                                                   String sql,
                                                   java.sql.PreparedStatement stmt) {
        if (null == stmt) {
            return null;
        }
        if (stmt instanceof com.arsdigita.db.PreparedStatement) {
            return (com.arsdigita.db.PreparedStatement) stmt;
        } else {
            return new com.arsdigita.db.PreparedStatement(conn, sql, stmt);
        }
    }
}
