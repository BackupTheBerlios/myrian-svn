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

import com.arsdigita.util.*;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * Central location for obtaining database connection.
 *
 * @author David Dao
 * @version $Revision: #4 $ $Date: 2003/08/04 $
 * @since 4.5
 *
 */

public class ConnectionManager {

    public static final String versionId = "$Author: dennis $ - $Date: 2003/08/04 $ $Id: //core-platform/proto/src/com/arsdigita/db/ConnectionManager.java#4 $";

    private static final Logger LOG =
        Logger.getLogger(ConnectionManager.class);

    private static ConnectionManager MANAGER;

    static ConnectionManager getInstance() {
        return MANAGER;
    }

    static void setInstance(ConnectionManager cm) {
        MANAGER = cm;
    }


    private DatabaseConnectionPool m_pool = null;
    private Class m_poolImpl =
        com.arsdigita.db.oracle.OracleConnectionPoolImpl.class;

    // # of times a getConnection will retry if connection is null
    // set by connectionRetryLimit in enterprise.init
    private int m_retryLimit = 0;

    // # of milliseconds thread will sleep between retries.
    // set by connectionRetrySleep in enterprise.init
    private long m_retrySleep = 100;

    // max # of connections to pool
    // set by connectionPoolSize in enterprise.init
    private int m_connectionPoolSize = 8;

    // DB Connection info
    private String m_url;
    private String m_username;
    private String m_password;

    // This records the last time we attempted to reinitialize the connection
    // pool.
    private long m_lastAttempt = 0;

    // By default we wait at least 30 seconds between attempts to reconnect to
    // the db.
    private long m_interval = 30000;

    ConnectionManager(String url, String username, String password) {
        m_url = url;
        m_username = username;
        m_password = password;
    }

    void setInterval(long interval) {
        m_interval = interval;
    }

    static final void badConnection(Connection conn) {
        ConnectionManager cm = getInstance();
        synchronized (cm) {
            if (conn.m_pool == cm.m_pool) {
                cm.disconnect();
            }
        }

        setCurrentThreadConnection(null);
    }

    synchronized void disconnect() {
        if (m_pool != null) {
            CURRENT_THREAD_CONNECTION = new ThreadLocal();
            m_pool.closeConnections();
            m_pool = null;
            m_lastAttempt = System.currentTimeMillis();
        }
    }

    synchronized void connect() throws java.sql.SQLException {
        // check to see if pool is already intialized
        // for this url, username, and password.
        if (m_pool != null) {
            return;
        }

        DatabaseConnectionPool pool;

        try {
            pool = (DatabaseConnectionPool) m_poolImpl.newInstance();
        } catch (InstantiationException e) {
            LOG.error("Unable to initialize DB pool", e);
            throw new DbException(e);
        } catch (IllegalAccessException e) {
            LOG.error("Unable to initialize DB pool", e);
            throw new DbException(e);
        }

        LOG.info("Setting connection info to " + m_url + ", " +
                 m_username + ", " + m_password);
        pool.setConnectionInfo(m_url, m_username, m_password);
        LOG.info("Setting connection pool size to " + m_connectionPoolSize);
        pool.setConnectionPoolSize(m_connectionPoolSize);

        try {
            // We do this to verify that this is a valid pool before letting
            // potentially large amounts of threads request a connection and
            // possibly overload the listener.
            java.sql.Connection conn = pool.getConnection();
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            SQLException wrapped = SQLExceptionHandler.wrap(e);
            if (wrapped instanceof DbNotAvailableException) {
                pool.closeConnections();
            }
            throw wrapped;
        }

        // We have to wait until here to set m_pool to the new value since we
        // don't want to make the pool available to other threads without
        // proper connection info and pool size.
        m_pool = pool;
    }

    synchronized void setDatabaseConnectionPoolName(Class poolImpl) {
        m_poolImpl = poolImpl;
    }

    /**
     * Sets the number of times ConnectionManager will retry
     * getting a connection.
     */
    synchronized void setRetryLimit(int retryLimit) {
        m_retryLimit = retryLimit;
    }

    /**
     * Sets the number of milliseconds ConnectionManager will
     * sleep between retries when getting a connection.
     */
    synchronized void setRetrySleep(long retrySleep) {
        m_retrySleep = retrySleep;
    }

    /**
     * Sets the number of connections that will be used by a connection
     * pool.  Note that a non-pooling connection manager may
     * silently ignore this setting.
     */
    synchronized void setConnectionPoolSize(int connectionPoolSize) {
        m_connectionPoolSize = connectionPoolSize;
        if (m_pool != null) {
            LOG.info("Setting connection pool size to " + m_connectionPoolSize);
            try {
                m_pool.setConnectionPoolSize(m_connectionPoolSize);
            } catch (java.sql.SQLException e) {
                LOG.error("Unable to set connection pool size", e);

            }
        }
        // If not set here, this will be set in setDefaultConnectionInfo
    }

    /**
     * Sets a driver-specific parameter.
     *
     * @param name Name of parameter.
     * @param value Value of parameter.
     */
    synchronized void setDriverSpecificParameter(String name, String value) {
        LOG.info("Setting " + name + " to " + value);
        if (m_pool != null) {
            try {
                m_pool.setDriverSpecificParameter(name, value);
            } catch (java.sql.SQLException  e) {
                LOG.error("Unable to set driver specific parameter " +
                          name + " to " + value);
            }
        } else {
            LOG.error("setDriverSpecificParameter must be called after " +
                      "setting default connection info.  Ignoring set for " +
                      name + " to " + value);
        }
    }

