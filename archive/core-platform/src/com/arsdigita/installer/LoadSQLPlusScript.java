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
 * $Id: //core-platform/dev/src/com/arsdigita/installer/LoadSQLPlusScript.java#1 $
 *
 *  This is the class with sole purpose to feed SQL*Plus script through
 *  JDBC interface.  SQL*Plus scripts are being parsed by SimpleOracleSQLParser,
 *  and the extracted statements are then being executed one at the time.
 *
 */

package com.arsdigita.installer;

import java.sql.*;
import java.io.*;
import java.lang.reflect.*;

public class LoadSQLPlusScript {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/installer/LoadSQLPlusScript.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private Connection s_con;
    private Statement s_stmt;
    private int s_stmtCount = 0;
    private boolean s_onErrorContinue = false;
    private boolean s_echoSQLStatement = false;
    private int s_exitValue = 0;
    
    public LoadSQLPlusScript () {
    }

    public static void main (String args[]) {

        if (args.length != 4) {
            System.err.println(
                "Usage: LoadSQLPlusScript " +
                "<JDBC_URL> <username> <password> <script_filename>");
            System.exit(1);
        }

        String jdbcUrl = args[0];
        String dbUsername = args[1];
        String dbPassword = args[2];
        String scriptFilename = args[3];

        LoadSQLPlusScript loader = new LoadSQLPlusScript();

        try {
            loader.loadSQLPlusScript(jdbcUrl, dbUsername, dbPassword, scriptFilename);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            System.err.println(t.toString());
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.toString());
            System.exit(1);
        }

        System.exit( loader.getExitValue() );

    }

    public void setConnection (Connection connection) {
        this.s_con = connection;
    }

    public int getExitValue () {
        return s_exitValue;
    }

    public void loadSQLPlusScript (String jdbcUrl, 
                                   String dbUsername,
                                   String dbPassword, 
                                   String scriptFilename)
        throws ClassNotFoundException, 
        SQLException,
        ParseException, 
        FileNotFoundException,
        IllegalAccessException, 
        NoSuchMethodException,
        InvocationTargetException {

        if (System.getProperty("sql.verbose") != null  &&
                System.getProperty("sql.verbose").equals("true")) {
            s_echoSQLStatement = true;
        }

        if (System.getProperty("sql.continue") != null  &&
                System.getProperty("sql.continue").equals("true")) {
            s_onErrorContinue = true;
        }

        m_loadSQLPlusScript(jdbcUrl, dbUsername, dbPassword, scriptFilename);
    }

    public void loadSQLPlusScript (String jdbcUrl, 
                                   String dbUsername,
                                   String dbPassword, 
                                   String scriptFilename,
                                   boolean echoSQLStatement, 
                                   boolean onErrorContinue)
        throws ClassNotFoundException, SQLException,
                ParseException, FileNotFoundException,
                IllegalAccessException, NoSuchMethodException,
                InvocationTargetException {
        s_echoSQLStatement = echoSQLStatement;
        s_onErrorContinue = onErrorContinue;
        m_loadSQLPlusScript (jdbcUrl, dbUsername, dbPassword, scriptFilename);
    }

    protected void m_loadSQLPlusScript (String jdbcUrl, 
                                        String dbUsername,
                                        String dbPassword, 
                                        String scriptFilename)
        throws ClassNotFoundException, SQLException,
                ParseException, FileNotFoundException,
                IllegalAccessException, NoSuchMethodException,
                InvocationTargetException {

        Class.forName("oracle.jdbc.driver.OracleDriver");

        if ( s_con == null ) {
            s_con = DriverManager.getConnection(jdbcUrl, 
                                                dbUsername,
                                                dbPassword);
        }

        // Parse SQL script and feed JDBC with one statement at the time

        System.err.println("Trying to open: '" + scriptFilename + "'");
        SimpleOracleSQLParser parser =
                new SimpleOracleSQLParser(scriptFilename);
        // iterate over parser.SQLStamentList();

        s_stmt = s_con.createStatement();

        parser.useSQLStatement(this, "executeOneStatement");

        s_stmt.close();
        s_con.close();

    }


    /**
     *  This is a method which will be passed to Oracle SQL parser to
     *  be executed for each parsed statement.
     *  It must accept single String argument.
     *  It must be public (because it will be called by other
     *  classes.)
     */

    public void executeOneStatement (String sqlString) 
        throws SQLException {
        s_stmtCount++;
        System.err.print("Statement count: " + s_stmtCount);
        if (s_echoSQLStatement) {
            System.err.println();
            System.err.println(sqlString);
        }

        try {
            int rowsAffected = s_stmt.executeUpdate(sqlString);
            System.err.println("  " + rowsAffected + " row(s) affected");
            //  If in verbose mode, add some whitespace for legibility
            if (s_echoSQLStatement) {
                System.err.println();
            }
        } catch (SQLException e) {
            s_exitValue = 1;
            System.err.println(" -- FAILED: " + e.getMessage());
            if (!s_onErrorContinue) {
                throw e;
            }
        }

    }
        
}
