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

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/installer/LoadSQLPlusScript.java#9 $ by $Author: jorris $, $DateTime: 2003/02/19 13:23:12 $";

    private static final Logger s_log =
        Logger.getLogger(LoadSQLPlusScript.class);

    private Connection m_con;
    private Statement m_stmt;
    private int m_stmtCount = 0;
    private boolean m_onErrorContinue = false;
    private boolean m_echoSQLStatement = false;
    private int m_exitValue = 0;
    private String m_database = "oracle";

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
        m_database = database;
    }

    public void setConnection (Connection connection) {
        m_con = connection;
    }

    public void setConnection (String jdbcUrl, String dbUsername, String dbPassword)
        throws SQLException {
        m_con = DriverManager.getConnection(jdbcUrl,
                                            dbUsername,
                                            dbPassword);
    }

    public int getExitValue () {
        return m_exitValue;
    }

    public void loadSQLPlusScript (String scriptFilename)
        throws ClassNotFoundException,
               SQLException,
               ParseException,
               FileNotFoundException,
               IllegalAccessException,
               NoSuchMethodException {
        /*
        if (System.getProperty("sql.verbose") != null  &&
            System.getProperty("sql.verbose").equals("true")) {
            m_echoSQLStatement = true;
        }

        if (System.getProperty("sql.continue") != null  &&
            System.getProperty("sql.continue").equals("true")) {
            m_onErrorContinue = true;
        }
        */
        m_echoSQLStatement = true;
        m_onErrorContinue = false;

        loadScript(scriptFilename);
    }

    public void loadSQLPlusScript (String scriptFilename,
                                   boolean echoSQLStatement,
                                   boolean onErrorContinue)
        throws ClassNotFoundException, SQLException,
               ParseException, FileNotFoundException,
               IllegalAccessException, NoSuchMethodException {
        m_echoSQLStatement = echoSQLStatement;
        m_onErrorContinue = onErrorContinue;
        loadScript (scriptFilename);
    }

    protected void loadScript (String scriptFilename)
        throws ClassNotFoundException, SQLException,
               ParseException, FileNotFoundException,
               IllegalAccessException, NoSuchMethodException {

        if (m_database.equals("postgres")) {
            Class.forName("org.postgresql.Driver");
        } else {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }

        s_log.warn("Using database " + m_database);
        // Parse SQL script and feed JDBC with one statement at the time
        s_log.warn ("Trying to open: '" + scriptFilename + "'");
        SimpleSQLParser parser = new SimpleSQLParser(scriptFilename);

        // iterate over parser.SQLStamentList();

        m_stmt = m_con.createStatement();

        try {
            parser.useSQLStatement(this, "executeOneStatement");
        } catch (InvocationTargetException e) {
        }

        m_stmt.close();
        m_con.commit();
        m_con.close();

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
        m_stmtCount++;
        s_log.info ("Statement count: " + m_stmtCount);
        if (m_echoSQLStatement) {
            s_log.info (sqlString);
        }

        try {
            int rowsAffected = m_stmt.executeUpdate(sqlString);
            s_log.warn ("  " + rowsAffected + " row(s) affected");
        } catch (SQLException e) {
            m_exitValue = 1;
            s_log.warn (" -- FAILED: " + e.getMessage());
            if (!m_onErrorContinue) {
                throw e;
            }
        }
    }
}
