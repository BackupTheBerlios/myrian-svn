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
package com.redhat.persistence.jdo;

import javax.jdo.JDOUserException;

public class JDOStateTest extends AbstractCase {
    public JDOStateTest() {}

    public JDOStateTest(String name) {
        super(name);
    }

    public void testMakePersistent() {
        JDOState state = new JDOState();
        assertTrue("hollow", state.isHollow());
        state.makePersistent();
        assertTrue("hollow", state.isHollow());
        state.makeTransactional();
        assertTrue("persistent", state.isPersistent());
        assertTrue("clean", !state.isDirty());
    }
}
