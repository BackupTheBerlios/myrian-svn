package com.redhat.persistence.jdo;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JDOSuite extends TestSuite {
    public static Test suite() {
        JDOSuite suite = new JDOSuite();

        suite.addTestSuite(SimpleTest.class);
        suite.addTestSuite(PandoraTest.class);
        suite.addTestSuite(WithoutTxnTest.class);
        suite.addTestSuite(MapTest.class);
        suite.addTestSuite(ListTest.class);
        suite.addTestSuite(ManagerTest.class);
        suite.addTestSuite(BiTxnTest.class);
        suite.addTestSuite(NestedObjectTest.class);
        suite.addTestSuite(JDOStateTest.class);

        suite.addTest(QueryTest.suite());

        return new JDOTestSetup(suite);
    }
}
