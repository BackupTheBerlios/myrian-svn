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
 * $Id: //core-platform/dev/src/com/arsdigita/installer/ExecuteSQLStatement.java#2 $
 *
 *  Simple class which executes single SQL statement not returning
 *  rows.
 *
 */

package com.arsdigita.installer;

import java.sql.*;

public class ExecuteSQLStatement {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/installer/ExecuteSQLStatement.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    static public void main (String args[]) {

        if (args.length != 4) {
            System.err.println(
                "Usage: $0 ExecuteSQLStatement " +
                "<JDBC_URL> <username> <password> <SQL>");
            System.exit(1);
        }

        String jdbcUrl = args[0];
        String dbUsername = args[1];
        String dbPassword = args[2];
        String sqlString = args[3];

        try {
            executeSQL(jdbcUrl, dbUsername, dbPassword, sqlString);
        } catch (Exception e) {
            System.err.println(e.toString());
            System.exit(1);
        }

        System.exit(0);

    }


    private static void executeSQL (String jdbcUrl, String dbUsername,
                                String dbPassword, String sqlString)
        throws ClassNotFoundException, SQLException
    {

        Class.forName("oracle.jdbc.driver.OracleDriver");

        Connection con = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

        Statement stmt = con.createStatement();

        stmt.executeUpdate(sqlString);

        stmt.close();
        con.close();

    }

}
