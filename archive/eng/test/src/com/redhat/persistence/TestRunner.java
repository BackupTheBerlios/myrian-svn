package com.redhat.persistence;

import junit.framework.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * TestRunner
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public class TestRunner {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/TestRunner.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    public static final void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            run(args[i]);
        }
    }

    private static final void run(String suite) throws Exception {
        TestResult result = new TestResult();
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
            System.out.println(failure.toString());
            System.out.println(failure.trace());
        }
    }

}
