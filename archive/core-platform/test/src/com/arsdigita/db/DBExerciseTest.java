/*
 * Copyright (C) 2001, 2002, 2003, 2003 Red Hat Inc. All Rights Reserved.
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
 * This test exists to exercise the database and the sundry JDBC
 * methods.  It should ultimately be extended to include most or
 * all such methods.
 *
 * @author Kevin Scaldeferri
 */


public class DBExerciseTest extends TestCase {

    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/db/DBExerciseTest.java#6 $ by $Author: dennis $, $DateTime: 2003/08/15 13:46:34 $";

    private static java.sql.Connection conn;

    private static final String dirRoot =
        System.getProperty("test.base.dir") +
        "/com/arsdigita/db/";
    private static final String blobFileName = "adlogo.gif";

    public DBExerciseTest(String name) {
        super(name);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(DBExerciseTest.class);
    }

    protected void setUp() {

    }

    public static Test suite() throws SQLException {
        TestSuite suite = new TestSuite();
        suite.addTest(new DBExerciseTest("testBlob"));

        TestSetup wrapper = new TestSetup(suite) {
                public void setUp() throws SQLException {
                    conn = ConnectionManager.getConnection();
                    java.sql.PreparedStatement tableStmt = null;

                    if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                        tableStmt = conn.prepareStatement
                            ("create table db_test (\n" +
                             "    theId          integer primary key,\n" +
                             "    aBlob          bytea\n" +
                             ")");
                    } else {
                        tableStmt = conn.prepareStatement
                            ("create table db_test (\n" +
                             "    theId          integer primary key,\n" +
                             "    aBlob          blob\n" +
                             ")");
                    }
                    tableStmt.executeUpdate();
                    tableStmt.close();
                }

                public void tearDown() throws SQLException {
                    java.sql.PreparedStatement stmt;
                    stmt = conn.prepareStatement("drop table db_test");
                    stmt.executeUpdate();
                    stmt.close();
                }

            };

        return wrapper;
    }

    public static Test makeWrapper() throws SQLException
    {
        return suite();
    }
    public void testBlob() {
        try {
            java.sql.PreparedStatement blobInsertStmt =
                conn.prepareStatement("insert into db_test\n" +
                                      "(theId, aBlob)\n" +
                                      "values\n" +
                                      "(?,?)");

            // might not be the right location
            File blobFile = new File(dirRoot, blobFileName);
            long fileSize = blobFile.length();
            byte[] blobBytes = new byte[(int) fileSize];

            DataInputStream in = new DataInputStream(new FileInputStream(blobFile));
            in.readFully(blobBytes);
            in.close();

            blobInsertStmt.setInt(1, 1);
            blobInsertStmt.setBytes(2,blobBytes);
            blobInsertStmt.executeUpdate();
            blobInsertStmt.close();

            java.sql.PreparedStatement blobRetrieveStmt =
                conn.prepareStatement("select aBlob\n" +
                                      "from db_test\n" +
                                      "where theId = 1");

            java.sql.ResultSet rs = blobRetrieveStmt.executeQuery();

            if (rs.next()) {
                long size = 0;
                if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                    size = (new Integer(rs.getBytes(1).length)).longValue();
                } else {
                    java.sql.Blob blob = rs.getBlob(1);
                    size = blob.length();
                }
                assertEquals(fileSize, size);
            } else {
                fail("Didn't find row we just inserted");
            }
            rs.close();
            blobRetrieveStmt.close();

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
