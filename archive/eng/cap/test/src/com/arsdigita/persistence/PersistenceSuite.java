/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import junit.framework.*;

import com.redhat.persistence.pdl.*;
import java.sql.*;
import java.util.regex.*;

/**
 * PersistenceSuite
 *
 * @author Jon Orris
 * @version $Revision: #2 $ $Date: 2004/07/08 $
 */
public class PersistenceSuite extends TestSuite {
    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/PersistenceSuite.java#2 $ by $Author: rhs $, $DateTime: 2004/07/08 17:46:05 $";

    public static Test suite() {
        PersistenceSuite suite = new PersistenceSuite();

        suite.addTestSuite(InitializerTest.class);

        suite.addTestSuite(MetadataDebuggingTest.class);

        // API tests
        suite.addTestSuite(OIDTest.class);
        suite.addTestSuite(GenericDataObjectTest.class);
        suite.addTestSuite(GenericDataQueryTest.class);
        suite.addTestSuite(DataQueryImplTest.class);
        suite.addTestSuite(DataCollectionImplTest.class);
        suite.addTestSuite(DataAssociationImplTest.class);
        suite.addTestSuite(DataAssociationCursorTest.class);
        suite.addTestSuite(DataOperationTest.class);
        suite.addTestSuite(FilterTest.class);

        // Datatype tests
        suite.addTestSuite(LobTest.class);
        suite.addTestSuite(DatatypeTest.class);

        // Tests of more subtle semantics
        suite.addTestSuite(DeletionTest.class);
        suite.addTestSuite(ExtendLobTest.class);
        suite.addTestSuite(LazyLoadFailureTest.class);
        suite.addTestSuite(RefetchTest.class);
        suite.addTestSuite(ObserverTest.class);
        suite.addTestSuite(MultiThreadDataObjectTest.class);

        // Connection/transaction related tests
        suite.addTestSuite(AggressiveConnectionCloseTest.class);
        suite.addTestSuite(StatementClosingTest.class);
        suite.addTestSuite(PooledConnectionSourceTest.class);
        suite.addTestSuite(TransactionContextTest.class);

        // Data manipulation tests
        suite.addTest(MetaTest.suite());
        suite.addTestSuite(DynamicLinkAttributeTest.class);
        suite.addTestSuite(DynamicLinkTest.class);
        suite.addTestSuite(DynamicNodeTest.class);
        suite.addTestSuite(DynamicOrderTest.class);
        suite.addTestSuite(DynamicPartyTest.class);
        suite.addTestSuite(StaticLinkAttributeTest.class);
        suite.addTestSuite(StaticLinkTest.class);
        suite.addTestSuite(StaticNodeTest.class);
        suite.addTestSuite(StaticOrderTest.class);
        suite.addTestSuite(StaticPartyTest.class);

        BaseTestSetup wrapper = new BaseTestSetup(suite) {
            protected void setUp() throws Exception {
                super.setUp();
                // XXX: hack for getting session to load via static
                // initializer in PersistenceTestCase
                Class dummy = PersistenceTestCase.class;
                Session ssn = SessionManager.getSession();
                Connection conn = ssn.getConnection();
                Schema.load(ssn.getMetadataRoot().getRoot(), conn);
                conn.commit();
            }
            protected void tearDown() throws Exception {
                Session ssn = SessionManager.getSession();
                Connection conn = ssn.getConnection();
                Schema.unload(ssn.getMetadataRoot().getRoot(), conn);
                super.tearDown();
                conn.commit();
            }
        };

        wrapper.addSQLSetupScript("com/arsdigita/persistence/setup.sql");
        wrapper.addSQLTeardownScript("com/arsdigita/persistence/teardown.sql");

        return wrapper;
    }

}
