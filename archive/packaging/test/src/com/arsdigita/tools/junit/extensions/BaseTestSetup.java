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
import com.arsdigita.init.*;
import com.arsdigita.persistence.*;
import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.pdl.*;

import java.sql.*;
import java.io.*;

import com.arsdigita.db.ConnectionManager;
import com.arsdigita.installer.LoadSQLPlusScript;
import com.arsdigita.util.DummyServletContext;
import com.arsdigita.util.ResourceManager;
import com.arsdigita.util.jdbc.*;
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
        final Protectable p = new Protectable() {
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

    /**
     * Sets up the fixture. Override to set up additional fixture
     * state.
     */
    protected void setUp() throws Exception {
        if (m_suite.testCount() > 0) {
            final Connection conn = Connections.acquire
                (Init.getConfig().getJDBCURL());
            new Startup(conn).run();

            ResourceManager.getInstance().setServletContext(new DummyServletContext());

            setupSQL();
        }
    }

    /**
     * Tears down the fixture. Override to tear down the additional
     * fixture state.
     */
    protected void tearDown() throws Exception {
        if (m_suite.testCount() > 0) {
            teardownSQL ();
        }
    }

    protected void setupSQL() throws Exception {
        if (m_setupSQLScripts.size() > 0) {
            runScripts(m_setupSQLScripts);
        }
    }


    protected void teardownSQL() throws Exception {
        if (m_teardownSQLScripts.size() > 0) {
            runScripts(m_teardownSQLScripts);
        }
    }

    private void runScripts(final List scripts) throws Exception {
        LoadSQLPlusScript loader = new LoadSQLPlusScript();
        Connection conn = Connections.acquire(Init.getConfig().getJDBCURL());

        loader.setConnection(conn);

        for (Iterator iterator = scripts.iterator(); iterator.hasNext(); ) {
            final String script = (String) iterator.next();

            loader.loadSQLPlusScript(script);
        }

        conn.commit();
        conn.close();
    }

    // Unwanted things

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
        // Nooped
    }

    public boolean getPerformInitialization() {
        return true;
    }

    public void setSetupSQLScript(String setupSQLScript) {
        m_setupSQLScripts.add(setupSQLScript);
    }

    public void setTeardownSQLScript(String teardownSQLScript) {
        m_teardownSQLScripts.add(teardownSQLScript);
    }
}
