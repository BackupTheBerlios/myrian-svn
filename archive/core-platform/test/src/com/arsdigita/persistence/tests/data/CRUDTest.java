package com.arsdigita.persistence.tests.data;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.tools.junit.framework.BaseTestCase;

import java.util.*;

/**
 * CRUDTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/10/31 $
 **/

public class CRUDTest extends BaseTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/tests/data/CRUDTest.java#1 $ by $Author: rhs $, $DateTime: 2002/10/31 12:25:15 $";

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
