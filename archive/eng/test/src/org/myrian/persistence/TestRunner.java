/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence;

import junit.framework.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;
import org.apache.log4j.Logger;

/**
 * TestRunner
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/10/04 $
 **/

public class TestRunner {


    private static final Logger s_log = Logger.getLogger(TestRunner.class);

    private static boolean verbose() {
        return Boolean.getBoolean("junit.verbose");
    }

    public static final void main(String[] args) throws Exception {
        List results = new ArrayList();
        for (int i = 0; i < args.length; i++) {
            results.add(run(args[i]));
        }

        System.out.println("");

        for (int i = 0; i < results.size(); i++) {
            TestResult result = (TestResult) results.get(i);
            if (result.runCount() == 0) {
                if (verbose()) {
                    System.out.println("Test " + args[i] + ": SKIPPED");
                }
                continue;
            }
            System.out.println
                ("Test " + args[i] + ": " +
                 (result.wasSuccessful() ? "PASSED" : "FAILED"));
            System.out.println
                ("Tests run: " + result.runCount()
                 + ", failures: " + result.failureCount()
                 + ", errors: " + result.errorCount());
        }
    }

    private static final TestResult run(String suite) throws Exception {
        final boolean halt = "true".equals
            (System.getProperty("junit.haltonfailure"));
        String include = System.getProperty("junit.test");
        if (include == null) {
            include = System.getProperty("junit.include");
        }
        String exclude = System.getProperty("junit.exclude");
        final Pattern in = include == null ?
            null : Pattern.compile(include, Pattern.DOTALL);
        final Pattern ex = exclude == null ?
            null : Pattern.compile(exclude, Pattern.DOTALL);

        final TestResult result = new TestResult() {
            protected void run(TestCase test) {
                String name = "" + test;
                if (ex != null && ex.matcher(name).find()) { return; }
                if (in == null || in.matcher(name).find()) {
                    super.run(test);
                }
            }
        };
        result.addListener(new TestListener() {
            public void addError(Test test, Throwable t) {
                s_log.warn("error " + test);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("stack", t);
                }

                print(test, t, false);
                if (halt) { result.stop(); }
            }
            public void addFailure(Test test, AssertionFailedError t) {
                s_log.warn("failure " + test);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("stack", t);
                }

                print(test, t, true);
                if (halt) { result.stop(); }
            }
            public void startTest(Test test) {
                s_log.info("starting " + test);
            }
            public void endTest(Test test) {
                s_log.info("stopping " + test);
            }
        });

        Class klass = Class.forName(suite);
        Method method = klass.getMethod("suite", new Class[0]);
        Test test = (Test) method.invoke(null, null);
        test.run(result);

        return result;
    }

    private static final void print(Test test, Throwable t, boolean failed) {
        System.out.println
            ("Testcase " + (failed ? "FAILED" : " ERROR") + ": " + test);
        t.printStackTrace(System.out);
    }

}