    /**
     * Number of connections that will be used by a connection pool.
     * Irrelevant if using a non-pooling connection manager.
     */
    int getConnectionPoolSize() {
        return m_connectionPoolSize;
    }

    /**
     * Return an available connection from the pool.
     */
    // intentionally not synchronized, due to the sleep.
    private java.sql.Connection gimmeConnection()
        throws java.sql.SQLException {

        DatabaseConnectionPool pool = m_pool;

        if (pool == null) {
            synchronized (this) {
                long since = System.currentTimeMillis() - m_lastAttempt;
                if (since > m_interval) {
                    m_lastAttempt = System.currentTimeMillis();
                    connect();
                    pool = m_pool;
                } else {
                    throw new DbNotAvailableException
                        ("The database went down. Will reattempt " +
                         "connecting in " + (m_interval - since) +
                         " milliseconds.");
                }
            }
        }

        Assert.assertNotNull(pool, "pool");

        int retries = 0;
        long time = System.currentTimeMillis();

        do {
            java.sql.Connection conn = null;
            // note that some drivers can be configured
            // to block in getConnection, in which case
            // this synchronized will be quite a
            // bottleneck.  Note that it's not
            // an ordered first-come-first-served system
            // that determines which of several competing
            // threads gets the monitor lock.
            // TODO: Consider working on a system for
            // controlling the order of who gets this lock?
            // TODO: Consider removing this synchronization
            // altogether, assuming m_pool is practically
            // immutable, and pushing all synchronization
            // responsibility down to the DB pool driver.
            // Or, maybe switch to wait/notify, see below TODO.
            //synchronized(ConnectionManager.class) {
            try {
                conn = pool.getConnection();
            } catch (SQLException e) {
                SQLException wrapped = SQLExceptionHandler.wrap(e);
                if (wrapped instanceof DbNotAvailableException) {
                    disconnect();
                }
                throw wrapped;
            }
            //}

            if (LOG.isInfoEnabled()) {
                long newtime = System.currentTimeMillis() - time;

                LOG.info("getConnection(). Executed in " + newtime +
                         " total ms. across " + retries + " retries");
                Throwable t = new Throwable("getConnection stack trace");
                LOG.debug(null, t);
            }

            // Wrap the connection object with our implementation.
            if (conn == null) {
                retries++;
                if (retries <= m_retryLimit) {
                    LOG.warn("Unable to get connection, sleeping (retry #" +
                             retries + ")");
                    try {
                        // TODO: consider synchronizing and changing this to
                        // a wait/notify relationship, instead of the current
                        // polling system.
                        Thread.sleep(m_retrySleep);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                } else {
                    LOG.warn("Unable to get connection, giving up " +
                             "(beyond retry limit at #" + retries + ")");
                    throw new java.sql.SQLException("Unable to get connection");
                }
            } else {
                // TODO: set threadlocal connection here once we have SDM #149294.
                return conn;
            }
            // this is an ugly loop end; the exits are both earlier, either via
            // throwing an exception or returning a successful connection.
            // looping will occur if conn is null and retrylimit has not
            // been exceeded.
        } while (true);
    }


    /**
     * Static API
     **/


    public static java.sql.Connection getConnection()
        throws java.sql.SQLException {
        return MANAGER.gimmeConnection();
    }


    static void closeConnections() {
        if (MANAGER != null &&
            MANAGER.m_pool != null) {
            MANAGER.m_pool.closeConnections();
        }
    }

    /**
     * Frees all of the connections in the pool.
     */
    public static void freeConnections() {
        if (MANAGER != null) {
            MANAGER.disconnect();
        }
    }

    /**
     * Code for ensuring that there is only one connection per thread.
     **/


    // Stores the connection for the current thread.
    private static ThreadLocal CURRENT_THREAD_CONNECTION = new ThreadLocal();

    /**
     * Returns the connection presently in use by this thread.
     * Presently relies on TransactionContext correctly clearing
     * this information.  TODO: Change to use a close listener for
     * clearing when/if we implement them (SDM #149294).
     *
     * May return null if there is no connection presently in use.
     * Will not ever allocate a connection, so presumably whatever
     * opened the connection will be responsible for closing it.
     *
     * This should be used whenever possible to avoid opening
     * multiple connections for one thread.
     *
     * @return This thread's current connection, or null if none.
     */
    // synchronization is not necessary since these are threadlocal
    public static java.sql.Connection getCurrentThreadConnection() {
        return (java.sql.Connection) CURRENT_THREAD_CONNECTION.get();
    }

    /**
     *  - to be removed
     * when we have close listeners and this doesn't need to be
     * externally managed anymore (SDM #149294).
     * <P>
     * Sets the threadlocal connection to the specified value.
     * Should be called every time a connection is opened or closed.
     */
    // synchronization is not necessary since these are threadlocal
    public static void setCurrentThreadConnection(java.sql.Connection conn) {
        CURRENT_THREAD_CONNECTION.set(conn);
    }

}
