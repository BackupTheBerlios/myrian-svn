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
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * A simple implementation of the java.sql.Statement interface that
 * wraps a "real" implementation of java.sql.Statement
 *
 * <ul><lh>aD specific features:</lh>
 * <li>This implementation has been extended to support "closeAfterUse",
 * where the statement will be closed automatically when it is
 * no longer needed.<P>
 * As an optimization to enhance ability to close statements, it is
 * assumed that only one result will be returned from any execute
 * (one resultset or one update count) when closeAfterUse is enabled.
 * Thus, the statement will be closed if execute returns false, and
 * getMoreResults will throw an IllegalStateException.
 * If the boolean variant of execute returns true,
 * it is necessary to call getResultSet, and the statement will
 * be closed when that result set is closed.
 * closeAfterUse must not be used (and the statement must be
 * explicitly closed by the caller) if multiple results are needed.
 * <P>
 * Note that all subclasses should ensure that they implement
 * closeAfterUse correctly.</li>
 * <li>Supports tracking whether this statement
 * needs autocommit off or not (a statement that was used for
 * modifying data should cause its Connection  to not be aggressively closed,
 * as transaction-like behavior would be lost, so it needs autocommit
 * off)</li>
 * </ul>
 *
 * @author <a href="mailto:mthomas@arsdigita.com">Mark Thomas</a>
 * @version $Revision: #5 $ $Date: 2002/10/04 $
 * @since 4.5
 */
public class Statement implements java.sql.Statement, ResultSetEventListener {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/Statement.java#5 $ $Author: rhs $ $Date: 2002/10/04 $";

    private static final java.util.Set dbgStatements = new java.util.HashSet();

    private static final Logger s_cat = Logger.getLogger(com.arsdigita.db.Statement.class.getName());

    // the statement object that we wrap
    protected java.sql.Statement m_stmt;

    // the connection object that created this Statement object
    protected com.arsdigita.db.Connection m_conn;

    private ArrayList sql_list = new ArrayList();

    // Tracks whether "closeAfterUse" is enabled.
    protected boolean m_closeAfterUse = false;

    // Tracks last update count
    // used to support "closeAfterUse" functionality
    private int m_lastUpdateCount = -1;

    // Tracks whether lastUpdateCount is valid
    // used to support "closeAfterUse" functionality
    private boolean m_lastUpdateCountCurrent = false;

    // Tracks whether this statement needs autocommit to
    // be off.  Should be explicitly set by caller; defaults
    // to true because that's safer if caller does not indicate
    // their requirement.
    private boolean m_needsAutoCommitOff = true;

    // Constructor: use the "wrap" class method to create instances
    protected Statement(com.arsdigita.db.Connection conn,
                        java.sql.Statement stmt) {
        m_conn = conn;
        m_stmt = stmt;
        m_conn.increaseUserCount();
        if (s_cat.isDebugEnabled()) {
            synchronized (dbgStatements) {
                dbgStatements.add(stmt);
                s_cat.debug("Statement constructor: created " + this + ", " +
                            "statement count is now " + dbgStatements.size(),
                            new Throwable("Stack trace"));
            }
        }
    }

    // Methods

