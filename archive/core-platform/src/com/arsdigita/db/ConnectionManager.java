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
import org.apache.log4j.Category;

/**
 * 
 * Central location for obtaining database connection.
 *
 * @author David Dao (<a href="mailto:ddao@arsdigita.com"></a>)
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 * @since 4.5
 * 
 */

public class ConnectionManager {

    public static final String versionId = "$Author: dennis $ - $Date: 2002/05/12 $ $Id: //core-platform/dev/src/com/arsdigita/db/ConnectionManager.java#1 $";

    private static DatabaseConnectionPool s_pool = null;
    private static String s_poolName = 
        "com.arsdigita.db.oracle.OracleConnectionPoolImpl";

    // # of times a getConnection will retry if connection is null
    // set by connectionRetryLimit in enterprise.init
    private static int s_retryLimit = 0;

    // # of milliseconds thread will sleep between retries.
    // set by connectionRetrySleep in enterprise.init
    private static long s_retrySleep = 100;
    
    // max # of connections to pool
    // set by connectionPoolSize in enterprise.init
    private static int s_connectionPoolSize = 8;

    private static ThreadLocal s_currentThreadConnection = new ThreadLocal();

    private static Category cat = Category.getInstance(com.arsdigita.db.ConnectionManager.class.getName());

    static void setDefaultConnectionInfo(String url,
                                         String username,
                                         String password)
        throws java.sql.SQLException {

        synchronized (ConnectionManager.class) {
            
            // check to see if pool is already intialized
            // for this url, username, and password.

            if (s_pool != null) {
                if ( s_pool.getUrl().equals(url) &&
                     s_pool.getUserName().equals(username) &&
                     s_pool.getPassword().equals(password) ) {
                    // pool already intialized.
                    return; 
                } else {
                    s_pool.freeConnections();
                }
            }  
            
            // reset the pool
            s_pool = null;

            try {
                s_pool = (DatabaseConnectionPool)Class.forName(s_poolName)
                    .newInstance();
            } catch (Exception e) {
                //ClassNotFoundException, InstantiationException, 
                //IllegalAccessException
                cat.error("Unable to initialize DB pool", e);
                throw new DbException(e);
            }

            cat.info("Setting connection info to " + url + ", " + 
                     username + ", " + password);
            s_pool.setConnectionInfo(url, username, password);
            cat.info("Setting connection pool size to " + s_connectionPoolSize);
            s_pool.setConnectionPoolSize(s_connectionPoolSize);
        }
    }

    protected static synchronized void setDatabaseConnectionPoolName(String implName) {
        s_poolName = implName;
    }

    /**
     * Sets the number of times ConnectionManager will retry
     * getting a connection.
     */
    protected static synchronized void setRetryLimit(int retryLimit) {
        s_retryLimit = retryLimit;
    }

    /**
     * Sets the number of milliseconds ConnectionManager will
     * sleep between retries when getting a connection.
     */
    protected static synchronized void setRetrySleep(long retrySleep) {
        s_retrySleep = retrySleep;
    }

    /**
     * Sets the number of connections that will be used by a connection
     * pool.  Note that a non-pooling connection manager may
     * silently ignore this setting.
     */
    protected static synchronized void setConnectionPoolSize(int connectionPoolSize) {
        s_connectionPoolSize = connectionPoolSize;
        if (s_pool != null) {
            cat.info("Setting connection pool size to " + s_connectionPoolSize);
            try {
                s_pool.setConnectionPoolSize(s_connectionPoolSize);
            } catch (java.sql.SQLException e) {
                cat.error("Unable to set connection pool size", e);
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
    protected static synchronized void setDriverSpecificParameter(String name, String value) {
        cat.info("Setting " + name + " to " + value);
        if (s_pool != null) {
            try {
                s_pool.setDriverSpecificParameter(name, value);
            } catch (java.sql.SQLException  e) {
                cat.error("Unable to set driver specific parameter " + 
                          name + " to " + value);
            }
        } else {
            cat.error("setDriverSpecificParameter must be called after " + 
                      "setting default connection info.  Ignoring set for " + 
                      name + " to " + value);
        }
    }

    /**
     * Number of connections that will be used by a connection pool.
     * Irrelevant if using a non-pooling connection manager.
     */
    public static int getConnectionPoolSize() {
        return s_connectionPoolSize;
    }

    /**
     * Return an available connection from the pool.
     */
    // intentionally not synchronized, due to the sleep.
    public static java.sql.Connection getConnection() 
            throws java.sql.SQLException {

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
            // altogether, assuming s_pool is practically
            // immutable, and pushing all synchronization 
            // responsibility down to the DB pool driver.
            // Or, maybe switch to wait/notify, see below TODO.
            //synchronized(ConnectionManager.class) {

            try {
                conn = s_pool.getConnection();
            } catch (SQLException e) {
                SQLExceptionHandler.throwSQLException(e);
                throw e;  // code should never get here, but just in case
            }
            //}

            if (cat.isInfoEnabled()) {
                long newtime = System.currentTimeMillis() - time;
                
                cat.info("getConnection(). Executed in " + newtime + 
                         " total ms. across " + retries + " retries");
                Throwable t = new Throwable("getConnection stack trace");
                cat.debug(null, t);
            }
	
            // Wrap the connection object with our implementation.
            if (conn == null) {
                retries++;
                if (retries <= s_retryLimit) {
                    cat.warn("Unable to get connection, sleeping (retry #" + 
                             retries + ")");
                    try {
                        // TODO: consider synchronizing and changing this to
                        // a wait/notify relationship, instead of the current
                        // polling system.
                        Thread.sleep(s_retrySleep);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                } else {
                    cat.warn("Unable to get connection, giving up " + 
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
        return (java.sql.Connection)s_currentThreadConnection.get();
    }

    /**
     * <b><font color="red">Experimental</font></b> - to be removed
     * when we have close listeners and this doesn't need to be
     * externally managed anymore (SDM #149294).
     * <P>
     * Sets the threadlocal connection to the specified value.
     * Should be called every time a connection is opened or closed.
     */
    // synchronization is not necessary since these are threadlocal
    public static void setCurrentThreadConnection(java.sql.Connection conn) {
        s_currentThreadConnection.set(conn);
    }

    /**
     * Frees all of the connections in the pool.
     */
	public static void freeConnections() {
		if (s_pool != null) {
			s_pool.freeConnections();
		}
	}
}
										   

