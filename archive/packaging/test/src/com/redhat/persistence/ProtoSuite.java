/*
 * Copyright (C) 2003, 2003 Red Hat Inc. All Rights Reserved.
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

package com.redhat.persistence;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * ProtoSuite
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 **/

public class ProtoSuite extends PackageTestSuite {

    public final static String versionId = "$Id: //core-platform/test-packaging/test/src/com/redhat/persistence/ProtoSuite.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

    public ProtoSuite() {}

    public ProtoSuite(Class theClass) {
        super(theClass);
    }

    public ProtoSuite(String name) {
        super(name);
    }

    public static Test suite() {
        ProtoSuite suite = new ProtoSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new BaseTestSetup(suite);
        wrapper.setInitScriptTarget("com.arsdigita.db.Initializer");
        wrapper.setSetupSQLScript
            (System.getProperty("test.sql.dir") +
             "/com/arsdigita/persistence/setup.sql");
        wrapper.setTeardownSQLScript
            (System.getProperty("test.sql.dir") +
             "/com/arsdigita/persistence/teardown.sql");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

}
