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
package com.arsdigita.persistence;

import java.math.*;

/**
 * DeletionTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2004/05/03 $
 **/

public class DeletionTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //users/rhs/persistence/cap/test/src/com/arsdigita/persistence/DeletionTest.java#1 $ by $Author: rhs $, $DateTime: 2004/05/03 11:48:01 $";

    public DeletionTest(String name) {
        super(name);
    }

    public void testRemoveOpt2manyBack() {
        Session ssn = SessionManager.getSession();
        OID TEST = new OID("test.Test", BigInteger.ZERO);
        OID ICLE = new OID("test.Icle", BigInteger.ZERO);

        DataObject icle = ssn.create(ICLE);
        icle.save();

        DataObject test = ssn.create(TEST);
        test.set("required", icle);
        test.save();

        icle.set("opt2manyBack", test);
        icle.save();

        icle.set("opt2manyBack", null);
        icle.save();

        icle = ssn.retrieve(ICLE);
        assertEquals(null, icle.get("opt2manyBack"));
    }

}
