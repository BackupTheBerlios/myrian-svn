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

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.sql.SQLException;

public class ConnectionManagerTest extends TestCase {

    public static final String versionId = "$Id: //core-platform/test-qgen/test/src/com/arsdigita/db/ConnectionManagerTest.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    private String ORACLE_DATE_QUERY = "select sysdate from dual";
    private String POSTGRES_DATE_QUERY = "select 'now'::timestamp";

    private static java.sql.Connection conn = null;

    public ConnectionManagerTest(String name) {
        super(name);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(ConnectionManagerTest.class);
    }

    protected void setUp() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new ConnectionManagerTest("testGetConnection"));
        return suite;
    }

    public void testGetConnection() throws SQLException {
        conn = ConnectionManager.getConnection();

        try {
            assertNotNull(conn);

            String dateQuery = null;
            if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                dateQuery = POSTGRES_DATE_QUERY;
            } else {
                dateQuery = ORACLE_DATE_QUERY;
            }

            java.sql.PreparedStatement stmt =
                conn.prepareStatement(dateQuery);
            try {
                java.sql.ResultSet rs = stmt.executeQuery();
                try {
                    if (rs.next()) {
                        String date = rs.getString(1);
                        assertNotNull(date);
                    } else {
                        fail("Empty result set from sysdate query");
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } finally {
            ConnectionManager.returnConnection(conn);            
        }
    }
}
