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

import junit.framework.*;
import junit.extensions.*;

import java.io.*;
import java.sql.SQLException;

/**
 * This test verifies the exception-type-narrowing functionality
 * of the DbExceptionHandler
 *
 * @author <A HREF="mailto:eison@arsdigita.com">David Eison</A>
 */
public class DbExceptionHandlerTest extends TestCase {

    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/db/DbExceptionHandlerTest.java#5 $";

    private static java.sql.Connection conn;

    private static final String dirRoot =
        System.getProperty("test.base.dir") +
        "/com/arsdigita/db/";
    private static final String blobFileName = "adlogo.gif";

    public DbExceptionHandlerTest(String name) {
        super(name);
    }

    /**
     * Technically, this code beyonds in a suite setup and teardown..
     * Unfortunately, I can't quite figure out how to make that work
     * right w/ the rest of our test architecture, so we'll stick them
     * here for now.
     */
    public void setUp() throws SQLException {
        conn = ConnectionManager.getConnection();

        java.sql.PreparedStatement tableStmt =
            conn.prepareStatement(
                                  "create table db_test (\n" +
                                  "    theId          integer primary key\n" +
                                  ")");

        tableStmt.executeUpdate();
        tableStmt.close();
    }

    public void tearDown() throws SQLException {
        java.sql.PreparedStatement stmt;
        stmt = conn.prepareStatement("drop table db_test");
        stmt.executeUpdate();
        stmt.close();
    }

    public void testUniqueConstraintException() throws SQLException {
        java.sql.PreparedStatement insertStmt =
            conn.prepareStatement("insert into db_test\n" +
                                  "(theId)\n" +
                                  "values\n" +
                                  "(1)");
        insertStmt.executeUpdate();
        try {
            insertStmt.executeUpdate();
            fail("Unique constraint violation should have caused error");
        } catch (UniqueConstraintException e) {
            // good
        } catch (SQLException e) {
            // bad
            fail("Unique constraint violation should have caused " +
                 "UniqueConstraintException, instead caused " + e);
        }
        insertStmt.close();
    }

    public void testDbNotAvailableException() throws SQLException {
        // TODO: Figure out other ways to simulate DB failure
        // than just garbage connection info?  Is this possible?

        java.sql.PreparedStatement insertStmt = null;
        java.sql.Connection badConn = null;
        try {
            badConn = java.sql.DriverManager.getConnection("total garbage");
            fail("Using garbage connection info should have caused error");
        } catch (SQLException e) {
            // Normally the job of parsing the SQLException and re-throwing
            // the right type is handled by code in the pool or buried in DB.
            // However, here we're using straight JDBC, so we need to explicitly
            // cause the parsing to happen.
            SQLException wrapped = SQLExceptionHandler.wrap(e);
            if (wrapped instanceof DbNotAvailableException) {
                // good
            } else {
                throw new com.arsdigita.util.UncheckedWrapperException
                    (wrapped);
                /*
                fail("Setting garbage connection info should have caused " +
                "DbNotAvailableException, instead caused " + wrapped);*/
            }
        }

        try {
            badConn = java.sql.DriverManager.getConnection("jdbc:totalgarbage");
            fail("Using garbage connection info should have caused error");
        } catch (SQLException e) {
            // Normally the job of parsing the SQLException and re-throwing
            // the right type is handled by code in the pool or buried in DB.
            // However, here we're using straight JDBC, so we need to explicitly
            // cause the parsing to happen.
            SQLException wrapped = SQLExceptionHandler.wrap(e);

            if (wrapped instanceof DbNotAvailableException) {
                // good
            } else {
                fail("Setting garbage connection info should have caused " +
                     "DbNotAvailableException, instead caused " + wrapped);
            }
        }

        try {
            badConn = java.sql.DriverManager.getConnection("jdbc:oracle:garbage");
            fail("Using garbage connection info should have caused error");
        } catch (SQLException e) {
            // Normally the job of parsing the SQLException and re-throwing
            // the right type is handled by code in the pool or buried in DB.
            // However, here we're using straight JDBC, so we need to explicitly
            // cause the parsing to happen.
            SQLException wrapped = SQLExceptionHandler.wrap(e);
            if (wrapped instanceof DbNotAvailableException) {
                // good
            } else {
                fail("Setting garbage connection info should have caused " +
                     "DbNotAvailableException, instead caused " + wrapped);
            }
        }

        try {
            badConn = java.sql.DriverManager.getConnection("jdbc:oracle:oci8:@totalgarbage");
            insertStmt =
                badConn.prepareStatement("insert into db_test\n" +
                                         "(theId)\n" +
                                         "values\n" +
                                         "(1)");
            insertStmt.executeUpdate();
            fail("Using garbage connection info should have caused error");
        } catch (SQLException e) {
            // Normally the job of parsing the SQLException and re-throwing
            // the right type is handled by code in the pool or buried in DB.
            // However, here we're using straight JDBC, so we need to explicitly
            // cause the parsing to happen.
            SQLException wrapped = SQLExceptionHandler.wrap(e);
            if (wrapped instanceof DbNotAvailableException) {
                // good
            } else {
                fail("Setting garbage connection info should have caused " +
                     "DbNotAvailableException, instead caused " + wrapped);
            }
        }
    }
}
