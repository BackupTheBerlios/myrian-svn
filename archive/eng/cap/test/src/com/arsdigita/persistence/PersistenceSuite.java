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

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import junit.framework.*;

import com.redhat.persistence.pdl.*;
import java.sql.*;
import java.util.regex.*;

/**
 * PersistenceSuite
 *
 * @author Jon Orris
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 */
public class PersistenceSuite extends TestSuite {
    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/PersistenceSuite.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
