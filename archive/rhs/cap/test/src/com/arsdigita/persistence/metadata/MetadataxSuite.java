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
package com.arsdigita.persistence.metadata;
import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * MetadataSuite - Suite of tests for persistence.metadata
 *
 * @author Jon Orris
 * @version $Revision: #1 $ $Date: 2004/05/03 $
 */
public class MetadataxSuite extends PackageTestSuite {
    public final static String versionId = "$Id: //users/rhs/persistence/cap/test/src/com/arsdigita/persistence/metadata/MetadataxSuite.java#1 $ by $Author: rhs $, $DateTime: 2004/05/03 11:48:01 $";

    public static Test suite() {
        MetadataxSuite suite = new MetadataxSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new BaseTestSetup(suite);

        wrapper.setSetupSQLScript("/../default/persistence/aggressive-test.sql");
        wrapper.setTeardownSQLScript("/../default/persistence/aggressive-teardown.sql");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run( suite() );

    }

}
