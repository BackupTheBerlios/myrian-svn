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

import java.sql.SQLException;
import java.sql.SQLWarning;

import java.util.Map;
import org.apache.log4j.Logger;


/**
 * An implementation of the java.sql.Connection interface that
 * wraps a "real" implementation of java.sql.Connection.
 *
 * <ul><lh>aD specific features:</lh>
 * <li>Can track a user count and call a listener
 * when the user count reaches zero.</li>
 * <li>Supports transparently getting a new connection if
 * the underlying connection is closed, allowing aggressive
 * closing whenever user count reaches zero if transaction-like
 * behavior is not required</li>
 * <li>Simple flag for tracking whether this connection
 * needs autocommit off or not (a connection that was used for
 * modifying data should not be aggressively closed, as transaction-like
 * behavior would be lost, so it needs autocommit off)</li>
 * </ul>
 *
 * @author <a href="mailto:mthomas@arsdigita.com">Mark Thomas</a>
 * @version $Revision: #3 $ $Date: 2002/08/13 $
 * @since 4.5
 */
// Synchronization in this class is primarily because close can be called via 
// a finalizer thread, so m_conn needs to be guarded so that 
// ensureConnectionExists can make a new one if m_conn
// is closed out from underneath this connection.
public class Connection implements java.sql.Connection {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/Connection.java#3 $ $Author: dennis $ $Date: 2002/08/13 $";

    // the connection object that we wrap
    private java.sql.Connection m_conn;	 
	private BaseConnectionPool  m_pool;

    private static final Logger s_cat = Logger.getLogger(com.arsdigita.db.Connection.class.getName());

    private static final java.util.Set dbgConnections = new java.util.HashSet();
    private long dbgTimestamp;

    /**
     * Keeps track of user counts based on underlying java.sql.connection object.
     *
     * It should be safe to do this tracking here at the 
     * com.arsdigita.db.Connection level, since no thread should 
     * ever receive a new com.arsdigita.db.Connection if the
     * one presently in use has modified data.  If this assumption is ever
     * violated, it will be necessary to associate the flag
     * with the underlying java.sql.connection object, implying a 
     * static hashset to access a simple flag variable class.
     */
    private int m_userCount = 0;

    /**
     * Tracks whether this connection needs autocommit off (e.g. has
     * modified any data).
     *
     * It should be safe to do this tracking here at the 
     * com.arsdigita.db.Connection level, since no thread should 
     * ever receive a new com.arsdigita.db.Connection if the
     * one presently in use has modified data.  If this assumption is ever
     * violated, it could be necessary to associate this flag
     * with the underlying java.sql.connection object, e.g. a 
     * static hashset to access a simple flag variable class.
     *
     * Defaults to false here, but all Statements default to true, 
     * so the first statement that is executed will flag this unless
     * the statement was explicitly set to indicate it does not
     * need autocommit off.
     */
    private boolean m_needsAutoCommitOff = false;

    /**
     * Connection use listeners.  ACS-specific extension of Connection
     * to notify listeners when there are no further immediate users of
     * the connection, but the connection hasn't necessarily been explicitly
     * closed.
     */
    private java.util.Set s_listeners = new java.util.HashSet();

    private Thread m_thread;

    /**
     * Indicates that connection close was 'soft', e.g. further use
     * should be allowed by replacing a null m_conn when needed.
     */
    private boolean m_softClose = false;

    // Constructor: use the "wrap" class method to create instances
    private Connection(java.sql.Connection conn, BaseConnectionPool pool) {
        this.m_conn = conn;
        this.m_pool = pool;

        if (s_cat.isDebugEnabled()) {
            // There are some warnings in here, but they are
            // only checked for if debug is enabled because
            // they would slow things down if always checked
            // for.
            Throwable t = 
                    new Throwable("Connection create stack trace for debugging");
            synchronized (dbgConnections) {
                if (dbgConnections.contains(m_conn)) {
                    s_cat.warn("connection constructor: Creating new " + 
                               "connection wrapping " + m_conn + " before " +
                               "previous wrapper for this connection was " +
                               "closed.");
                }
                dbgConnections.add(m_conn);
                s_cat.debug("connection constructor: created " + this + ", " +
                            "connection count is now " + dbgConnections.size(), 
                            t);
                if (dbgConnections.size() > 
                        ConnectionManager.getConnectionPoolSize()) {
                    s_cat.warn("connection constructor: connections list " +
                               "exceeded pool size: " + dbgConnections);
                }
            }
            dbgTimestamp = System.currentTimeMillis();
        }		
        // TODO : Need to set class variables here, somehow....
    }

