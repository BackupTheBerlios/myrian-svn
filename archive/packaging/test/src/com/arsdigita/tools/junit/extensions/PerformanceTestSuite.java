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

package com.arsdigita.tools.junit.extensions;

import com.arsdigita.categorization.*;
import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.PerformanceSuite;

import junit.framework.Test;


public class PerformanceTestSuite extends PerformanceSuite {

    public PerformanceTestSuite () {
        super();
    }

/*
    EXAMPLE:

    public static Test suite () {

        PerformanceTestSuite suite = new PerformanceTestSuite ();

        Test CategoryTest = new CategoryTest ( "testEquals" );
        suite.addTest ( CategoryTest );
        Test CategoryTest2 = new CategoryTest ( "testSetGetProperties" );
        suite.addTest ( CategoryTest2 );
        Test CategoryTest3 = new CategoryTest ( "testDeleteCategory" );
        suite.addTest ( CategoryTest3 );

        BaseTestSetup wrapper = new BaseTestSetup(suite);
        wrapper.setInitScriptTarget ("com.arsdigita.kernel.security.Initializer");
        return wrapper;
    }
    */
}
