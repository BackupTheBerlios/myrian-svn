/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

import java.sql.SQLException;

import com.arsdigita.db.DatabaseConnectionPool;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

/**
 * Base connection pooling class
 *
 * @author Bob Donald
 * @version $Id: //core-platform/dev/src/com/arsdigita/db/BaseConnectionPool.java#12 $ $DateTime: 2003/08/19 15:33:40 $
 * @since
 *
 */

abstract public class BaseConnectionPool implements DatabaseConnectionPool {

    public static final String versionId = "$Author: bche $ " +
        "- $Date: 2003/08/19 $ " + 
        "$Id: //core-platform/dev/src/com/arsdigita/db/BaseConnectionPool.java#12 $";

    private static final Logger cat = Logger.getLogger(BaseConnectionPool.class.getName());

    private List m_usedConnections = new LinkedList();
    private List m_availConnections = new LinkedList();
    private Object m_monitor = new Object();

    protected String m_user;
    protected String m_password;
    protected String m_url;
    protected int    m_maxSize = 10; // set default

    private boolean  m_loaded = false;

    public void setConnectionInfo(String url, String username,
                                  String password) throws SQLException {
        cat.info("Using: " + url + ", " + username + ", " + password);
        m_user = username;
        m_password = password;
        m_url = url;
    }

    public String getUrl() {
        return m_url;
    }

    public String getUserName() {
        return m_user;
    }

    public String getPassword() {
        return m_password;
    }

    public void closeConnections() {
        synchronized (m_monitor) {
            for (Iterator it = m_availConnections.iterator(); it.hasNext(); ) {
                java.sql.Connection conn = (java.sql.Connection) it.next();
                try {
                    conn.close();
                } catch (java.sql.SQLException e) {
                    cat.error(e);
                }
            }
            for (Iterator it = m_usedConnections.iterator(); it.hasNext(); ) {
                java.sql.Connection conn = (java.sql.Connection) it.next();
                try {
                    conn.close();
                } catch (java.sql.SQLException e) {
                    cat.error(e);
                }
            }
            m_availConnections.clear();
            m_usedConnections.clear();
            m_loaded = false;
        }
    }

    public void freeConnections() {
        synchronized(m_monitor) {
            m_availConnections.clear();
            m_loaded = false;
        }
        cat.info("Connections in pool are freed.");
    }

    public void setConnectionPoolSize(int num) throws java.sql.SQLException {
        m_maxSize = num;
        cat.info("Connection pool size set to " + m_maxSize);
    }

    // don't synchronize this method because we want to return null
    // if we don't get a connection
    public java.sql.Connection getConnection() throws java.sql.SQLException {
        java.sql.Connection conn = null;

        if (m_loaded == false ) {

            cat.info("Populating database connection pool.");
            synchronized(m_monitor) {
                while(m_availConnections.size() < m_maxSize) {
                    java.sql.Connection pooledConn = getNewConnection();
                    if (pooledConn != null) {
                        m_availConnections.add(pooledConn);
                    }
                }
                m_loaded = true;
                cat.info("Database connection pool loaded with " +
                         m_availConnections.size() +
                         " connections.");
            }
        }

        try {
            synchronized (m_monitor) {
                conn = (java.sql.Connection) m_availConnections.remove(0);
                m_usedConnections.add(conn);                
                cat.info("Retrieving connection from pool. " +
                         m_availConnections.size() +
                         " remaining.");
            }
        } catch ( java.lang.IndexOutOfBoundsException e ) {
            conn = null;
        }

        return conn;
    }

    public void returnToPool( java.sql.Connection conn ) {
        synchronized (m_monitor) {
            if (m_usedConnections.remove(conn)) {
                m_availConnections.add(conn);
                cat.info("Connection returned to pool. " +
                         m_availConnections.size() +
                         " remaining.");
            }
        }
    }
    
    public boolean containsConnection(java.sql.Connection conn) {
        boolean bReturn = false;
        if (m_availConnections.contains(conn)) {
            bReturn = true;
        } else if (m_usedConnections.contains(conn)) {
            bReturn = true;
        }
        
        return bReturn;
    }

    /**
     * Sets a driver-specific parameter.
     *
     * @param name Name of parameter.
     * @param value Value of parameter.
     */
    abstract public void setDriverSpecificParameter(String name,
                                                    String value)
        throws java.sql.SQLException;

    /**
     * Gets a new database connection for populating pool.
     *
     */
    abstract protected java.sql.Connection getNewConnection()
        throws java.sql.SQLException;

}
