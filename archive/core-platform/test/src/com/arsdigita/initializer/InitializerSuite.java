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

package com.arsdigita.initializer;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * @author Jon Orris
 * @version $Revision: #6 $ $Date: 2003/10/28 $
 */
public class InitializerSuite extends PackageTestSuite {
    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/initializer/InitializerSuite.java#6 $ by $Author: jorris $, $DateTime: 2003/10/28 13:22:55 $";

    public static Test suite() {
        InitializerSuite suite = new InitializerSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new CoreTestSetup(suite);
        return wrapper;
    }
}
