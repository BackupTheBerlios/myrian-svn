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

package com.arsdigita.persistence.tests.data;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.tools.junit.framework.BaseTestCase;

import java.util.*;

/**
 * CRUDTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public class CRUDTest extends BaseTestCase {

    public final static String versionId = "$Id: //core-platform/test-qgen/test/src/com/arsdigita/persistence/tests/data/CRUDTest.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

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
