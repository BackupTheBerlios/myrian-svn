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

package com.arsdigita.db.postgres;

import com.arsdigita.db.BaseConnectionPool;
import com.arsdigita.db.SQLExceptionHandler;

import java.sql.SQLException;

import org.apache.log4j.Category;

/**
 * Connection pooling class for PosgreSQL databases.
 *
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/db/postgres/PostgresConnectionPoolImpl.java#2 $ $DateTime: 2002/07/17 14:30:02 $
 * @since 4.5
 * 
 */

public class PostgresConnectionPoolImpl extends BaseConnectionPool {

    public static final String versionId = "$Author: randyg $ - $Date: 2002/07/17 $ $Id: //core-platform/dev/src/com/arsdigita/db/postgres/PostgresConnectionPoolImpl.java#2 $";

    private static final Category s_log = Category.getInstance(PostgresConnectionPoolImpl.class.getName());

    public void setConnectionInfo(String url, String username,
                                  String password) throws SQLException {
        super.setConnectionInfo(url, username, password);
        try {
            Class.forName("org.postgresql.Driver");
        } catch ( ClassNotFoundException cnfe ) {
            SQLExceptionHandler.throwSQLException("Cannot load postgres " + 
                                                  "driver - class not found.");
            // we don't *need* a bogus "throw e" call here 
            // since this is a void method.  Since cnfe isn't declared
            // to be thrown, we'll just leave the "throw e" out.
        } 
    }

    public java.sql.Connection getNewConnection() throws java.sql.SQLException {

        try {
            java.sql.Connection conn =
                java.sql.DriverManager.getConnection(m_url, m_user, m_password);
            return conn;
        } catch (SQLException e) {
            s_log.error(e);
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



