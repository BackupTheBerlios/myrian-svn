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

import com.arsdigita.tools.junit.framework.BaseTestCase;


/**
 * The MappingsTest class contains JUnit test cases for the various ways that
 * persistences supports mapping logical object hierarchy to a physical data
 * model.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 **/

public class MappingsTest extends BaseTestCase {

    public final static String versionId = "$Id: //core-platform/test-packaging/test/src/com/arsdigita/persistence/tests/data/MappingsTest.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

    public MappingsTest(String name) {
        super(name);
    }

    private static final void doTest(String type) {
        String qualified = "com.arsdigita.persistence.tests.data.mappings." +
            type;
        CRUDTestlet testlet = new CRUDTestlet(qualified);
        testlet.run();
    }

    private static final void doIsolationTest(String type, String[] path) {
        String qualified = "com.arsdigita.persistence.tests.data.mappings." +
            type;
        new PropertyIsolationTestlet(qualified, path).run();
    }

    private static final void doDoubleTest(String type, String[] path) {
        String qualified = "com.arsdigita.persistence.tests.data.mappings." +
            type;
        new DoubleUpdateTestlet(qualified, path).run();
    }

    public void testBase() {
        doTest("Base");
    }

    public void testTarget() {
        doTest("Target");
    }

    public void testParasiteOne() {
        doTest("ParasiteOne");
    }

    public void testParasiteTwo() {
        doTest("ParasiteTwo");
    }

    public void testSymbioteOne() {
        doTest("SymbioteOne");
    }

    public void testSymbioteTwo() {
        doTest("SymbioteOne");
    }

    public void testNormalized() {
        doTest("Normalized");
    }

    public void testReferenceIsolation() {
        doIsolationTest
            ("ReferenceTo", new String[] { "target", "id" });
    }

    public void testReferenceDouble() {
        doDoubleTest
            ("ReferenceTo", new String[] { "target", "id" });
    }

    public void testReferenceFromIsolation() {
        doIsolationTest
            ("ReferenceFrom", new String[] { "target", "id" });
    }

    public void testReferenceFromDouble() {
        doDoubleTest
            ("ReferenceFrom", new String[] { "target", "id" });
    }

    public void testReferenceMappingTableIsolation() {
        doIsolationTest
            ("ReferenceMappingTable", new String[] { "target", "id" });
    }

    public void testReferenceMappingTableDouble() {
        doDoubleTest
            ("ReferenceMappingTable", new String[] { "target", "id" });
    }

    public void testRequiredTwoWayReference() {
        new DoubleUpdateTestlet
            ("test.Component", new String[] { "test", "id" }).run();
    }

}
