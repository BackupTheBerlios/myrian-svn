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

package com.arsdigita.xml;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * @author Jon Orris
 * @version $Revision: #2 $ $Date: 2003/04/09 $
 */
public class XmlSuite extends PackageTestSuite {
    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/xml/XmlSuite.java#2 $ by $Author: rhs $, $DateTime: 2003/04/09 16:35:55 $";

    public static Test suite() {
        XmlSuite suite = new XmlSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new BaseTestSetup(suite);
        wrapper.setInitScriptTarget ("com.arsdigita.globalization.Initializer");
        return wrapper;
    }
}
