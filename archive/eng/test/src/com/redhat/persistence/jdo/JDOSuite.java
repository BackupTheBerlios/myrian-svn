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
        suite.addTestSuite(MemoryTest.class);

        suite.addTest(QueryTest.suite());

        JDOTestSetup setup = new JDOTestSetup(suite);
        setup.load(Employee.class);
        setup.load(Department.class);
        setup.load(User.class);
        setup.load(Group.class);
        setup.load(Rolodex.class);
        setup.load(Order.class);
        setup.load(Item.class);
        setup.load(Product.class);
        setup.load(Picture.class);
        setup.load(Magazine.class);
        return setup;
    }
}
