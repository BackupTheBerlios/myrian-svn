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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

/**
 *
 *
 * @author David Dao
 * @version $Revision: #2 $ $Date: 2003/04/09 $
 * @since 4.5
 *
 */

public class J2EEConnectionManagerImpl implements DatabaseConnectionPool {

    public static final String versionId = "$Author: rhs $ - $Date: 2003/04/09 $ $Id: //core-platform/proto/src/com/arsdigita/db/J2EEConnectionManagerImpl.java#2 $";

    private static final Logger cat = Logger.getLogger(J2EEConnectionManagerImpl.class.getName());

    private String defaultDataSource = "jdbc/db";

    public void setConnectionInfo(String url, String username, String password)
        throws java.sql.SQLException {
        return;
    }

    public String getUrl() {
        return "";
    }

    public String getUserName() {
        return "";
    }

    public String getPassword() {
        return "";
    }

    public void closeConnections() {
        return;
    }

    public void freeConnections() {
        return;
    }

    public java.sql.Connection getConnection()
        throws java.sql.SQLException {
        try {
            Context ctx = new InitialContext();

            DataSource ds = (DataSource) ctx.lookup(defaultDataSource);

            java.sql.Connection conn = ds.getConnection();

            ctx.close();

            return conn;
        } catch (NamingException e) {
            throw new java.sql.SQLException("Caught NamingException: " + e.toString());
        }
    }

    public void setConnectionPoolSize(int num) throws java.sql.SQLException {
        cat.warn("Ignoring set connection pool size " + num + "; this is not a pooled driver.");
    }

    public void setDriverSpecificParameter(String name, String value)
        throws java.sql.SQLException {
        cat.warn("Ignoring driver specific parameter " + name);
    }
}