    /**
     * Adds an SQL command to the current batch of commmands for this
     * Statement object.
     */
    public void addBatch(String sql) throws SQLException {
        try {
            sql_list.add(sql);
            m_stmt.addBatch(sql);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Cancels this Statement object if both the DBMS and driver
     * support aborting an SQL statement.
     */
    public void cancel() throws SQLException {
        try {
            m_stmt.cancel();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /** Makes the set of commands in the current batch empty. */
    public void clearBatch() throws SQLException {
        try {
            m_stmt.clearBatch();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /** Clears all the warnings reported on this Statement object. */
    public void clearWarnings() throws SQLException {
        try {
            m_stmt.clearWarnings();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Releases this Statement object's database and JDBC resources
     * immediately instead of waiting for this to happen when it is
     * automatically closed.
     *
     * NOTE that this may be run by a finalizer thread, so any
     * listeners with side effects should be threadsafe or
     * guarantee that they will not be needed while this Statement
     * is being closed (e.g. the connection needs to synchronize
     * intelligently, since there is no guarantee that it won't be
     * used at the same time as the finalizer).
     */
    public synchronized void close() throws SQLException {
        try {
            // TODO: figure out why double-closing is happening
            if (m_stmt != null) {
                m_stmt.close();
                m_conn.reduceUserCount();
                if (s_cat.isDebugEnabled()) {
                    synchronized (dbgStatements) {
                        if (!dbgStatements.contains(m_stmt)) {
                            s_cat.warn("Statement close: closing Statement that " +
                                       "was never opened or has already " +
                                       "been closed: " + m_stmt,
                                       new Throwable("Stack trace"));
                        } else {
                            dbgStatements.remove(m_stmt);
                        }

                        s_cat.debug("Statement close: Statement count is now " +
                                    dbgStatements.size());
                    }
                }
                m_stmt = null;
            }
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    protected synchronized void finalize() throws Throwable {
        try {
            if (m_stmt != null) {
                StringBuffer sb = null;
                ResultSetMetaData data = null;
                String lineSeparator = System.getProperty("line.separator");
                try {
                    java.sql.ResultSet rs = getResultSet();
                    data = rs.getMetaData();
                    rs.close();
                    sb = new StringBuffer(24 * data.getColumnCount());
                    sb.append("ResultSetMetaData:")
                        .append(lineSeparator)
                        .append("  Columns:")
                        .append(lineSeparator);
                    for (int i = 1; i <= data.getColumnCount(); i++) {
                        String table = data.getTableName(i);
                        if (table != null && table.trim().length() > 0) {
                            table = table + ".";
                        }
                        sb.append("    * ")
                            .append(table)
                            .append(data.getColumnName(i))
                            .append(lineSeparator);
                    }
                } catch (SQLException e) {
                    sb = new StringBuffer("Could not retrieve more information " +
                                          "about closed Statement: " +
                                          e.getMessage());
                }
                s_cat.warn("Statement was not closed by programmer: " + this +
                           ", closing in garbage collection.  Lots of these " +
                           "messages can indicate the cause of an out " +
                           "of cursors error. " + sb.toString());
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
     * This one just hits the log file, not DS.  It should be called before
     * running the query, and the other logQuery called after running.
     */
    private void logQuery(String type, String sql) throws SQLException {
        try {
            if (s_cat.isInfoEnabled()) {
                int conn_id = m_stmt.getConnection().hashCode();
                s_cat.info("Connection["+conn_id+"]::"+type+"::");
                s_cat.info(sql);
            }
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * This one just hits DS, not the log file.  It should be called after
     * running the query.
     */
    private void logQuery(String type, String sql, long time) throws SQLException {
        try {
            int conn_id = m_stmt.getConnection().hashCode();
            DeveloperSupport.logQuery(conn_id,
                                      type,
                                      sql,
                                      null,
                                      time,
                                      null);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Hits log file and DS
     */
    protected void logException(String type, String sql,
                                SQLException sqle) throws SQLException {
        try {
            int conn_id = m_stmt.getConnection().hashCode();
            s_cat.warn("Connection["+conn_id+"]::"+type+"::");
            s_cat.warn(sql);
            if (sqle != null) {
                s_cat.warn("threw exception: ", sqle);
            }
            DeveloperSupport.logQuery(conn_id,
                                      type,
                                      sql,
                                      null,
                                      0,
                                      sqle);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Executes an SQL statement that may return multiple results.
     */
    public boolean execute(String sql) throws SQLException {
        if (!m_closeAfterUse) {
            return doExecute(sql);
        } else {
            boolean retval = false;
            resetLastUpdateCount();
            try {
                retval = doExecute(sql);
            } catch (SQLException e) {
                close();
                throw m_conn.wrap(e);
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
    private boolean doExecute(String sql) throws SQLException {
        try {
            logQuery("execute", sql);
            long time = System.currentTimeMillis();
            if (m_needsAutoCommitOff) {
                m_conn.flagNeedsAutoCommitOff();
            }
            boolean ret = m_stmt.execute(sql);
            time = System.currentTimeMillis() - time;
            logQuery("execute", sql, time);
            return ret;
        } catch (SQLException e) {
            // Log SQLException and rethrow exception.
            logException("execute", sql, e);
            throw m_conn.wrap(e);
        }
    }

    /**
     * Submits a batch of commands to the database for execution and
     * if all commands execute successfully, returns an array of
     * update counts.
     */
    public int[] executeBatch() throws SQLException {
        if (!m_closeAfterUse) {
            return doExecuteBatch();
        } else {
            resetLastUpdateCount();
            try {
                return doExecuteBatch();
            } finally {
                close();
            }
        }
    }

    /**
     * The actual executeBatch; wrapped to implement the closeAfterUse
     * functionality.
     */
    private int[] doExecuteBatch() throws SQLException {
        StringBuffer queries = new StringBuffer();
        for (int i = 0; i < sql_list.size(); i++) {
            queries.append("Query[").append(i).append("] =").
                append(sql_list.get(i));
        }
        try {
            logQuery("executeBatch", queries.toString());
            long time = System.currentTimeMillis();
            if (m_needsAutoCommitOff) {
                m_conn.flagNeedsAutoCommitOff();
            }
            int[] ret = m_stmt.executeBatch();
            time = System.currentTimeMillis() - time;
            logQuery("executeBatch", queries.toString(), time);
            return ret;
        } catch (SQLException e) {
            // Log SQLException and rethrow exception.
            logException("executeBatch", queries.toString(), e);
            throw m_conn.wrap(e);
        }
    }

    /**
     * Executes an SQL statement that returns a single ResultSet
     * object.
     */
    public java.sql.ResultSet executeQuery(String sql) throws SQLException {
        if (!m_closeAfterUse) {
            return doExecuteQuery(sql);
        } else {
            resetLastUpdateCount();
            ResultSet rs;
            try {
                rs = doExecuteQuery(sql);
            } catch (SQLException e) {
                close();
                throw m_conn.wrap(e);
            }
            rs.addResultSetEventListener(this);
            return rs;
        }
    }

    /**
     * The actual executeQuery; wrapped to implement the closeAfterUse
     * functionality.
     */
    private ResultSet doExecuteQuery(String sql) throws SQLException {
        try {
            logQuery("executeQuery", sql);
            long time = System.currentTimeMillis();
            if (m_needsAutoCommitOff) {
                m_conn.flagNeedsAutoCommitOff();
            }
            ResultSet rs = ResultSet.wrap(this, m_stmt.executeQuery(sql));
            time = System.currentTimeMillis() - time;
            logQuery("executeQuery", sql, time);
            return rs;
        } catch (SQLException e) {
            // Log SQLException and rethrow exception.
            logException("executeQuery", sql, e);
            throw m_conn.wrap(e);
        }
    }

    /**
     * Executes a SQL INSERT, UPDATE or DELETE statement.
     */
    public int executeUpdate(String sql) throws SQLException {
        if (!m_closeAfterUse) {
            return doExecuteUpdate(sql);
        } else {
            resetLastUpdateCount();
            try {
                return doExecuteUpdate(sql);
            } finally {
                close();
            }
        }
    }

    /**
     * The actual executeUpdate; wrapped to implement the closeAfterUse
     * functionality.
     */
    private int doExecuteUpdate(String sql) throws SQLException {
        try {
            logQuery("executeUpdate", sql);
            long time = System.currentTimeMillis();
            if (m_needsAutoCommitOff) {
                m_conn.flagNeedsAutoCommitOff();
            }
            int ret = m_stmt.executeUpdate(sql);
            time = System.currentTimeMillis() - time;
            logQuery("executeUpdate", sql, time);
            return ret;
        } catch (SQLException e) {
            // Log SQLException and rethrow exception.
            logException("executeUpdate", sql, e);
            throw m_conn.wrap(e);
        }
    }

    /**
     * Returns the Connection object that produced this Statement
     * object.
     */
    public java.sql.Connection getConnection() throws SQLException {
        // doesn't actually throw SQLException, but we have to
        // conform to the interface.
        return m_conn;
    }

    /**
     * Retrieves the direction for fetching rows from database tables
     * that is the default for result sets generated from this
     * Statement object.
     */
    public int getFetchDirection() throws SQLException {
        try {
            return m_stmt.getFetchDirection();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Retrieves the number of result set rows that is the default
     * fetch size for result sets generated from this Statement
     * object.
     */
    public int getFetchSize() throws SQLException {
        try {
            return m_stmt.getFetchSize();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Returns the maximum number of bytes allowed for any column
     * value.
     */
    public int getMaxFieldSize() throws SQLException {
        try {
            return m_stmt.getMaxFieldSize();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Retrieves the maximum number of rows that a ResultSet object
     * can contain.
     */
    public int getMaxRows() throws SQLException {
        try {
            return m_stmt.getMaxRows();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Moves to a Statement object's next result.
     */
    public boolean getMoreResults() throws SQLException {
        try {
            if (m_closeAfterUse) {
                throw new IllegalStateException("Unable to get more results from " +
                                                "statement when in close after use mode");
            }
            return m_stmt.getMoreResults();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Retrieves the number of seconds the driver will wait for a
     * Statement object to execute.
     */
    public int getQueryTimeout() throws SQLException {
        try {
            return m_stmt.getQueryTimeout();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Returns the current result as a ResultSet object.
     */
    public java.sql.ResultSet getResultSet() throws SQLException {
        if (!m_closeAfterUse) {
            return doGetResultSet();
        } else {
            ResultSet rs = null;
            try {
                rs = doGetResultSet();
            } catch (SQLException e) {
                close();
            }
            if (s_cat.isDebugEnabled()) {
                s_cat.debug("Setting resultSetEventListener " + this + " for " + rs,
                            new Throwable());
            }
            rs.addResultSetEventListener(this);
            return rs;
        }
    }

    /**
     * The actual getResultSet; wrapped to implement the closeAfterUse
     * functionality.
     */
    private ResultSet doGetResultSet() throws SQLException {
        try {
            return new ResultSet(this, m_stmt.getResultSet());
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }


    /**
     * Retrieves the result set concurrency for ResultSet objects
     * generated by this Statement object.
     */
    public int getResultSetConcurrency() throws SQLException {
        try {
            return m_stmt.getResultSetConcurrency();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Retrieves the result set type for ResultSet objects generated
     * by this Statement object.
     */
    public int getResultSetType() throws SQLException {
        try {
            return m_stmt.getResultSetType();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Returns the current result as an update count; if the result
     * is a ResultSet object or there are no more results, -1 is
     * returned.
     */
    public int getUpdateCount() throws SQLException {
        if (!m_closeAfterUse) {
            return doGetUpdateCount();
        } else {
            if (!m_lastUpdateCountCurrent) {
                m_lastUpdateCount = doGetUpdateCount();
                m_lastUpdateCountCurrent = true;
                close();
            }
            return m_lastUpdateCount;
        }
    }

    /**
     * The actual getUpdateCount; wrapped to implement the closeAfterUse
     * functionality.
     */
    private int doGetUpdateCount() throws SQLException {
        try {
            return m_stmt.getUpdateCount();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Retrieves the first warning reported by calls on this
     * Statement object.
     */
    public SQLWarning getWarnings() throws SQLException {
        try {
            return m_stmt.getWarnings();
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Defines the SQL cursor name that will be used by subsequent
     * Statement object execute methods.
     */
    public void setCursorName(String name) throws SQLException {
        try {
            m_stmt.setCursorName(name);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets escape processing on or off.
     */
    public void setEscapeProcessing(boolean enable) throws SQLException {
        try {
            m_stmt.setEscapeProcessing(enable);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gives the driver a hint as to the direction in which the rows
     * in a result set will be processed.
     */
    public void setFetchDirection(int direction) throws SQLException {
        try {
            m_stmt.setFetchDirection(direction);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Gives the JDBC driver a hint as to the number of rows that
     * should be fetched from the database when more rows are
     * needed.
     */
    public void setFetchSize(int rows) throws SQLException {
        try {
            m_stmt.setFetchSize(rows);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the limit for the maximum number of bytes in a column to
     * the given number of bytes.
     */
    public void setMaxFieldSize(int max) throws SQLException {
        try {
            m_stmt.setMaxFieldSize(max);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the limit for the maximum number of rows that any
     * ResultSet object can contain to the given number.
     */
    public void setMaxRows(int max) throws SQLException {
        try {
            m_stmt.setMaxRows(max);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Sets the number of seconds the driver will wait for a
     * Statement object to execute to the given number of seconds.
     */
    public void setQueryTimeout(int seconds) throws SQLException {
        try {
            m_stmt.setQueryTimeout(seconds);
        } catch (SQLException e) {
            throw m_conn.wrap(e);
        }
    }

    /**
     * Wraps a PreparedStatement instance.
     */
    static Statement wrap(com.arsdigita.db.Connection conn,
                          java.sql.Statement stmt) {
        if (null == stmt) {
            return null;
        }
        if (stmt instanceof com.arsdigita.db.Statement) {
            return (com.arsdigita.db.Statement) stmt;
        } else {
            return new Statement(conn, stmt);
        }
    }

    /**
     * Two statements are equal if they wrap the same
     * statement.
     */
    public boolean equals(Object o) {
        if (o instanceof com.arsdigita.db.Statement) {
            if (m_stmt == null) {
                return super.equals(o);
            } else {
                Statement s = (com.arsdigita.db.Statement)o;
                return m_stmt.equals(s.m_stmt);
            }
        }
        return false;
    }

    /**
     * Hashcode delegates to the wrapped statement.
     */
    public int hashCode() {
        if (m_stmt != null) {
            return m_stmt.hashCode();
        } else {
            return super.hashCode();
        }
    }

    /**
     * Sets whether this statement will be closed after
     * use.  Should be called before using the statement
     * (e.g. before executing, preferably right after
     * construction).
     */
    public void setCloseAfterUse(boolean value) {
        if (m_closeAfterUse != value) {
            resetLastUpdateCount();
            m_closeAfterUse = value;
        }
    }

    /**
     * Indicates whether close after use is enabled.
     *
     * @return true if statement will be closed after use.
     */
    public boolean isCloseAfterUseEnabled() {
        return m_closeAfterUse;
    }

    /**
     * Resets last update count variable.  Should be called
     * before any execute when close after use is enabled.
     */
    protected void resetLastUpdateCount() {
        m_lastUpdateCount = -1;
        m_lastUpdateCountCurrent = false;
    }

    /**
     * ResultSetEventListener interface.
     * Called when resultSet closes.
     */
    public void resultSetClosed(ResultSetEvent event) throws java.sql.SQLException {
        if (s_cat.isDebugEnabled()) {
            s_cat.debug("Closing Statement because resultset was closed. " +
                        "Statement: " + this + ", resultset: " +
                        event.getResultSet());
        }
        close();
    }

    /**
     * <b><font color="red">Experimental</font></b>
     *
     * Sets whether this statement need autocommit to be
     * off.  Basically, this should be set true for any modifying statements
     * (e.g. insert, update, delete) and false for any select.  This
     * should be set before calling any of the execute methods.
     *
     * This value defaults to 'true', since that is
     * safer behavior.  This flag is intended to be used
     * by the transaction context's listener to determine
     * whether to hold on to a connection after use or recycle
     * it back to the connection pool.
     */
    public void setNeedsAutoCommitOff(boolean needsAutoCommitOff) {
        m_needsAutoCommitOff = needsAutoCommitOff;
    }

    /**
     * <b><font color="red">Experimental</font></b>
     *
     * Sets whether this statement need autocommit to be
     * off.  Basically, this should be true for any modifying statements
     * (e.g. insert, update, delete) and false for any select.
     * It is the caller's responsibility to provide this
     * information, in order to avoid re-parsing the SQL.
     * If the caller does not provide this information,
     * it is assumed that this value is 'true'.
     */
    public boolean getNeedsAutoCommitOff() {
        return m_needsAutoCommitOff;
    }

}
