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
import org.apache.log4j.Logger;

/**
 * ExtendLobTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class ExtendLobTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/ExtendLobTest.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private static final Logger LOG = Logger.getLogger(ExtendLobTest.class);

    private static final String EXTEND_LOB =
        "com.arsdigita.persistence.ExtendLob";

    public ExtendLobTest(String name) {
        super(name);
    }

    public void test() {
        Session ssn = SessionManager.getSession();

        DataObject data = ssn.create(new OID(EXTEND_LOB, new BigDecimal("0")));
        data.set("lob", "value");
        data.save();
    }

}
