/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.persistence.oql;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;

/**
 * OQLSuite
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/17 $
 **/

public class OQLSuite extends TestSuite {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/oql/OQLSuite.java#2 $ by $Author: rhs $, $DateTime: 2004/08/17 17:46:29 $";

    public static Test suite() {
        OQLSuite suite = new OQLSuite();
        suite.addTestSuite(QueryTest.class);
        BaseTestSetup wrapper = new BaseTestSetup(suite);
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

}
