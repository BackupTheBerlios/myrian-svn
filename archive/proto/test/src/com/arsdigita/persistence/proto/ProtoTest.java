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
 * @version $Revision: #6 $ $Date: 2003/01/06 $
 **/

public class ProtoTest extends TestCase {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/proto/ProtoTest.java#6 $ by $Author: rhs $, $DateTime: 2003/01/06 17:58:56 $";

    public ProtoTest(String name) {
        super(name);
    }

    public void test() throws Exception {
        PDL.main(new String[] {
            "pdl/com/arsdigita/persistence/global.pdl", "test/pdl/Test.pdl"
        });

        OID oid = new OID("test.Test", BigInteger.ZERO);
        Property NAME = oid.getObjectType().getProperty("name");
        Property COLLECTION = oid.getObjectType().getProperty("collection");
        Property OPT2MANY = oid.getObjectType().getProperty("opt2many");
        doTest(oid, NAME, COLLECTION);
        doTest(oid, NAME, OPT2MANY);
    }

    private void doTest(OID oid, Property str, Property col) {
        Session ssn = new Session();
        PersistentObject po = ssn.create(oid);
        PersistentObject po2 = ssn.retrieve(oid);
        assertTrue(po == po2);

        ssn.set(oid, str, "foo");
        assertEquals("foo", ssn.get(oid, str));

        ssn.delete(oid);
        assertEquals(null, ssn.retrieve(oid));

        ssn.create(oid);

        PersistentCollection pc =
            (PersistentCollection) ssn.get(oid, col);

        Object one = ssn.create
            (new OID("test.Icle", new BigInteger("1")));
        Object two = ssn.create
            (new OID("test.Icle", new BigInteger("2")));
        Object three = ssn.create
            (new OID("test.Icle", new BigInteger("3")));

        ssn.add(oid, col, one);
        assertCollection(new Object[] {one}, pc);
        ssn.add(oid, col, two);
        assertCollection(new Object[] {one, two}, pc);
        ssn.add(oid, col, three);
        assertCollection(new Object[] {one, two, three}, pc);
        ssn.remove(oid, col, two);
        assertCollection(new Object[] {one, three}, pc);
        ssn.remove(oid, col, three);
        assertCollection(new Object[] {one}, pc);
        ssn.add(oid, col, three);
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

        ssn.delete(oid);
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
