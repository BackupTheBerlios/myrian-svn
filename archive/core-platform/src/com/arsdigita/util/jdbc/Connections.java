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

import com.arsdigita.db.*;
import com.arsdigita.util.*;
import java.sql.*;
import org.apache.log4j.Logger;

/**
 * A collection of static utility methods for dealing with JDBC
 * connections.
 *
 * @author Justin Ross
 */
public final class Connections {
    public static final String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/jdbc/Connections.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/26 15:31:04 $";

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

                SQLExceptionHandler.setDbExceptionHandlerImplName
                    ("com.arsdigita.db.postgres.PostgresDbExceptionHandlerImpl");

                break;
            case DbHelper.DB_ORACLE:
                Classes.loadClass("oracle.jdbc.driver.OracleDriver");

                SQLExceptionHandler.setDbExceptionHandlerImplName
                    ("com.arsdigita.db.oracle.OracleDbExceptionHandlerImpl");

                break;
            default:
                throw new IllegalArgumentException("Unsupported database");
            }

            final Connection conn = DriverManager.getConnection(url);

            Assert.exists(conn, Connection.class);

            conn.setAutoCommit(false);

            return conn;
        } catch (ClassNotFoundException e) {
            throw new UncheckedWrapperException(e);
        } catch (InstantiationException e) {
            throw new UncheckedWrapperException(e);
        } catch (IllegalAccessException e) {
            throw new UncheckedWrapperException(e);
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }
}
