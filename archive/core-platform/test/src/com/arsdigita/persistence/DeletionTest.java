package com.arsdigita.persistence;

import java.math.*;

/**
 * DeletionTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/10/02 $
 **/

public class DeletionTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/DeletionTest.java#1 $ by $Author: rhs $, $DateTime: 2002/10/02 14:58:38 $";

    public DeletionTest(String name) {
        super(name);
    }

    public void testRemoveOpt2manyBack() {
        Session ssn = SessionManager.getSession();
        OID TEST = new OID("test.Test", BigInteger.ZERO);
        OID ICLE = new OID("test.Icle", BigInteger.ZERO);

        DataObject icle = ssn.create(ICLE);
        icle.save();

        DataObject test = ssn.create(TEST);
        test.set("required", icle);
        test.save();

        icle.set("opt2manyBack", test);
        icle.save();

        icle.set("opt2manyBack", null);
        icle.save();

        icle = ssn.retrieve(ICLE);
        assertEquals(null, icle.get("opt2manyBack"));
    }

}
