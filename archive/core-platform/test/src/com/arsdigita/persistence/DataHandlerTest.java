package com.arsdigita.persistence;

import java.math.*;

/**
 * DataHandlerTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/10/01 $
 **/

public class DataHandlerTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/DataHandlerTest.java#1 $ by $Author: rhs $, $DateTime: 2002/10/01 16:08:31 $";

    public DataHandlerTest(String name) {
        super(name);
    }

    public void test() {
        Session ssn = SessionManager.getSession();
        DataObject data = ssn.create(new OID("test.dataHandler.Test",
                                             BigInteger.ZERO));
        data.set("value", "foo");
        data.save();

        assertTrue(!FakeDataHandler.DELETED.contains(data));
        data.delete();
        assertTrue(FakeDataHandler.DELETED.contains(data));
    }

}
