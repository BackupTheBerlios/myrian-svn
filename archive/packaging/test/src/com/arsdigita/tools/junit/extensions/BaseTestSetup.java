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

package com.arsdigita.tools.junit.extensions;

import com.arsdigita.util.*;
import com.arsdigita.util.config.*;
import com.arsdigita.util.parameter.*;
import com.arsdigita.db.*;
import com.arsdigita.persistence.*;
import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.pdl.*;

import java.sql.*;
import java.io.*;

import com.arsdigita.db.ConnectionManager;
import com.arsdigita.installer.LoadSQLPlusScript;
import com.arsdigita.util.DummyServletContext;
import com.arsdigita.util.ResourceManager;
import junit.extensions.TestDecorator;
import junit.framework.Protectable;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A Decorator to set up and tear down additional fixture state.
 * Subclass BaseTestSetup and insert it into your tests when you want
 * to set up additional state once before the tests are run.
 */
public class BaseTestSetup extends TestDecorator {

    private static final TestConfig CONFIG = new TestConfig();

    private boolean m_performInitialization = true;
    private String m_scriptName;
    private String m_iniName;
    private TestSuite m_suite;

    private List m_setupSQLScripts = new LinkedList();
    private List m_teardownSQLScripts = new LinkedList();

    public BaseTestSetup(Test test, TestSuite suite) {
        super(test);
        m_suite = suite;
    }

    public BaseTestSetup(TestSuite suite) {
        this(suite, suite);
    }

    public void run(final TestResult result) {
        Protectable p= new Protectable() {
            public void protect() throws Exception {
                setUp();
                basicRun(result);
                tearDown();
            }
        };
        result.runProtected(this, p);
    }

    public void addSQLSetupScript(String setupSQLScript) {
        m_setupSQLScripts.add(setupSQLScript);
    }
    public void addSQLTeardownScript(String teardown) {
        m_teardownSQLScripts.add(teardown);
    }

    public void setSetupSQLScript(String setupSQLScript) {
        m_setupSQLScripts.add(setupSQLScript);
    }


    public void setTeardownSQLScript(String teardownSQLScript) {
        m_teardownSQLScripts.add(teardownSQLScript);
    }

    public void setInitScript(String scriptName) {
        m_scriptName = scriptName;
    }

    public String getInitScript() {
        return m_scriptName;
    }

    public void setInitScriptTarget(String iniName) {
        m_iniName = iniName;
    }

    public String getInitScriptTarget() {
        return m_iniName;
    }

    public void setPerformInitialization(boolean performInitialization) {
        m_performInitialization = performInitialization;
    }

    public boolean getPerformInitialization() {
        return m_performInitialization;
    }

    private Connection getConnection() {
        String jdbc = CONFIG.getURL();
        String user = CONFIG.getUser();
        String password = CONFIG.getPassword();
        int database = CONFIG.getDatabase();

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
                throw new IllegalArgumentException("unsupported database");
            }

            Connection conn =
                DriverManager.getConnection(jdbc, user, password);
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

    private void openSession() {
        String pdl = CONFIG.getPDL();
        int database = CONFIG.getDatabase();
        DbHelper.setDatabase(database);

        Connection conn = getConnection();
        ConnectionSource source = new DedicatedConnectionSource(conn);
        MetadataRoot root = PDL.loadDirectory(new File(pdl));
        SessionManager.open("default", root, source, database);
    }

    /**
     * Sets up the fixture. Override to set up additional fixture
     * state.
     */

    protected void setUp() throws Exception {
        if ( m_suite.testCount() > 0 ) {
            openSession();
            if (m_performInitialization) {
                ResourceManager.getInstance().setServletContext(new DummyServletContext());
                Initializer.startup(m_suite, m_scriptName, m_iniName);
            }
            setupSQL();
        }
    }

    /**
     * Tears down the fixture. Override to tear down the additional
     * fixture state.
     */
    protected void tearDown() throws Exception {
        if ( m_suite.testCount() > 0 ) {
            teardownSQL ();
            if (m_performInitialization) {
                Initializer.shutdown();
            }
        }
    }

    protected void setupSQL () throws Exception {
        if (m_setupSQLScripts.size() > 0) {
            runScripts(m_setupSQLScripts);
        }
    }


    protected void teardownSQL() throws Exception {
        if (m_teardownSQLScripts.size() > 0) {
            runScripts(m_teardownSQLScripts);
        }
    }

    private void runScripts(List scripts) throws Exception {
        LoadSQLPlusScript loader = new LoadSQLPlusScript();
        Connection conn = getConnection();
        loader.setConnection(conn);
        for (Iterator iterator = scripts.iterator(); iterator.hasNext();) {
            String script = (String) iterator.next();
            loader.loadSQLPlusScript(script);
        }
        try {
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    private static class TestConfig extends BaseConfig {
        private String m_url;
        private String m_user;
        private String m_password;
        private String m_pdl;
        private int m_database;

        TestConfig() {
            super("/test.properties");

            m_url = (String) initialize
                (new JDBCURLParameter("waf.test.jdbc.url"));
            m_user = (String) initialize
                (new StringParameter("waf.test.jdbc.user"));
            m_password = (String) initialize
                (new StringParameter("waf.test.jdbc.password"));
            m_pdl = (String) initialize
                (new StringParameter("waf.test.pdl"));

            m_database = DbHelper.getDatabaseFromURL(m_url);
        }

        public String getURL() {
            return m_url;
        }

        public String getUser() {
            return m_user;
        }

        public String getPassword() {
            return m_password;
        }

        public String getPDL() {
            return m_pdl;
        }

        public int getDatabase() {
            return m_database;
        }
    }

}
