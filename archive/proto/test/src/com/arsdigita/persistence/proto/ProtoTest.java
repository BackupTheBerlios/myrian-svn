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
 * @version $Revision: #10 $ $Date: 2003/02/12 $
 **/

public class ProtoTest extends TestCase {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/proto/ProtoTest.java#10 $ by $Author: ashah $, $DateTime: 2003/02/12 16:39:50 $";


    private static class Generic {

        private ObjectType m_type;
        private BigInteger m_id;

        public Generic(ObjectType type, BigInteger id) {
            m_id = id;
        }

        public ObjectType getType() {
            return m_type;
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

        ObjectType TEST = Root.getRoot().getObjectType("test.Icle");

        Adapter.addAdapter(Generic.class, TEST, new Adapter() {
                public Object getKey(Object obj) {
                    return ((Generic) obj).getID();
                }

                public ObjectType getObjectType(Object obj) {
                    return ((Generic) obj).getType();
                }
            });

        Generic test = new Generic(TEST, BigInteger.ZERO);
        Property NAME = TEST.getProperty("name");
        Property COLLECTION = TEST.getProperty("collection");
        Property OPT2MANY = TEST.getProperty("opt2many");
        doTest(test, NAME, COLLECTION);
        doTest(test, NAME, OPT2MANY);
    }

    private void doTest(Generic obj, Property str, Property col) {
        Session ssn = new Session();
        ssn.create(obj);
        Object obj2 = ssn.retrieve(obj.getType(), obj.getID());
        assertTrue(obj == obj2);

        ssn.set(obj, str, "foo");
        assertEquals("foo", ssn.get(obj, str));

        ssn.delete(obj);
        assertEquals(null, ssn.retrieve(obj.getType(), obj.getID()));

        ssn.create(obj);

        PersistentCollection pc =
            (PersistentCollection) ssn.get(obj, col);

        ObjectType ICLE = Root.getRoot().getObjectType("test.Icle");
        Object one = new Generic(ICLE, new BigInteger("1"));
        ssn.create(one);
        Object two = new Generic(ICLE, new BigInteger("2"));
        ssn.create(two);
        Object three = new Generic(ICLE, new BigInteger("3"));
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
