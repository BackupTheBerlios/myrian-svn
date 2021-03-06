/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
