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

import com.arsdigita.db.DatabaseConnectionPool;

import org.apache.log4j.Category;	

import java.util.LinkedList;
import java.util.List; 
import java.util.Collections;		

/**
 * Base connection pooling class
 *
 * @author Bob Donald (<a href="mailto:bdonald@arsdigita.com"></a>)
 * @version $Id: //core-platform/dev/src/com/arsdigita/db/BaseConnectionPool.java#2 $ $DateTime: 2002/07/18 13:18:21 $
 * @since  
 * 
 */

abstract public class BaseConnectionPool implements DatabaseConnectionPool {

    private static final String versionId = "$Author: dennis $ - $Date: 2002/07/18 $ $Id: //core-platform/dev/src/com/arsdigita/db/BaseConnectionPool.java#2 $";

    private static Category cat = Category.getInstance(BaseConnectionPool.class.getName());	 

    List m_usedConnections = Collections.synchronizedList(new LinkedList());
    List m_availConnections = Collections.synchronizedList(new LinkedList());
    
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
    
    public void freeConnections() {	  
        synchronized(this.getClass()) {
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
            synchronized(this.getClass()) {
                while(m_availConnections.size() < m_maxSize) {
                    java.sql.Connection pooledConn = getNewConnection();
                    if (pooledConn != null) {		
                        m_availConnections.add(pooledConn);
                    }
                }
                m_loaded = true;
            }
            cat.info("Database connection pool loaded with " +
                     m_availConnections.size() +
                     " connections.");
        }
        
        try {
            conn = (java.sql.Connection) m_availConnections.remove(0);  
            m_usedConnections.add(conn);
            conn = Connection.wrap( conn, this );
            cat.info("Retrieving connection from pool. " +
                     m_availConnections.size() +
                     " remaining.");
        } catch ( java.lang.IndexOutOfBoundsException e ) {	 
            conn = null;
    	}
        
        return conn;
    }	  
    
    public void returnToPool( java.sql.Connection conn ) { 
        if (m_usedConnections.remove(conn)) {
            m_availConnections.add(conn);
            cat.info("Connection returned to pool. " +
                     m_availConnections.size() +
                     " remaining.");
        }
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


