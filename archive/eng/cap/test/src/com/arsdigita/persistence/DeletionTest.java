/*
 * Copyright (C) 2002-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.arsdigita.persistence;

import java.math.*;

/**
 * DeletionTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class DeletionTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/DeletionTest.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
