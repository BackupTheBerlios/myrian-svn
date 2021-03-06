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

package com.arsdigita.persistence;

import com.arsdigita.db.SQLExceptionHandler;
import com.arsdigita.logging.SecureLogFilter;
import com.arsdigita.tools.junit.framework.BaseTestCase;

import java.io.*;
import java.io.StringWriter;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.extensions.*;
import junit.framework.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.varia.DenyAllFilter;
import org.apache.log4j.varia.StringMatchFilter;

/**
 * A base JUnit test class useful for writing junit test cases
 * that rely on Log4j.
 * Automatically adds/removes a new Log object that
 * will log to a member StringWriter variable,
 * turns on all debugging on setup, and restores
 * debugging to original values on teardown.
 *
 * Example code:
 * <pre>
 * StringMatchFilter filterHold = new StringMatchFilter();
 * String holdString = "connectionUserCountHitZero holding on to connection";
 * filterHold.setStringToMatch(holdString);
 * filterHold.setAcceptOnMatch(true);
 * log.addFilter(new DenyAllFilter());
 * ...
 * assertLogContains(holdString);
 * ...
 * clearLog();
 * ...
 * assertLogDoesNotContain(holdString);
 * ...
 * </pre>
 *
 * @author David Eison
 */
public class Log4jBasedTestCase extends PersistenceTestCase {

    public static final String versionId = "$Id: //core-platform/test-packaging/test/src/com/arsdigita/persistence/Log4jBasedTestCase.java#2 $";

    /**
     * The log object.  Should be modified
     * by adding filters, and perhaps changing default priority.
     */
    protected WriterAppender log = null;

    /**
     * The StringWriter that log will log to
     */
    protected StringWriter logSW = null;

    HashMap originalPriorities = new HashMap();

    public Log4jBasedTestCase (String name) {
        super(name);
    }

    public void runBare() throws Throwable {
        try {
            logSetUp();
            super.runBare();
        } finally {
            logTearDown();
        }
    }

    /**
     * Turns on all logging so that the desired log messages will be found.
     * Adds a new logger, by default logging everything except for
     * secure messages to the member stringwriter variable.
     */
    public void logSetUp() throws Exception {
        // nuke all priorities back to DEBUG
        Category root = Category.getRoot();
        Enumeration enum = root.getCurrentCategories();

        while (enum.hasMoreElements()) {
            Category cat = (Category)enum.nextElement();
            originalPriorities.put(cat, cat.getPriority());
            cat.setPriority(Priority.DEBUG);
        }

        root.info("All categories were temporarily set to DEBUG for a test");

        logSW = new StringWriter();
        log = new WriterAppender(new TTCCLayout(), logSW);
        log.addFilter(new SecureLogFilter());
        root.addAppender(log);
    }

    /**
     * Restores all logging & aggressive connection close setting to original state.
     */
    public void logTearDown() throws Exception {
        Category root = Category.getRoot();
        root.info("All categories will be restored to their original values");

        // restore all priorities
        Iterator it = originalPriorities.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry current = (java.util.Map.Entry)it.next();
            Category cat = (Category)current.getKey();
            Priority pri = (Priority)current.getValue();
            cat.setPriority(pri);
        }

        root.removeAppender(log);
        log.close();
    }

    /**
     * String of messages that have been logged since creation or
     * last clearLog.
     */
    public String getLogMessage() {
        return logSW.toString();
    }

    /**
     * Asserts that log contains given message
     * (case sensitive, exact match).
     */
    public void assertLogContains(String msg) {
        assertTrue("Log messages should contain " + msg +
                   " but did not.  Log was:" + Utilities.LINE_BREAK +
                   "'" + getLogMessage() + "'",
                   getLogMessage().indexOf(msg) >= 0);
    }

    /**
     * Asserts that log does not contain given message
     * (case sensitive, exact match).
     */
    public void assertLogDoesNotContain(String msg) {
        assertTrue("Log messages should not contain " + msg +
                   " but did.  Log was:" + Utilities.LINE_BREAK +
                   "'" + getLogMessage() + "'",
                   getLogMessage().indexOf(msg) < 0);
    }

    /**
     * Clears the log's messages.
     * Existing filters and priorities remain intact.
     */
    public void clearLog() {
        logSW.getBuffer().setLength(0);
        assertTrue("Log should be empty after reset, instead contained:" +
                   Utilities.LINE_BREAK + "'" + getLogMessage() + "'",
                   getLogMessage().length() == 0);
    }
}
