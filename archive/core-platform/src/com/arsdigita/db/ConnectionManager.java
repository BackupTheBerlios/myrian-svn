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

import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 *
 * Central location for obtaining database connection.
 *
 * @author David Dao
 * @version $Revision: #22 $ $Date: 2003/11/21 $
 * @since 4.5
 *
 */

public class ConnectionManager {

    public static final String versionId = "$Author: rhs $ - $Date: 2003/11/21 $ $Id: //core-platform/dev/src/com/arsdigita/db/ConnectionManager.java#22 $";

    private static final Logger LOG =
        Logger.getLogger(ConnectionManager.class);

    /**
     * Gets a jdbc connection.
     *
     * @deprecated Use {@link Session.getConnection()} instead.
     **/
    public static java.sql.Connection getConnection()
        throws java.sql.SQLException {
        return SessionManager.getSession().getConnection();
    }


    /**
     * Returns a connection to the connection pool. Anytime code calls
     * getConnection(), it needs to call this method when it is done
     * with the connection
     *
     * @param conn the connection to return
     * @throws java.sql.SQLException
     * @deprecated Connections acquired through
     * Session.getConnection() will automatically be returned to the
     * pool at the end of the transaction.
     **/
    public static void returnConnection(Connection conn)
        throws java.sql.SQLException {
        // do nothing
    }

    /**
     * Returns the connection presently in use by this thread.
     * @deprecated Use {@link Session.getConnection()} instead.
     **/
    public static java.sql.Connection getCurrentThreadConnection() {
        return SessionManager.getSession().getConnection();
    }

}
