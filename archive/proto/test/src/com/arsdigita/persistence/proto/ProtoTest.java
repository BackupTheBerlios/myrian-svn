package com.arsdigita.persistence.proto;

import com.arsdigita.tools.junit.framework.BaseTestCase;

import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.persistence.proto.pdl.PDL;
import java.util.*;
import java.math.*;
import java.io.*;

/**
 * ProtoTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2003/01/02 $
 **/

public class ProtoTest extends BaseTestCase {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/proto/ProtoTest.java#4 $ by $Author: rhs $, $DateTime: 2003/01/02 15:38:03 $";

    public ProtoTest(String name) {
        super(name);
    }

    public void test() throws Exception {
        PDL.main(new String[] {
            "pdl/com/arsdigita/persistence/global.pdl", "test/pdl/Test.pdl"
        });

        OID oid = new OID("test.Test", BigInteger.ZERO);
        Property NAME = oid.getObjectType().getProperty("name");
        Property OPT2MANY = oid.getObjectType().getProperty("opt2many");

        Session ssn = new Session();
        PersistentObject po = ssn.create(oid);
        PersistentObject po2 = ssn.retrieve(oid);
        assertTrue(po == po2);

        ssn.set(oid, NAME, "foo");
        assertEquals("foo", ssn.get(oid, NAME));

        ssn.delete(oid);
        assertEquals(null, ssn.retrieve(oid));

        ssn.create(oid);

        PersistentCollection pc =
            (PersistentCollection) ssn.get(oid, OPT2MANY);

        Object one = ssn.create
            (new OID("test.Icle", new BigInteger("1")));
        Object two = ssn.create
            (new OID("test.Icle", new BigInteger("2")));
        Object three = ssn.create
            (new OID("test.Icle", new BigInteger("3")));

        ssn.add(oid, OPT2MANY, one);
        assertCollection(new Object[] {one}, pc);
        ssn.add(oid, OPT2MANY, two);
        assertCollection(new Object[] {one, two}, pc);
        ssn.add(oid, OPT2MANY, three);
        assertCollection(new Object[] {one, two, three}, pc);
        ssn.remove(oid, OPT2MANY, two);
        assertCollection(new Object[] {one, three}, pc);
        ssn.remove(oid, OPT2MANY, three);
        assertCollection(new Object[] {one}, pc);
        ssn.add(oid, OPT2MANY, three);
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
