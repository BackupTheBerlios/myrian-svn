/*
* Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.db.DbHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class LoadSQLPlusScript {

    public static final String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/installer/LoadSQLPlusScript.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

    private static final Logger s_log =
            Logger.getLogger(LoadSQLPlusScript.class);

    private Connection m_con;
    private Statement m_stmt;
    private int m_stmtCount = 0;
    private boolean m_onErrorContinue = false;
    private boolean m_echoSQLStatement = false;
    private int m_exitValue = 0;

    public static void main (String args[]) {
        BasicConfigurator.configure();

        if (args.length != 4) {
            s_log.error("Usage: LoadSQLPlusScript " +
                    "<JDBC_URL> <username> <password> <script_filename>");
            System.exit(1);
        }

        String jdbcUrl = args[0];
        String dbUsername = args[1];
        String dbPassword = args[2];
        String scriptFilename = args[3];

        LoadSQLPlusScript loader = new LoadSQLPlusScript();
        loader.setConnection (jdbcUrl, dbUsername, dbPassword);
        loader.loadSQLPlusScript(scriptFilename, true, true);
        System.exit(loader.getExitValue());
    }

    public void setConnection (Connection connection) {
        m_con = connection;
    }

    public void setConnection (String jdbcUrl, String dbUsername,
                               String dbPassword) {
        try {
            int db = DbHelper.getDatabaseFromURL(jdbcUrl);

            switch (db) {
                case DbHelper.DB_POSTGRES:
                    Class.forName("org.postgresql.Driver");
                    break;
                case DbHelper.DB_ORACLE:
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    break;
                default:
                    throw new IllegalArgumentException("unsupported database");
            }

            s_log.warn("Using database " + DbHelper.getDatabaseName(db));
            m_con = DriverManager.getConnection(jdbcUrl, dbUsername,
                    dbPassword);
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        } catch (ClassNotFoundException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    public int getExitValue () {
        return m_exitValue;
    }

    public void loadSQLPlusScript (String scriptFilename) {
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

    public void loadSQLPlusScript(String scriptFilename,
                                  boolean echoSQLStatement,
                                  boolean onErrorContinue) {
        m_echoSQLStatement = echoSQLStatement;
        m_onErrorContinue = onErrorContinue;
        loadScript (scriptFilename);
    }

    protected void loadScript(String scriptFilename) {
        // Parse SQL script and feed JDBC with one statement at the time
        s_log.warn ("Loading: '" + scriptFilename + "'");
        try {
            m_stmt = m_con.createStatement();
            load(scriptFilename);
            m_stmt.close();
            m_con.commit();
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    private void load(final String filename) {
        try {
            StatementParser sp = new StatementParser
                    (filename, new FileReader(filename),
                            new StatementParser.Switch() {
                                public void onStatement(String sql) {
                                    executeStatement(sql);
                                }
                                public void onInclude(String include) {
                                    include(filename, include);
                                }
                            });
            sp.parse();
        } catch (ParseException e) {
            throw new UncheckedWrapperException(e);
        } catch (FileNotFoundException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    private void include(String including, String included) {
        File includedFile = new File(included);
        if (includedFile.isAbsolute()) {
            s_log.warn("Absolute path found: '" + included + "'");
        } else {
            s_log.warn("Relative path found: '" + included + "'");
            //  Well make it absolute then.
            includedFile =
                    new File(new File(including).getAbsoluteFile()
                    .getParentFile(), included).getAbsoluteFile();
        }
        s_log.warn("Recursively including: '" + includedFile + "'");
        load(includedFile.toString());
    }

    private void executeStatement (String sql) {
        m_stmtCount++;
        s_log.info ("Statement count: " + m_stmtCount);
        if (m_echoSQLStatement) {
            s_log.info (sql);
        }

        try {
            int rowsAffected = m_stmt.executeUpdate(sql);
            s_log.warn ("  " + rowsAffected + " row(s) affected");
            m_con.commit();
        } catch (SQLException e) {
            try {
                m_con.rollback();
            } catch (SQLException se) {
                throw new UncheckedWrapperException(se);
            }
            m_exitValue = 1;
            s_log.error(" -- FAILED: " + e.getMessage());
            s_log.error("SQL: " + sql);
            if (!m_onErrorContinue) {
                throw new UncheckedWrapperException(e);
            }
        }
    }

}
