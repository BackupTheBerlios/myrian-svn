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

package com.arsdigita.tools.junit.extensions;

import java.sql.Connection;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.FileNotFoundException;

import com.arsdigita.db.ConnectionManager;
import com.arsdigita.db.DbHelper;
import com.arsdigita.initializer.Script;
import com.arsdigita.installer.LoadSQLPlusScript;
import com.arsdigita.installer.ParseException;
import junit.extensions.*;
import junit.framework.*;

/**
 * A Decorator to set up and tear down additional fixture state.
 * Subclass BaseTestSetup and insert it into your tests when you want
 * to set up additional state once before the tests are run.
 */
public class BaseTestSetup extends TestDecorator {

    protected boolean performInitialization = true;
    protected String scriptName;
    protected String iniName;
    protected TestSuite suite;

    private List m_setupSQLScripts = new LinkedList();
    private List m_teardownSQLScripts = new LinkedList();

    public BaseTestSetup(Test test, TestSuite suite) {
        super(test);
        this.suite = suite;
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
        this.scriptName = scriptName;
    }

    public String getInitScript() {
        return scriptName;
    }

    public void setInitScriptTarget(String iniName) {
        this.iniName = iniName;
    }

    public String getInitScriptTarget() {
        return iniName;
    }

    public void setPerformInitialization(boolean performInitialization) {
        this.performInitialization = performInitialization;
    }

    public boolean getPerformInitialization() {
        return performInitialization;
    }

    /**
     * Sets up the fixture. Override to set up additional fixture
     * state.
     */

    protected void setUp() throws Exception {
        if ( suite.testCount() > 0 ) {
            if (performInitialization) {
                Initializer.startup(suite, scriptName, iniName);
                setupSQL ();
            }
        }
    }

    /**
     * Tears down the fixture. Override to tear down the additional
     * fixture state.
     */
    protected void tearDown() throws Exception {
        if ( suite.testCount() > 0 ) {
            teardownSQL ();
            if (performInitialization) {
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
        for (Iterator iterator = scripts.iterator(); iterator.hasNext();) {
            String script = (String) iterator.next();
            LoadSQLPlusScript loader = new LoadSQLPlusScript();
            loader.setConnection ( ConnectionManager.getConnection() );
            loader.setDatabase(DbHelper.getDatabaseDirectory());
            loader.loadSQLPlusScript ( script );

        }
    }

}