    /** 
     * Clears all warnings reported for this Connection object. 
     */
    public synchronized void clearWarnings() throws SQLException {
        ensureConnectionExists();
        try {
            m_conn.clearWarnings();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Releases a Connection's database and JDBC resources
     * immediately, but allows an underlying connection to be 
     * automatically re-obtained later.  This is useful for 
     * aggressive closing as soon as there are no current users
     * of the connection, as the connection can be re-obtained
     * when there is a new user.
     */
    public synchronized void softClose() throws SQLException {
        this.close();
        m_softClose = true;
    }

    /** 
     * Releases a Connection's database and JDBC resources
     * immediately instead of waiting for them to be automatically
     * released. 
     */
    // TODO: determine if this should fire connection use listeners?
    // presently they are only fired when the user count is
    // explicitly reduced to zero, at which point they are used
    // to close the connection.  So, if they did fire the use 
    // listeners, changes would have to be made to avoid infinite loops.
    public synchronized void close() throws SQLException {
        if (s_cat.isDebugEnabled()) {
            // There are some warnings in here, but they are
            // only checked for if debug is enabled because
            // they would slow things down if always checked
            // for.
            Throwable t = new Throwable("Connection close stack trace for " +
                                        "debugging");
            synchronized (dbgConnections) {
                if (dbgConnections.size() > 
                    ConnectionManager.getConnectionPoolSize()) {
                    s_cat.warn("connection close: un-closed connections " + 
                               "list exceeded pool size: " + dbgConnections);
                }
                if (!dbgConnections.contains(m_conn)) {
                    s_cat.warn("connection close: closing connection that " +
                               "was never opened or has already " +
                               "been closed: " + m_conn + " from " + this);
                } else {
                    dbgConnections.remove(m_conn);
                }
                s_cat.debug("un-closed connection count is now " + 
                            dbgConnections.size() + ", connection was held " + 
                            "open for " + 
                            (System.currentTimeMillis() - dbgTimestamp) + 
                            " ms.", t);
            }
        }

        m_softClose = false;
        
        // put the connection back in the pool or just close it
        try {
            if (m_conn != null) {
                if (m_pool != null ) {
                    m_pool.returnToPool(m_conn);
                } else {
                    m_conn.close();
                }
                m_conn = null;
            }
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    protected synchronized void finalize() throws Throwable {
        try {
            if (!isClosed()) {
                s_cat.warn("Connection was not closed by programmer: " + this + 
                           ", closing in garbage collection.  This " + 
                           "can lead to out of cursor exceptions so it should " +
                           "be corrected by the programmer.");
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
     * Makes all changes made since the previous commit/rollback
     * permanent and releases any database locks currently held by the
     * Connection. 
     */
    public synchronized void commit() throws SQLException {
        ensureConnectionExists();
        try {
            m_conn.commit();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Creates a Statement object for sending SQL statements to the
     * database. 
     *
     * This is really a com.arsdigita.db.Statement, but the 
     * interface doesn't let us change the return type.     
     */
    public synchronized java.sql.Statement createStatement() throws SQLException {
        ensureConnectionExists();
        try {
            return Statement.wrap(this, m_conn.createStatement());
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Creates a Statement object that will generate ResultSet
     * objects with the given type and concurrency. 
     *
     * This is really a com.arsdigita.db.Statement, but the 
     * interface doesn't let us change the return type.     
     */
    public synchronized java.sql.Statement createStatement(int resultSetType,
                                              int resultSetConcurrency)
            throws SQLException {
        ensureConnectionExists();
        try {
            return Statement.wrap(this,
                                  m_conn.createStatement(resultSetType,
                                                         resultSetConcurrency));
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Gets the current auto-commit state. 
     */
    public synchronized  boolean getAutoCommit() throws SQLException {
        ensureConnectionExists();
        try {
            return m_conn.getAutoCommit();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Returns the Connection's current catalog name. 
     */
    public synchronized String getCatalog() throws SQLException {
        ensureConnectionExists();
        try {
            return m_conn.getCatalog();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Gets the metadata regarding this connection's database. 
     *
     * This is really a com.arsdigita.db.DatabaseMetaData, but the 
     * interface doesn't let us change the return type.     
     */
    public synchronized java.sql.DatabaseMetaData getMetaData() throws SQLException {
        ensureConnectionExists();
        try {
            return DatabaseMetaData.wrap(this, m_conn.getMetaData());
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Gets this Connection's current transaction isolation level. 
     */
    public synchronized int getTransactionIsolation() throws SQLException {
        ensureConnectionExists();
        try {
            return m_conn.getTransactionIsolation();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Gets the type map object associated with this connection. 
     */
    public synchronized Map getTypeMap() throws SQLException {
        ensureConnectionExists();
        try {
            return m_conn.getTypeMap();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /** 
     * Returns the first warning reported by calls on this
     * Connection. 
     */
    public synchronized SQLWarning getWarnings() throws SQLException {
        ensureConnectionExists();
        try {
            return m_conn.getWarnings();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Tests to see if a Connection is closed. 
     */
    public synchronized boolean isClosed() throws SQLException {
        try {
            return (m_conn == null || m_conn.isClosed());
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Tests to see if the connection is in read-only mode. 
     */
    public synchronized boolean isReadOnly() throws SQLException {
        ensureConnectionExists();
        try {
            return m_conn.isReadOnly();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Converts the given SQL statement into the system's native SQL
     * grammar. 
     */
    public synchronized String nativeSQL(String sql) throws SQLException {
        s_cat.info("nativeSQL: ");
        s_cat.info(sql);
        ensureConnectionExists();
        try {
            return m_conn.nativeSQL(sql);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Creates a CallableStatement object for calling database stored
     * procedures. 
     *
     * This is really a com.arsdigita.db.CallableStatement, but the 
     * interface doesn't let us change the return type.     
     */
    public synchronized java.sql.CallableStatement prepareCall(String sql) throws SQLException {
        // logging will happen at statement use time
        ensureConnectionExists();
        try {
            return CallableStatement.wrap(this, sql, m_conn.prepareCall(sql));
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Creates a CallableStatement object that will generate
     * ResultSet objects with the given type and concurrency. 
     *
     * This is really a com.arsdigita.db.CallableStatement, but the 
     * interface doesn't let us change the return type.     
     */
    public synchronized java.sql.CallableStatement prepareCall(String sql,
                                                  int resultSetType,
                                                  int resultSetConcurrency)
            throws SQLException {
        // logging will happen at statement use time
        ensureConnectionExists();
        try {
            return CallableStatement.wrap(this, 
                                          sql,
                                          m_conn.prepareCall(sql,
                                                             resultSetType, 
                                                             resultSetConcurrency));
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /** 
     * Creates a PreparedStatement object for sending parameterized
     * SQL statements to the database. 
     *
     * This is really a com.arsdigita.db.PreparedStatement, but the 
     * interface doesn't let us change the return type.
     */
    public synchronized java.sql.PreparedStatement prepareStatement(String sql)
            throws SQLException {
        // logging will happen at statement use time
        ensureConnectionExists();
        try {
            return PreparedStatement.wrap(this, sql, m_conn.prepareStatement(sql));
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /** 
     * Creates a PreparedStatement object that will generate
     * ResultSet objects with the given type and concurrency. 
     *
     * This is really a com.arsdigita.db.PreparedStatement, but the 
     * interface doesn't let us change the return type.
     */
    public synchronized java.sql.PreparedStatement prepareStatement(String sql,
                                                       int resultSetType,
                                                       int resultSetConcurrency)
            throws SQLException {
        // logging will happen at statement use time
        ensureConnectionExists();
        try {
            return PreparedStatement.wrap(this, 
                                          sql,
                                          m_conn.prepareStatement(sql,
                                                                  resultSetType, 
                                                                  resultSetConcurrency));
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Drops all changes made since the previous commit/rollback and
     * releases any database locks currently held by this
     * Connection. 
     */
    public synchronized void rollback() throws SQLException {
        ensureConnectionExists();
        try {
            m_conn.rollback();
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Sets this connection's auto-commit mode. 
     */
    public synchronized void setAutoCommit(boolean autoCommit) throws SQLException {
        ensureConnectionExists();
        try {
            m_conn.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Sets a catalog name in order to select a subspace of this
     * Connection's database in which to work. 
     */
    public synchronized void setCatalog(String catalog) throws SQLException {
        ensureConnectionExists();
        try {
            m_conn.setCatalog(catalog);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /** 
     * Puts this connection in read-only mode as a hint to enable
     * database optimizations. 
     */
    public synchronized void setReadOnly(boolean readOnly) throws SQLException {
        ensureConnectionExists();
        try {
            m_conn.setReadOnly(readOnly);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Attempts to change the transaction isolation level to the one
     * given. 
     */
    public synchronized void setTransactionIsolation(int level) throws SQLException {
        ensureConnectionExists();
        try {
            m_conn.setTransactionIsolation(level);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /**
     * Installs the given type map as the type map for this
     * connection. 
     */
    public synchronized void setTypeMap(Map map) throws SQLException {
        ensureConnectionExists();
        try {
            m_conn.setTypeMap(map);
        } catch (SQLException e) {
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }

    /** 
     * Wraps a Connection instance.
     */
    static Connection wrap(java.sql.Connection conn, BaseConnectionPool pool) {
        return new Connection(conn, pool);
    }

    /**
     * <b><font color="red">Experimental</font></b>
     *
     * Decreases the user count and fires connection use listeners
     * if user count reaches zero.  
     * Presently used to track the # of outstanding statements for 
     * this connection.
     */
    protected synchronized void reduceUserCount() {
        // Sychronized because one Statement could be created while another is being
        // finalized, so m_userCount must be guarded.
        m_userCount--;
        if (m_userCount <= 0) {
            java.util.Iterator i = s_listeners.iterator();
            while (i.hasNext()) {
                ConnectionUseListener l = (ConnectionUseListener)i.next();
                try {
                    l.connectionUserCountHitZero(this);
                } catch (Exception e) {
                    s_cat.error("Error running connection used listener " + 
                                l, e);
                }
            }
        }
    }

    /**
     * <b><font color="red">Experimental</font></b>
     *
     * Increases the user count.  Presently used to track the # of outstanding
     * statements for this connection.
     */
    protected synchronized void increaseUserCount() {
        // Sychronized because one Statement could be created while another is being
        // finalized, so m_userCount must be guarded.
        m_userCount++;
    }

    /**
     * <b><font color="red">Experimental</font></b>
     * 
     * Adds a request listener to this particular connection.
     * If this method is needed, it should be called before actually 
     * using the connection.
     */
    public void addConnectionUseListener(ConnectionUseListener ul) { 
        // Connection can't be in use at the time this should be called, so 
        // no need to synchronize (for once...).
        s_listeners.add(ul);
    }



    /**
     * <b><font color="red">Experimental</font></b>
     *
     * Flags that a statement needed autocommit to be
     * off.  Basically, this should be set true for any modifying statements
     * (e.g. insert, update, delete), at the time of statement
     * execution.
     */
    protected void flagNeedsAutoCommitOff() {
        m_needsAutoCommitOff = true;
    }

    /**
     * <b><font color="red">Experimental</font></b>
     * Indicates whether any statement needed autocommit to be
     * off.  
     */
    public boolean getNeedsAutoCommitOff() {
        return m_needsAutoCommitOff;
    }

    /** 
     * Allocates m_conn if it is null.
     *
     * This is due to the delaying of holding on to a connection until
     * a modification occurs; unfortunately there is no way to be sure that 
     * a connection is no longer needed, so when no more statements are in 
     * use we return the underlying java.sql.Connection to the connection 
     * pool while allowing the user to still have their 
     * com.arsdigita.db.Connection object.  If they then use the 
     * com.arsdigita.db.Connection again, we pull another underlying 
     * java.sql.Connection from the pool.
     * 
     * All calls to this method should be from a synchronized method.
     */
    private void ensureConnectionExists() throws java.sql.SQLException {
        if (m_conn == null) {
            if (m_softClose) {
                // the connection-users-hit-zero check fired too early.
                // so, we get back a connection, re-register as the current
                // thread listener, shut off autocommit, and 
                // resume business as usual.
                s_cat.info("Connection " + this + " was closed early, re-opening");
                
                // we want a raw java.sql.Connection for m_conn, 
                // not something we wrapped.
                m_conn = ((com.arsdigita.db.Connection)ConnectionManager.
                          getConnection()).m_conn;
                ConnectionManager.setCurrentThreadConnection(this);
                m_conn.setAutoCommit(false);
            } else {
                throw new SQLException ("Connection has been closed.");
            }
        }
    }

    /**
     * A debugging tostring, includes some information about the underlying
     * connection and this wrapper.
     */
    public String toString() {
        return "Connection[" + ((m_conn==null)?"null":(Integer.toString(m_conn.hashCode()))) + 
            ", pool " + m_pool + ", needsAutoCommitOff " + 
            m_needsAutoCommitOff + "]";
    }
}



