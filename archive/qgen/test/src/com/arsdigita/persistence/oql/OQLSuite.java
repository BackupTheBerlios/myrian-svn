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

package com.arsdigita.persistence.oql;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * OQLSuite
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public class OQLSuite extends PackageTestSuite {

    public final static String versionId = "$Id: //core-platform/test-qgen/test/src/com/arsdigita/persistence/oql/OQLSuite.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    public OQLSuite() {}

    public OQLSuite(Class theClass) {
        super(theClass);
    }

    public OQLSuite(String name) {
        super(name);
    }

    public static Test suite() {
        OQLSuite suite = new OQLSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new CoreTestSetup(suite);
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

}
