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

/**
 * $Id: //core-platform/dev/src/com/arsdigita/installer/TestJDBCSetup.java#3 $
 *
 *  Simple class which tests JDBC connection.  This currently works
 *  for either postgres or oracle.  The class must be modified to work
 *  with any other database
 *
 */

package com.arsdigita.installer;

import java.sql.*;

public class TestJDBCSetup {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/installer/TestJDBCSetup.java#3 $ by $Author: randyg $, $DateTime: 2002/07/19 08:51:21 $";

    public static void main (String args[]) {

        if (args.length != 3) {
            System.err.println(
                "Usage: $0 TestJDBCSetup " +
                "<JDBC_URL> <username> <password>");
            System.exit(1);
        }

        String jdbcUrl = args[0];
        String dbUsername = args[1];
        String dbPassword = args[2];

        try {
            testJDBCSetup(jdbcUrl, dbUsername, dbPassword);
        } catch (Exception e) {
            System.err.println(e.toString());
            System.exit(1);
        }
        System.out.println("Success");
        System.exit(0);

    }


    static void testJDBCSetup (String jdbcUrl, String dbUsername,
                                String dbPassword)
        throws ClassNotFoundException, SQLException
    {

        int rowCount = 0;

        String result = "";
        
        String sql = null;
        if (jdbcUrl.indexOf("postgres") > -1) {
            Class.forName("org.postgresql.Driver");
            sql = "select 'success' as result";
        } else {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            sql = "SELECT 'success' AS result FROM dual";
        }

        Connection con = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

        Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            rowCount++;
            result = rs.getString("RESULT");
        }

        rs.close();
        stmt.close();
        con.close();

        if (rowCount != 1) {
            throw new SQLException("Database returned " + rowCount +
                  " rows, instead of exactly one.");
        }

        if (!result.equals("success")) {
            throw new SQLException("Database returned '" + result +
                  "', instead of 'success'.");
        }

    }

}
