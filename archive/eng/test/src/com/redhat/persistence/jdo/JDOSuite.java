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
