package com.arsdigita.persistence.tests.data;

import com.arsdigita.tools.junit.framework.BaseTestCase;


/**
 * The MappingsTest class contains JUnit test cases for the various ways that
 * persistences supports mapping logical object hierarchy to a physical data
 * model.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public class MappingsTest extends BaseTestCase {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/tests/data/MappingsTest.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

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
