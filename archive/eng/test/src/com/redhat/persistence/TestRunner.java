package com.redhat.persistence;

import junit.framework.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * TestRunner
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/03 $
 **/

public class TestRunner {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/TestRunner.java#5 $ by $Author: rhs $, $DateTime: 2004/08/03 14:02:15 $";

    private static final Logger s_log = Logger.getLogger(TestRunner.class);

    public static final void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            run(args[i]);
        }
    }

    private static final void run(String suite) throws Exception {
        final boolean halt = "true".equals
            (System.getProperty("junit.haltonfailure"));
        final String filter = System.getProperty("junit.test");
        final String exclude = System.getProperty("junit.exclude");

        final TestResult result = new TestResult() {
            protected void run(TestCase test) {
                String name = "" + test;
                if (exclude != null && name.matches(exclude)) { return; }
                if (filter == null || name.matches(filter)) {
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
                if (halt) { result.stop(); }
            }
            public void addFailure(Test test, AssertionFailedError t) {
                s_log.warn("failure " + test);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("stack", t);
                }
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
        System.out.println
            ("Test " + suite + ": " +
             (result.wasSuccessful() ? "PASSED" : "FAILED"));
        System.out.println
            ("Tests run: " + result.runCount()
             + ", failures: " + result.failureCount()
             + ", errors: " + result.errorCount());
        print(result.failures());
        print(result.errors());
    }

    private static final void print(Enumeration e) {
        while (e.hasMoreElements()) {
            TestFailure failure = (TestFailure) e.nextElement();
            System.out.println("Testcase: " + failure.failedTest());
            System.out.println(failure.isFailure() ? "FAILED " : "ERROR ");
            if (!"false".equals(System.getProperty("junit.verbose"))) {
                System.out.println(failure.toString());
                System.out.println(failure.trace());
            }
        }
    }

}
