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
package com.arsdigita.persistence.pdl;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * PDLSuite
 *
 * @author Jon Orris
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 */
public class PDLSuite extends PackageTestSuite {
    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/pdl/PDLSuite.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    public static Test suite() {
        PDLSuite suite = new PDLSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new BaseTestSetup(suite);
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run( suite() );
    }

}
