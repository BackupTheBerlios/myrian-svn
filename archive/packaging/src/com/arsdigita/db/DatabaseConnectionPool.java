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

/**
 *
 *
 * @author David Dao
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 * @since 4.5
 *
 */

public interface DatabaseConnectionPool {

    public static final String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/db/DatabaseConnectionPool.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

    public java.sql.Connection getConnection() throws java.sql.SQLException;

    /**
     * Sets the connection information for the connection pool. This is used
     * for creating connections that go into the pool.
     */
    public void setConnectionInfo(String url, String username, String password)
        throws java.sql.SQLException;

    /**
     * Returns the url string for the connection.
     */
    public String getUrl();

    /**
     * Returns the user name for the connections in the pool.
     */
    public String getUserName();

    /**
     * Returns the password for the connections in the pool.
     */
    public String getPassword();

    /**
     * Closes and frees all the connections in the pool.
     **/

    public void closeConnections();

    /**
     * Frees the connections in the pool.
     */
    public void freeConnections();

    /**
     * Sets the number of connections that will be used by a connection
     * pool.  Note that a non-pooling connection manager may
     * silently ignore this setting.
     */
    public void setConnectionPoolSize(int num) throws java.sql.SQLException;

    /**
     * Sets a driver-specific parameter.
     *
     * @param name Name of parameter.
     * @param value Value of parameter.
     */
    public void setDriverSpecificParameter(String name, String value)
        throws java.sql.SQLException;

    /**
     * Returns a connection to the pool
     * 
     * @param conn the connection to return to the pool
     */        
    public void returnToPool( java.sql.Connection conn );        
    
    /**
     * Returns true if this pool contains the connection, conn
     * 
     * @param conn the connection to test
     * @return true if this pool contains the connection conn
     */
    public boolean containsConnection(java.sql.Connection conn);
}
