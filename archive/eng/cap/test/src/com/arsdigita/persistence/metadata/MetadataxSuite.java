/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.arsdigita.persistence.metadata;
import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * MetadataSuite - Suite of tests for persistence.metadata
 *
 * @author Jon Orris
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 */
public class MetadataxSuite extends PackageTestSuite {
    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/metadata/MetadataxSuite.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
