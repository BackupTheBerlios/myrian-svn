/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
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
 * @version $Revision: #26 $ $Date: 2004/08/16 $
 * @since 4.5
 *
 */

public class ConnectionManager {

    public static final String versionId = "$Author: dennis $ - $Date: 2004/08/16 $ $Id: //core-platform/dev/src/com/arsdigita/db/ConnectionManager.java#26 $";

    private static final Logger LOG =
        Logger.getLogger(ConnectionManager.class);

    /**
     * Gets a jdbc connection.
     *
     * @deprecated Use {@link Session#getConnection()} instead.
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
     * @deprecated Use {@link Session#getConnection()} instead.
     **/
    public static java.sql.Connection getCurrentThreadConnection() {
        return SessionManager.getSession().getConnection();
    }

}
