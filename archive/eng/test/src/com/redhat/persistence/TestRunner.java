package com.redhat.persistence;

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
 * @version $Revision: #7 $ $Date: 2004/08/12 $
 **/

public class TestRunner {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/TestRunner.java#7 $ by $Author: ashah $, $DateTime: 2004/08/12 15:46:57 $";

    private static final Logger s_log = Logger.getLogger(TestRunner.class);

    public static final void main(String[] args) throws Exception {
        List results = new ArrayList();
        for (int i = 0; i < args.length; i++) {
            results.add(run(args[i]));
        }

        System.out.println("");

        for (int i = 0; i < results.size(); i++) {
            TestResult result = (TestResult) results.get(i);
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
                if (ex != null && ex.matcher(name).matches()) { return; }
                if (in == null || in.matcher(name).matches()) {
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
            ("Testcase " + (failed ? "FAILED" : "ERROR ") + ": " + test);

        if (!"false".equals(System.getProperty("junit.verbose"))) {
            t.printStackTrace(System.out);
        }
    }

}
