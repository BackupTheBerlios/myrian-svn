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

import com.arsdigita.tools.junit.framework.BaseTestCase;


/**
 * The MappingsTest class contains JUnit test cases for the various ways that
 * persistences supports mapping logical object hierarchy to a physical data
 * model.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class MappingsTest extends BaseTestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/tests/data/MappingsTest.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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

    public void testNakedJoin() {
        doTest("NakedJoin");
    }
}
