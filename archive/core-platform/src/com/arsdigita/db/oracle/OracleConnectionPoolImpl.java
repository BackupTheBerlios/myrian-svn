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
 * @version $Id: //core-platform/dev/src/com/arsdigita/db/oracle/OracleConnectionPoolImpl.java#3 $ $DateTime: 2002/07/18 13:18:21 $
 * @since  
 * 
 */

public class OracleConnectionPoolImpl extends BaseConnectionPool {

    private static final String versionId = "$Author: dennis $ - $Date: 2002/07/18 $ $Id: //core-platform/dev/src/com/arsdigita/db/oracle/OracleConnectionPoolImpl.java#3 $";

    private static OracleDataSource ods = null;

    private static Category cat = Category.getInstance(OracleConnectionPoolImpl.class.getName());

    private static boolean s_useFixFor901 = false;

    public static void setUseFixFor901(final boolean flag) {
        s_useFixFor901 = flag;
        if (flag == true) {
            cat.warn("Executing fix for oracle 901.");
        }
    }

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
            java.sql.Connection con = ods.getConnection();
            if (s_useFixFor901) {
                java.sql.PreparedStatement stmt = con.prepareStatement( "alter session set \"_push_join_union_view\" = false");
                stmt.execute();
            }
            return con;

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



