package com.redhat.persistence.jdo;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JDOSuite extends TestSuite {
    public static Test suite() {
        JDOSuite suite = new JDOSuite();

        suite.addTestSuite(SimpleTest.class);
        suite.addTestSuite(PandoraTest.class);
        suite.addTestSuite(WithoutTxnTest.class);

        // suite.addTest(new PandoraTest("testMain"));

        return new JDOTestSetup(suite);
    }
}
