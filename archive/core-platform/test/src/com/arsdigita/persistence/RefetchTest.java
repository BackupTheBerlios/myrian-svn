package com.arsdigita.persistence;

import java.math.*;
import org.apache.log4j.Category;

/**
 * RefetchTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/06/24 $
 **/

public class RefetchTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/RefetchTest.java#2 $ by $Author: jorris $, $DateTime: 2002/06/24 13:04:06 $";

    private static final Category s_log =
        Category.getInstance(RefetchTest.class);

    public RefetchTest(String name) {
        super(name);
    }

    static {
        load("com/arsdigita/persistence/testpdl/mdsql/RefetchTest.pdl");
    }

    private static final String REFETCH_TEST = "refetchTest.RefetchTest";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PARENT = "parent";

    private static final BigInteger NODE_ID = BigInteger.ZERO;
    private static final String NODE_NAME = "Node Name";
    private static final OID NODE_OID = new OID(REFETCH_TEST, NODE_ID);

    private static final BigInteger PARENT_ID = BigInteger.ONE;
    private static final String PARENT_NAME = "Parent Name";
    private static final OID PARENT_OID = new OID(REFETCH_TEST, PARENT_ID);

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
