/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.persistence;

import java.math.*;
import org.apache.log4j.Logger;

/**
 * RefetchTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public class RefetchTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/RefetchTest.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

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
