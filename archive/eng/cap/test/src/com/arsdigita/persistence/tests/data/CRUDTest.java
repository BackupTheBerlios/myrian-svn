/*
 * Copyright (C) 2002-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.arsdigita.persistence.tests.data;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.tools.junit.framework.BaseTestCase;

import java.util.*;

/**
 * CRUDTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class CRUDTest extends BaseTestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/tests/data/CRUDTest.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public CRUDTest(String name) {
        super(name);
    }

    public void testExtendLob() {
        CRUDTestlet test =
            new CRUDTestlet("com.arsdigita.persistence.ExtendLob");
        test.run();
    }

    public void testGroup() {
        CRUDTestlet test = new CRUDTestlet("com.arsdigita.kernel.Group");
        test.run();
    }

    public void testIcle() {
        CRUDTestlet test = new CRUDTestlet("test.Icle");
        test.run();
    }

    public void testTest() {
        CRUDTestlet test = new CRUDTestlet("test.Test");
        test.run();
    }

    public void test() {
        DataSource ds1 = new DataSource("test1.key");
        DataSource ds2 = new DataSource("test2.key");

        MetadataRoot root = MetadataRoot.getMetadataRoot();
        ObjectType type =
            root.getObjectType("com.arsdigita.kernel.ACSObject");
        ObjectTree tree = new ObjectTree(type);
        for (Iterator it = type.getProperties(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            tree.addPath(prop.getName());
        }

        System.out.println(tree);
    }

}
