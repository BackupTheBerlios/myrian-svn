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

package com.arsdigita.util.jdbc;

import com.arsdigita.db.DbHelper;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Classes;
import com.arsdigita.util.UncheckedWrapperException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * A collection of static utility methods for dealing with JDBC
 * connections.
 *
 * @author Justin Ross
 */
public final class Connections {
    public static final String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/jdbc/Connections.java#4 $" +
        "$Author: rhs $" +
        "$DateTime: 2003/11/21 10:51:18 $";

    private static final Logger s_log = Logger.getLogger(Connections.class);

    /**
     * Acquires a single connection using <code>url</code>.  This
     * method takes care of loading the appropriate driver and turning
     * auto-commit off.
     *
     * @param url A <code>String</code> JDBC URL
     */
    public static final Connection acquire(final String url) {
        Assert.exists(url);

        final int database = DbHelper.getDatabaseFromURL(url);

        try {
            switch (database) {
            case DbHelper.DB_POSTGRES:
                Classes.loadClass("org.postgresql.Driver");
                break;
            case DbHelper.DB_ORACLE:
                Classes.loadClass("oracle.jdbc.driver.OracleDriver");
                break;
            }

            final Connection conn = DriverManager.getConnection(url);

            Assert.exists(conn, Connection.class);

            conn.setAutoCommit(false);

	    // XXX Use connection metadata to find out if this is the
	    // bad oracle.  Do we need to do this this often?
	    //if (false) {
	    //    final PreparedStatement stmt = conn.prepareStatement
	    //        ("alter session set \"_push_join_union_view\" = false");
	    //    stmt.execute();
	    //}

            return conn;
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }
}
