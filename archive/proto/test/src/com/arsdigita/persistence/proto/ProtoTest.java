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
 * @version $Revision: #1 $ $Date: 2002/12/02 $
 **/

public class ProtoTest extends BaseTestCase {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/proto/ProtoTest.java#1 $ by $Author: rhs $, $DateTime: 2002/12/02 12:04:21 $";

    public ProtoTest(String name) {
        super(name);
    }

    public void test() {
        OID oid = new OID("test.Test", BigInteger.ZERO);
        Property NAME = oid.getObjectType().getProperty("name");
        Property COLLECTION = oid.getObjectType().getProperty("collection");

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

        ssn.add(oid, COLLECTION, "One");
        assertCollection(new Object[] {"One"}, pc);
        ssn.add(oid, COLLECTION, "Two");
        assertCollection(new Object[] {"One", "Two"}, pc);
        ssn.add(oid, COLLECTION, "Three");
        assertCollection(new Object[] {"One", "Two", "Three"}, pc);
        ssn.remove(oid, COLLECTION, "Two");
        assertCollection(new Object[] {"One", "Three"}, pc);
        ssn.remove(oid, COLLECTION, "Three");
        assertCollection(new Object[] {"One"}, pc);
        ssn.add(oid, COLLECTION, "Three");
        assertCollection(new Object[] {"One", "Three"}, pc);

        PrintWriter pw = new PrintWriter(System.out);
        pw.println("Foo:");
        ssn.dump(pw);

        ssn.flush();

        pw.println("Bar:");
        ssn.dump(pw);

        assertTrue(ssn.retrieve(oid) != po);

        pw.println("Baz:");
        ssn.dump(pw);

        pw.flush();
        pw.close();
    }

    private static void assertCollection(Object[] expected,
                                         PersistentCollection actual) {
        /*DataSet ds = actual.getDataSet();
        Cursor c = ds.getCursor();
        int index = 0;
        while (c.next()) {
            assertEquals(expected[index++], c.get());
        }

        assertEquals(expected.length, index);*/
    }

}
