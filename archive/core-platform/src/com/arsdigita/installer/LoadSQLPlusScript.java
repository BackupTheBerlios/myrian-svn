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
 * $Id: //core-platform/dev/src/com/arsdigita/installer/LoadSQLPlusScript.java#6 $
 *
 *  This is the class with sole purpose to feed SQL*Plus script through
 *  JDBC interface.  SQL*Plus scripts are being parsed by SimpleOracleSQLParser,
 *  and the extracted statements are then being executed one at the time.
 *
 */

package com.arsdigita.installer;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.FileNotFoundException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;

public class LoadSQLPlusScript {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/installer/LoadSQLPlusScript.java#6 $ by $Author: dennis $, $DateTime: 2002/08/14 16:49:53 $";

    private static final Logger s_log =
        Logger.getLogger(LoadSQLPlusScript.class);

    private Connection s_con;
    private Statement s_stmt;
    private int s_stmtCount = 0;
    private boolean s_onErrorContinue = false;
    private boolean s_echoSQLStatement = false;
    private int s_exitValue = 0;
    private String s_database = "oracle";

    public static void main (String args[]) {

        ConsoleAppender log = new ConsoleAppender();
        BasicConfigurator.configure(log);

        if (args.length != 4) {
            s_log.error (
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
            loader.setConnection (jdbcUrl, dbUsername, dbPassword);
            loader.loadSQLPlusScript(scriptFilename);
        } catch (Exception e) {
            s_log.error (e.toString());
            System.exit(1);
        }

        System.exit( loader.getExitValue() );

    }

    public void setDatabase (String database) {
        s_database = database;
    }

    public void setConnection (Connection connection) {
        s_con = connection;
    }

    public void setConnection (String jdbcUrl, String dbUsername, String dbPassword) 
        throws SQLException {
        s_con = DriverManager.getConnection(jdbcUrl, 
                                            dbUsername,
                                            dbPassword);
    }

    public int getExitValue () {
        return s_exitValue;
    }

    public void loadSQLPlusScript (String scriptFilename)
        throws ClassNotFoundException, 
        SQLException,
        ParseException, 
        FileNotFoundException,
        IllegalAccessException, 
        NoSuchMethodException {

        if (System.getProperty("sql.verbose") != null  &&
                System.getProperty("sql.verbose").equals("true")) {
            s_echoSQLStatement = true;
        }

        if (System.getProperty("sql.continue") != null  &&
                System.getProperty("sql.continue").equals("true")) {
            s_onErrorContinue = true;
        }

        m_loadSQLPlusScript(scriptFilename);
    }

    public void loadSQLPlusScript (String scriptFilename,
                                   boolean echoSQLStatement, 
                                   boolean onErrorContinue)
        throws ClassNotFoundException, SQLException,
                ParseException, FileNotFoundException,
                IllegalAccessException, NoSuchMethodException {
        s_echoSQLStatement = echoSQLStatement;
        s_onErrorContinue = onErrorContinue;
        m_loadSQLPlusScript (scriptFilename);
    }

    protected void m_loadSQLPlusScript (String scriptFilename)
        throws ClassNotFoundException, SQLException,
                ParseException, FileNotFoundException,
                IllegalAccessException, NoSuchMethodException {
        
        if (s_database == "postgres") {
            Class.forName("org.postgresql.Driver");
        } else {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        
        // Parse SQL script and feed JDBC with one statement at the time
        s_log.warn ("Trying to open: '" + scriptFilename + "'");
        SimpleSQLParser parser = new SimpleSQLParser(scriptFilename);
        
        // iterate over parser.SQLStamentList();

        s_stmt = s_con.createStatement();

        try {
            parser.useSQLStatement(this, "executeOneStatement");
        } catch (InvocationTargetException e) {
        }

        s_stmt.close();
        s_con.close();

    }


    /**
     *  This is a method which will be passed to the database SQL parser to
     *  be executed for each parsed statement.
     *  It must accept single String argument.
     *  It must be public (because it will be called by other
     *  classes.)
     */

    public void executeOneStatement (String sqlString) 
        throws SQLException {
        s_stmtCount++;
        s_log.info ("Statement count: " + s_stmtCount);
        if (s_echoSQLStatement) {
            s_log.info (sqlString);
        }

        try {
            int rowsAffected = s_stmt.executeUpdate(sqlString);
            s_log.warn ("  " + rowsAffected + " row(s) affected");
        } catch (SQLException e) {
            s_exitValue = 1;
            s_log.warn (" -- FAILED: " + e.getMessage());
            if (!s_onErrorContinue) {
                throw e;
            }
        }
    }
}
