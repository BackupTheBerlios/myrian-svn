/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.arsdigita.persistence;

import junit.framework.*;

/**
 * PersistenceSuite
 *
 * @author Jon Orris
 * @version $Revision: #5 $ $Date: 2004/09/01 $
 */
public class PersistenceSuite extends TestSuite {
    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/PersistenceSuite.java#5 $ by $Author: dennis $, $DateTime: 2004/09/01 11:40:07 $";

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

        PersistenceTestSetup wrapper = new PersistenceTestSetup(suite);
        wrapper.addSQLSetupScript("com/arsdigita/persistence/setup.sql");
        wrapper.addSQLTeardownScript("com/arsdigita/persistence/teardown.sql");

        return wrapper;
    }

}
