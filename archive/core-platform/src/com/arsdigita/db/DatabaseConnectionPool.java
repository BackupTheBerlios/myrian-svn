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

/**
 *
 *
 * @author David Dao (<a href="mailto:ddao@arsdigita.com"></a>)
 * @version $Revision: #4 $ $Date: 2002/10/02 $
 * @since 4.5
 *
 */

public interface DatabaseConnectionPool {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/DatabaseConnectionPool.java#4 $ by $Author: rhs $, $DateTime: 2002/10/02 13:49:31 $";

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
}
