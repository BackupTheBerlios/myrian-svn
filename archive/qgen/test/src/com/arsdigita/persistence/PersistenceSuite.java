/*
 * Copyright (C) 2001, 2002, 2003, 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;
import junit.framework.TestCase;

/**
 * PersistenceSuite
 *
 * @author Jon Orris
 * @version $Revision: #2 $ $Date: 2004/01/29 $
 */
public class PersistenceSuite extends PackageTestSuite {
    public final static String versionId = "$Id: //core-platform/test-qgen/test/src/com/arsdigita/persistence/PersistenceSuite.java#2 $ by $Author: ashah $, $DateTime: 2004/01/29 12:35:08 $";

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
        //suite.addTestSuite(NullTest.class);

        populateSuite(suite);
        BaseTestSetup wrapper = new CoreTestSetup(suite);
        //wrapper.setPerformInitialization(false);

        wrapper.addSQLSetupScript("/com/arsdigita/persistence/setup.sql");
        wrapper.addSQLSetupScript("/persistence/setup.sql");
        wrapper.addSQLSetupScript("/com/arsdigita/persistence/static/setup.sql");
        wrapper.addSQLSetupScript("/com/arsdigita/persistence/mdsql/setup.sql");

        wrapper.addSQLTeardownScript("/com/arsdigita/persistence/mdsql/teardown.sql");
        wrapper.addSQLTeardownScript("/com/arsdigita/persistence/static/teardown.sql");
        wrapper.addSQLTeardownScript("/persistence/teardown.sql");
        wrapper.addSQLTeardownScript("/com/arsdigita/persistence/teardown.sql");

//        wrapper.setTeardownSQLScript("/persistence/teardown.sql");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run( suite() );

    }

}
