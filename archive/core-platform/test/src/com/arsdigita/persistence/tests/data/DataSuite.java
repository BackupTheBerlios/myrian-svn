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

package com.arsdigita.persistence.tests.data;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * DataSuite
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2003/02/19 $
 **/

public class DataSuite extends PackageTestSuite {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/tests/data/DataSuite.java#3 $ by $Author: jorris $, $DateTime: 2003/02/19 22:26:51 $";

    public DataSuite() {}

    public DataSuite(Class theClass) {
        super(theClass);
    }

    public DataSuite(String name) {
        super(name);
    }

    public static Test suite() {
        DataSuite suite = new DataSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new BaseTestSetup(suite);
        wrapper.setInitScriptTarget("com.arsdigita.persistence.Initializer");
        wrapper.addSQLSetupScript(System.getProperty("test.sql.dir") +
                                  "/com/arsdigita/persistence/tests/data/setup.sql");
        wrapper.addSQLTeardownScript(System.getProperty("test.sql.dir") +
                                     "/com/arsdigita/persistence/tests/data/teardown.sql");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

}
