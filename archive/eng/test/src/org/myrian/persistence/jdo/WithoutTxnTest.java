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
package org.myrian.persistence.jdo;

import javax.jdo.JDOUserException;

public class WithoutTxnTest extends AbstractCase {
    public WithoutTxnTest() {}

    public WithoutTxnTest(String name) {
        super(name);
    }

    public void testMakePersistent() {
        Employee e = new Employee("name", null);
        e.setSalary(new Float(1.0f));
        try {
            m_pm.makePersistent(e);
            fail("JDOUserException must be thrown");
        } catch (JDOUserException _) {
            ; // expected
        }
    }
}
