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

/**
 * 
 *
 * @author David Dao (<a href="mailto:ddao@arsdigita.com"></a>)
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 * @since 4.5
 * 
 */

public interface DatabaseConnectionPool {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/DatabaseConnectionPool.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

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
}
