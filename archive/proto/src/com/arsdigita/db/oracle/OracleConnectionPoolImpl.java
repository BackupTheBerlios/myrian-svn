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

package com.arsdigita.db.oracle;

import com.arsdigita.db.BaseConnectionPool;
import com.arsdigita.db.SQLExceptionHandler;

import java.sql.SQLException;

import oracle.jdbc.pool.OracleDataSource;

import org.apache.log4j.Logger;

/**
 * Connection pooling class using Oracle implementation.
 *
 * @author David Dao (<a href="mailto:ddao@arsdigita.com"></a>)
 * @version $Id: //core-platform/proto/src/com/arsdigita/db/oracle/OracleConnectionPoolImpl.java#1 $ $DateTime: 2002/11/27 19:51:05 $
 * @since
 *
 */

public class OracleConnectionPoolImpl extends BaseConnectionPool {

    public static final String versionId = "$Author: dennis $ " +
        "- $Date: 2002/11/27 $ " + 
        "$Id: //core-platform/proto/src/com/arsdigita/db/oracle/OracleConnectionPoolImpl.java#1 $";

    private static final Logger cat = Logger.getLogger(OracleConnectionPoolImpl.class.getName());

    private static boolean s_useFixFor901 = false;

    public static void setUseFixFor901(final boolean flag) {
        s_useFixFor901 = flag;
        if (flag == true) {
            cat.warn("Executing fix for oracle 901.");
        }
    }

    private OracleDataSource m_ods = null;

    public void setConnectionInfo(String url, String username,
                                  String password) throws SQLException {
        try {
            super.setConnectionInfo(url, username, password);
            cat.info("Using: " + url + ", " + username + ", " + password);
            m_ods = new OracleDataSource();
            m_ods.setURL(m_url);
            m_ods.setUser(m_user);
            m_ods.setPassword(m_password);
        } catch (SQLException e) {
            cat.error("Error setting connection info", e);
            throw SQLExceptionHandler.wrap(e);
        }
    }

    protected java.sql.Connection getNewConnection()
        throws java.sql.SQLException {
        try {
            java.sql.Connection con = m_ods.getConnection();
            if (s_useFixFor901) {
                java.sql.PreparedStatement stmt = con.prepareStatement( "alter session set \"_push_join_union_view\" = false");
                stmt.execute();
            }
            return con;

        } catch (SQLException e) {
            cat.error("Error getting new connection", e);
            throw SQLExceptionHandler.wrap(e);
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
