/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.metadata;

import com.arsdigita.persistence.PersistenceTestCase;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.PersistenceException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @version $Revision: #4 $ $Date: 2002/08/14 $
 */

public class DynamicAssociationTest extends PersistenceTestCase {
    private static Logger s_log =
        Logger.getLogger(DynamicAssociationTest.class.getName());

    private MetadataRoot m_root = MetadataRoot.getMetadataRoot();
    private List m_tables = new ArrayList();
    private List m_assocs = new ArrayList();

    public DynamicAssociationTest(String name) {
        super(name);
    }

    protected void persistenceTearDown() {
        load("com/arsdigita/persistence/testpdl/static/DataOperation.pdl");
        Iterator iter = m_tables.iterator();
        java.sql.Statement statement = null;
        try {
            statement = SessionManager.getSession()
                .getConnection()
                .createStatement();
            while (iter.hasNext()) {
                String table = (String)iter.next();
                try {
                    statement.executeUpdate("drop table " + table);
                } catch (Exception e) {
                    s_log.info("Error executing statement " +
                               "'drop table " + table + "': " + e);
                }
            }
        } catch (Exception e) {
            s_log.info("Error creating statement: " + e.getMessage());
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
                //ignore
            }
        }

        getSession().getTransactionContext().abortTxn();
        getSession().getTransactionContext().beginTxn();

        iter = m_assocs.iterator();
        while (iter.hasNext()) {
            DataOperation operation = SessionManager.getSession()
                .retrieveDataOperation
                ("examples.DataOperationToDeleteTestDynamicAssociations");
            Association assoc = (Association)iter.next();
            String model = assoc.getModel().getName();
            String type1 = ((ObjectType)assoc.getRoleOne().getType())
                .getQualifiedName();
            String type2 = ((ObjectType)assoc.getRoleTwo().getType())
                .getQualifiedName();
            String prop1 = assoc.getRoleOne().getName();
            String prop2 = assoc.getRoleTwo().getName();

            operation.setParameter("modelName", model);
            operation.setParameter("objectType1", type1);
            operation.setParameter("property1", prop1);
            operation.setParameter("objectType2", type2);
            operation.setParameter("property2", prop2);
            operation.execute();
        }
        // this is here so that the "delete" operation above takes
        getSession().getTransactionContext().commitTxn();
        getSession().getTransactionContext().beginTxn();

        super.persistenceTearDown();
    }


    public void testCreation() throws Exception {
        DynamicAssociation dass = new DynamicAssociation(
                                                         "teststuff.foo",
                                                         "com.arsdigita.kernel.ACSObject",
                                                         "owned",
                                                         Property.COLLECTION,
                                                         "com.arsdigita.kernel.User",
                                                         "owner",
                                                         Property.REQUIRED);

        Association assoc = dass.save();
        ObjectType object =
            m_root.getObjectType("com.arsdigita.kernel.ACSObject");
        ObjectType user =
            m_root.getObjectType("com.arsdigita.kernel.User");

        assert("Property not found in User",
               user.getProperty("owned") != null);
        assert("Property not found in ACSObject",
               object.getProperty("owner") != null);

        DynamicAssociation dass2 = new DynamicAssociation(
                                                          "teststuff.foo",
                                                          "com.arsdigita.kernel.ACSObject",
                                                          "owned",
                                                          "com.arsdigita.kernel.User",
                                                          "owner");

        Association assoc2 = dass2.save();
        assert("Saved associations are different", assoc.equals(assoc2));

        try {
            dass2 = new DynamicAssociation(
                                           "teststuff.foo",
                                           "com.arsdigita.kernel.ACSObject",
                                           "container",
                                           "com.arsdigita.kernel.User",
                                           "owner");

            fail("No error thrown on bad association");
        } catch (Exception e) {
        }

        // assume there's at least one user in the system
        DataCollection collection =
            getSession().retrieve("com.arsdigita.kernel.User");

        if (collection.next()) {
            DataObject userObj = collection.getDataObject();
            OID userID = userObj.getOID();

            DataObject testObj1 =
                getSession().create("com.arsdigita.kernel.ACSObject");
            testObj1.set("id", new BigDecimal(-101));
            testObj1.set("objectType", "com.arsdigita.kernel.ACSObject");
            testObj1.set("displayName", "Test Object 1");
            testObj1.save();
            OID testID1 = testObj1.getOID();

            DataObject testObj2 =
                getSession().create("com.arsdigita.kernel.ACSObject");
            testObj2.set("id", new BigDecimal(-102));
            testObj2.set("objectType", "com.arsdigita.kernel.ACSObject");
            testObj2.set("displayName", "Test Object 2");
            testObj2.save();
            OID testID2 = testObj2.getOID();

            DataAssociation owned = (DataAssociation)userObj.get("owned");
            owned.add(testObj1);
            owned.add(testObj2);
            userObj.save();

            userObj = getSession().retrieve(userID);
            testObj1 = getSession().retrieve(testID1);
            testObj2 = getSession().retrieve(testID2);

            owned = (DataAssociation)userObj.get("owned");
            DataAssociationCursor cursor = owned.cursor();

            assert("Incorrect number of objects associated",
                   cursor.size() == 2);

            boolean found1 = false;
            boolean found2 = false;

            while (cursor.next()) {
                DataObject next = cursor.getDataObject();

                if (next.equals(testObj1)) {
                    found1 = true;
                } else if (next.equals(testObj2)) {
                    found2 = true;
                } else {
                    fail("Incorrect dataobject retrieved");
                }

                cursor.remove();
            }
            userObj.save();

            assert("Association was missing an object", found1 && found2);

            testObj1.delete();
            testObj2.delete();
        }

        collection.close();

        // clean up
        String tableName = ((JoinElement)assoc
                            .getRoleOne()
                            .getJoinPath()
                            .getJoinElements()
                            .next()).getTo().getTableName();
        m_tables.add(tableName);
        m_assocs.add(assoc);
    }
}
