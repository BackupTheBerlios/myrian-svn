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
 * LazyLoadFailureTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class LazyLoadFailureTest extends PersistenceTestCase {

    private static final Logger s_log =
        Logger.getLogger(LazyLoadFailureTest.class);

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/LazyLoadFailureTest.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public LazyLoadFailureTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/Node.pdl");
        super.persistenceSetUp();
    }

    public void test() {
        OID oid = new OID("examples.LazyNode", new BigDecimal(1));
        Session ssn = SessionManager.getSession();
        DataObject node = ssn.create(oid);
        node.set("name", "Test Node");
        node.save();

        DataQuery dq = ssn.retrieveQuery("examples.lazyNodesQuery");
        dq.addEqualsFilter("node.id", new BigDecimal(1));
        if (dq.next()) {
            node = (DataObject) dq.get("node");
        } else {
            fail("Lazy query didn't return any rows.");
        }

        try {
            String name = (String) node.get("lazyProperty");
            fail("Lazy load should have bombed out but didn't. It returned: ("
                 + name + ") instead.");
        } catch (PersistenceException e) {
            // Test passes
            if (s_log.isDebugEnabled()) {
                s_log.debug("Test passed with exeption", e);
            }
        }
    }

}
