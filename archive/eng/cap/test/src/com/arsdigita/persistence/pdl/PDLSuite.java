/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
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
 */
package com.arsdigita.persistence.pdl;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import junit.framework.*;

/**
 * PDLSuite
 *
 * @author Jon Orris
 * @version $Revision: #5 $ $Date: 2004/09/01 $
 */
public class PDLSuite extends TestSuite {
    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/pdl/PDLSuite.java#5 $ by $Author: dennis $, $DateTime: 2004/09/01 11:40:07 $";

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
