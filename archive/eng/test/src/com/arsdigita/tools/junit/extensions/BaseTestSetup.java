/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.arsdigita.tools.junit.extensions;

import com.arsdigita.util.*;
import com.arsdigita.db.*;

import java.sql.*;
import java.io.*;

import com.arsdigita.installer.SQLLoader;
import com.arsdigita.runtime.*;
import com.arsdigita.util.jdbc.*;
import com.redhat.persistence.pdl.*;
import junit.extensions.TestDecorator;
import junit.framework.Protectable;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * A Decorator to set up and tear down additional fixture state.
 * Subclass BaseTestSetup and insert it into your tests when you want
 * to set up additional state once before the tests are run.
 */
public class BaseTestSetup extends TestDecorator {

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

    private class TestLoader extends SQLLoader {
        TestLoader(Connection conn) { super(conn); }
        protected Reader open(String name) {
            String db = DbHelper.getDatabaseSuffix
                (DbHelper.getDatabase(getConnection()));
            ClassLoader ldr = Thread.currentThread().getContextClassLoader();
            InputStream is = null;
            if (name.endsWith(".sql")) {
                String first = name.substring(0, name.length() - 4);
                String dbname = first + "." + db + ".sql";
                is = ldr.getResourceAsStream(dbname);
            }
            if (is == null) {
                is = ldr.getResourceAsStream(name);
            }
            if (is == null) {
                return null;
            } else {
                return new InputStreamReader(is);
            }
        }
    }

    private void runScripts(final List scripts) throws Exception {
        Connection conn = Connections.acquire
	    (RuntimeConfig.getConfig().getJDBCURL());

        TestLoader ldr = new TestLoader(conn);

        for (Iterator iterator = scripts.iterator(); iterator.hasNext(); ) {
            final String script = (String) iterator.next();
            ldr.load(script);
        }

        conn.commit();
        conn.close();
    }

    public void setSetupSQLScript(String setupSQLScript) {
        m_setupSQLScripts.add(setupSQLScript);
    }

    public void setTeardownSQLScript(String teardownSQLScript) {
        m_teardownSQLScripts.add(teardownSQLScript);
    }
}
