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
