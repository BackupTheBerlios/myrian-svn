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

package com.arsdigita.db.oracle;

import com.arsdigita.db.BaseConnectionPool;
import com.arsdigita.db.DatabaseConnectionPool;
import com.arsdigita.db.SQLExceptionHandler;

import java.sql.SQLException;

import oracle.jdbc.pool.OracleDataSource;

import org.apache.log4j.Category;

/**
 * Connection pooling class using Oracle implementation.
 *
 * @author David Dao (<a href="mailto:ddao@arsdigita.com"></a>)
 * @version $Id: //core-platform/dev/src/com/arsdigita/db/oracle/OracleConnectionPoolImpl.java#1 $ $DateTime: 2002/05/12 18:23:13 $
 * @since  
 * 
 */

public class OracleConnectionPoolImpl extends BaseConnectionPool {

    private static final String versionId = "$Author: dennis $ - $Date: 2002/05/12 $ $Id: //core-platform/dev/src/com/arsdigita/db/oracle/OracleConnectionPoolImpl.java#1 $";

    private static OracleDataSource ods = null;

    private static Category cat = Category.getInstance(OracleConnectionPoolImpl.class.getName());
	 
    public void setConnectionInfo(String url, String username,
                                  String password) throws SQLException { 
        try {
            super.setConnectionInfo(url, username, password);
            cat.info("Using: " + url + ", " + username + ", " + password);
            ods = new OracleDataSource();
            ods.setURL(m_url);
            ods.setUser(m_user);
            ods.setPassword(m_password);
        } catch (SQLException e) {
            cat.error("Error setting connection info", e);
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }
    }
    
    protected java.sql.Connection getNewConnection() 
            throws java.sql.SQLException {
        try { 
            return ods.getConnection();
        } catch (SQLException e) {
            cat.error("Error getting new connection", e);
            SQLExceptionHandler.throwSQLException(e);
            throw e;  // code should never get here, but just in case
        }	
    }
		
    /**
     * Sets a driver-specific parameter.
     *
     * @param name Name of parameter.
     * @param value Value of parameter.
     */
    public void setDriverSpecificParameter(String name, 
                                           String value) 
            throws java.sql.SQLException {
        // ignore
    }

}



