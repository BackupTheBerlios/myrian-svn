package com.arsdigita.persistence.proto;

import com.arsdigita.tools.junit.framework.BaseTestCase;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.OID;
import java.math.*;
import java.io.*;

/**
 * ProtoTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/04 $
 **/

public class ProtoTest extends BaseTestCase {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/proto/ProtoTest.java#2 $ by $Author: rhs $, $DateTime: 2002/12/04 19:18:22 $";

    public ProtoTest(String name) {
        super(name);
    }

    public void test() {
        OID oid = new OID("test.Test", BigInteger.ZERO);
        Property NAME = oid.getObjectType().getProperty("name");
        Property COLLECTION = oid.getObjectType().getProperty("opt2many");

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
            (PersistentCollection) ssn.get(oid, COLLECTION);

        Object one = ssn.create
            (new OID("test.Icle", new BigInteger("1")));
        Object two = ssn.create
            (new OID("test.Icle", new BigInteger("2")));
        Object three = ssn.create
            (new OID("test.Icle", new BigInteger("3")));

        ssn.add(oid, COLLECTION, one);
        assertCollection(new Object[] {one}, pc);
        ssn.add(oid, COLLECTION, two);
        assertCollection(new Object[] {one, two}, pc);
        ssn.add(oid, COLLECTION, three);
        assertCollection(new Object[] {one, two, three}, pc);
        ssn.remove(oid, COLLECTION, two);
        assertCollection(new Object[] {one, three}, pc);
        ssn.remove(oid, COLLECTION, three);
        assertCollection(new Object[] {one}, pc);
        ssn.add(oid, COLLECTION, three);
        assertCollection(new Object[] {one, three}, pc);

        System.out.println("Foo:");
        ssn.dump();

        ssn.flush();

        System.out.println("Bar:");
        ssn.dump();

        assertTrue(ssn.retrieve(oid) != po);

        System.out.println("Baz:");
        ssn.dump();
    }

    private static void assertCollection(Object[] expected,
                                         PersistentCollection actual) {
        DataSet ds = actual.getDataSet();
        Cursor c = ds.getCursor();
        int index = 0;
        while (c.next()) {
            assertEquals(expected[index++], c.get());
        }

        assertEquals(expected.length, index);
    }

}
