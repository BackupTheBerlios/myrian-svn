/*
 * Copyright (C) 2002-2004 Red Hat, Inc. All Rights Reserved.
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
package com.arsdigita.persistence.tests.data;

import com.arsdigita.persistence.PersistenceTestCase;
import com.arsdigita.persistence.metadata.*;
import java.util.*;

/**
 * CRUDTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2004/09/20 $
 **/

public class CRUDTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/tests/data/CRUDTest.java#4 $ by $Author: ashah $, $DateTime: 2004/09/20 16:51:05 $";

    public CRUDTest(String name) {
        super(name);
    }

    public void testExtendLob() {
        CRUDTestlet test =
            new CRUDTestlet("com.arsdigita.persistence.ExtendLob");
        test.run();
    }

    public void FAILStestGroup() {
        CRUDTestlet test = new CRUDTestlet("com.arsdigita.kernel.Group");
        test.run();
    }

    public void testIcle() {
        CRUDTestlet test = new CRUDTestlet("test.Icle");
        test.run();
    }

    public void testTest() {
        CRUDTestlet test = new CRUDTestlet("test.Test");
        test.run();
    }

    public void FAILStest() {
        DataSource ds1 = new DataSource("test1.key");
        DataSource ds2 = new DataSource("test2.key");

        MetadataRoot root = MetadataRoot.getMetadataRoot();
        ObjectType type =
            root.getObjectType("com.arsdigita.kernel.ACSObject");
        ObjectTree tree = new ObjectTree(type);
        for (Iterator it = type.getProperties(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            tree.addPath(prop.getName());
        }

        System.out.println(tree);
    }

}
