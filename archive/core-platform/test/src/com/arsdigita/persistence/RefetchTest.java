package com.arsdigita.persistence;

import java.math.*;
import org.apache.log4j.Logger;

/**
 * RefetchTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2002/08/13 $
 **/

public class RefetchTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/RefetchTest.java#5 $ by $Author: dennis $, $DateTime: 2002/08/13 11:53:00 $";

    private static final Logger s_log =
        Logger.getLogger(RefetchTest.class);

    private static final BigInteger NODE_ID = BigInteger.ZERO;
    private static final String NODE_NAME = "Node Name";

    private static final BigInteger PARENT_ID = BigInteger.ONE;
    private static final String PARENT_NAME = "Parent Name";
    private static OID PARENT_OID = null;
    private static OID NODE_OID;
    private static final String REFETCH_TEST = "refetchTest.RefetchTest";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PARENT = "parent";

    public RefetchTest(String name) {
        super(name);
    }


    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/mdsql/RefetchTest.pdl");
        super.persistenceSetUp();
        if (PARENT_OID == null) {
            PARENT_OID = new OID(REFETCH_TEST, PARENT_ID);
        }
        if (NODE_OID == null) {
            NODE_OID = new OID(REFETCH_TEST, NODE_ID);
        }
    }

    public void test() {
        Session ssn = SessionManager.getSession();
        DataObject node = ssn.create(REFETCH_TEST);
        DataObject parent = ssn.create(REFETCH_TEST);

        parent.set(ID, PARENT_ID);
        parent.set(NAME, PARENT_NAME);
        parent.save();

        node.set(ID, NODE_ID);
        node.set(NAME, NODE_NAME);
        node.set(PARENT, parent);

        node.save();

        DataCollection nodes = ssn.retrieve(REFETCH_TEST);
        try {
            nodes.addEqualsFilter(ID, NODE_ID);
         //   s_log.warn("Node size: " + nodes.size());
             if (nodes.next()) {
                 node = nodes.getDataObject();
             } else {
                 fail("Node wasn't saved properly.");
             }

             DataObject newParent = ssn.retrieve(NODE_OID);

             BigInteger preID = (BigInteger) newParent.get(ID);

             node.set(PARENT, newParent);
             node.get(NAME);

             BigInteger postID = (BigInteger) newParent.get(ID);

             assertEquals(preID, postID);

        } finally {
            try {
                nodes.close();
            } catch (Exception e) {
                s_log.error("Error closing", e);
            }
        }
    }

}
