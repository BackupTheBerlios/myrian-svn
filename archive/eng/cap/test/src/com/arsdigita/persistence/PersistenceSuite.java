/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.arsdigita.persistence;

import junit.framework.*;

/**
 * PersistenceSuite
 *
 * @author Jon Orris
 * @version $Revision: #4 $ $Date: 2004/09/01 $
 */
public class PersistenceSuite extends TestSuite {
    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/PersistenceSuite.java#4 $ by $Author: rhs $, $DateTime: 2004/09/01 10:15:50 $";

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
