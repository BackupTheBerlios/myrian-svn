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

package com.arsdigita.db.postgres;

import com.arsdigita.db.BaseConnectionPool;
import com.arsdigita.db.SQLExceptionHandler;
import com.arsdigita.db.DbException;

import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Connection pooling class for PosgreSQL databases.
 *
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/db/postgres/PostgresConnectionPoolImpl.java#7 $ $DateTime: 2002/10/04 18:08:01 $
 * @since 4.5
 *
 */

public class PostgresConnectionPoolImpl extends BaseConnectionPool {

    public static final String versionId = "$Author: rhs $ - $Date: 2002/10/04 $ $Id: //core-platform/dev/src/com/arsdigita/db/postgres/PostgresConnectionPoolImpl.java#7 $";

    private static final Logger s_log = Logger.getLogger(PostgresConnectionPoolImpl.class.getName());

    public void setConnectionInfo(String url, String username,
                                  String password) throws SQLException {
        super.setConnectionInfo(url, username, password);
        try {
            Class.forName("org.postgresql.Driver");
        } catch ( ClassNotFoundException cnfe ) {
            throw new DbException
                ("Cannot load postgres driver - class not found.");
        }
    }

    public java.sql.Connection getNewConnection() throws java.sql.SQLException {

        try {
            java.sql.Connection conn =
                java.sql.DriverManager.getConnection(m_url, m_user, m_password);
            return conn;
        } catch (SQLException e) {
            s_log.error(e);
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
