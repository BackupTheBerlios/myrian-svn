/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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
 * @version $Revision: #2 $ $Date: 2003/01/07 $
 **/

public class MappingsTest extends BaseTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/tests/data/MappingsTest.java#2 $ by $Author: dennis $, $DateTime: 2003/01/07 14:51:38 $";

    public MappingsTest(String name) {
        super(name);
    }

    private static final void doTest(String type) {
        String qualified = "com.arsdigita.persistence.tests.data.mappings." +
            type;
        CRUDTestlet testlet = new CRUDTestlet(qualified);
        testlet.run();
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

}
