package com.arsdigita.persistence.proto;

import junit.framework.TestCase;
//import com.arsdigita.tools.junit.framework.BaseTestCase;

import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.persistence.proto.pdl.PDL;
import java.util.*;
import java.math.*;
import java.io.*;

/**
 * ProtoTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2003/02/12 $
 **/

public class ProtoTest extends TestCase {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/proto/ProtoTest.java#8 $ by $Author: rhs $, $DateTime: 2003/02/12 14:21:42 $";


    private static class Test {

        private BigInteger m_id;

        public Test(BigInteger id) {
            m_id = id;
        }

        public BigInteger getID() {
            return m_id;
        }

    }

    private static class Icle {

        private BigInteger m_id;

        public Icle(BigInteger id) {
            m_id = id;
        }

        public BigInteger getID() {
            return m_id;
        }

    }


    public ProtoTest(String name) {
        super(name);
    }

    public void test() throws Exception {
        PDL.main(new String[] {"test/pdl/Test.pdl"});

        Adapter.addAdapter(Test.class, new Adapter() {
                public Object getKey(Object obj) {
                    return "test.Test:" + ((Test) obj).getID();
                }

                public ObjectType getObjectType(Object obj) {
                    return Root.getRoot().getObjectType("test.Test");
                }
            });
        Adapter.addAdapter(Icle.class, new Adapter() {
                public Object getKey(Object obj) {
                    return "test.Icle:" + ((Icle) obj).getID();
                }

                public ObjectType getObjectType(Object obj) {
                    return Root.getRoot().getObjectType("test.Icle");
                }
            });

        Test test = new Test(BigInteger.ZERO);
        ObjectType type = Root.getRoot().getObjectType("test.Icle");
        Property NAME = type.getProperty("name");
        Property COLLECTION = type.getProperty("collection");
        Property OPT2MANY = type.getProperty("opt2many");
        doTest(test, test.getID(), NAME, COLLECTION);
        doTest(test, test.getID(), NAME, OPT2MANY);
    }

    private void doTest(Object obj, Object id, Property str, Property col) {
        Session ssn = new Session();
        ssn.create(obj);
        ObjectType type = ssn.getObjectType(obj);
        Object obj2 = ssn.retrieve(type, id);
        assertTrue(obj == obj2);

        ssn.set(obj, str, "foo");
        assertEquals("foo", ssn.get(obj, str));

        ssn.delete(obj);
        assertEquals(null, ssn.retrieve(type, id));

        ssn.create(obj);

        PersistentCollection pc =
            (PersistentCollection) ssn.get(obj, col);

        Object one = new Icle(new BigInteger("1"));
        ssn.create(one);
        Object two = new Icle(new BigInteger("2"));
        ssn.create(two);
        Object three = new Icle(new BigInteger("3"));
        ssn.create(three);

        ssn.add(obj, col, one);
        assertCollection(new Object[] {one}, pc);
        ssn.add(obj, col, two);
        assertCollection(new Object[] {one, two}, pc);
        ssn.add(obj, col, three);
        assertCollection(new Object[] {one, two, three}, pc);
        ssn.remove(obj, col, two);
        assertCollection(new Object[] {one, three}, pc);
        ssn.remove(obj, col, three);
        assertCollection(new Object[] {one}, pc);
        ssn.add(obj, col, three);
        assertCollection(new Object[] {one, three}, pc);

        System.out.println();
        System.out.println("---------------------------------");
        System.out.println();
        ssn.dump();

        ssn.flush();

        System.out.println();
        System.out.println("---------------------------------");
        System.out.println();
        ssn.dump();

        ssn.delete(obj);
        ssn.flush();
        System.out.println();
        System.out.println("---------------------------------");
        System.out.println();
        ssn.dump();
    }

    private static void assertCollection(Object[] expected,
                                         PersistentCollection actual) {
        HashSet exp = new HashSet();
        for (int i = 0; i < expected.length; i++) {
            exp.add(expected[i]);
        }

        HashSet act = new HashSet();
        DataSet ds = actual.getDataSet();
        Cursor c = ds.getCursor();
        while (c.next()) {
            act.add(c.get());
        }

        assertEquals(exp, act);
    }

}
