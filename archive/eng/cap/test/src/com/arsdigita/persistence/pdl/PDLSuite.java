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
package com.arsdigita.persistence.pdl;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * PDLSuite
 *
 * @author Jon Orris
 * @version $Revision: #3 $ $Date: 2004/09/01 $
 */
public class PDLSuite extends PackageTestSuite {
    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/pdl/PDLSuite.java#3 $ by $Author: rhs $, $DateTime: 2004/09/01 09:57:25 $";

    public static Test suite() {
        PDLSuite suite = new PDLSuite();
        suite.addTestSuite(AssociationMetadataTest.class);
        suite.addTestSuite(NameFilterTest.class);
        suite.addTestSuite(PDLTest.class);
        BaseTestSetup wrapper = new BaseTestSetup(suite);
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run( suite() );
    }

}
