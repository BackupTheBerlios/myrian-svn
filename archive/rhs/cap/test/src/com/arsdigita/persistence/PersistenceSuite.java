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
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;
import junit.framework.TestCase;

import com.redhat.persistence.pdl.*;
import java.sql.*;

/**
 * PersistenceSuite
 *
 * @author Jon Orris
 * @version $Revision: #4 $ $Date: 2004/05/28 $
 */
public class PersistenceSuite extends PackageTestSuite {
    public final static String versionId = "$Id: //users/rhs/persistence/cap/test/src/com/arsdigita/persistence/PersistenceSuite.java#4 $ by $Author: rhs $, $DateTime: 2004/05/28 09:15:25 $";

    public PersistenceSuite() {
        super();
    }

    public PersistenceSuite(Class theClass) {
        super(theClass);
    }

    public PersistenceSuite(String name) {
        super(name);
    }

    public static Test suite() {
        PersistenceSuite suite = new PersistenceSuite();
        populateSuite(suite);

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

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run( suite() );

    }

}
