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

import java.util.*;
import javax.jdo.*;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

public class MemoryTest extends AbstractCase {

    private static final Logger s_log = Logger.getLogger(QueryTest.class);

    private Department m_bigDept = null;

    public MemoryTest() {}

    public MemoryTest(String name) {
        super(name);
    }

    protected void setUp() {
        m_pm.currentTransaction().begin();
        m_bigDept = new Department("big");
        m_pm.makePersistent(m_bigDept);
        m_pm.currentTransaction().commit();
    }

    public void testBasic() {
        for (int i = 0; i < 10; i++) {
            m_pm.currentTransaction().begin();
            BigEmployee e = new BigEmployee("big" + i, null);
            e.setSalary(new Float(1.0f));
            m_pm.makePersistent(e);
            m_pm.currentTransaction().commit();
        }
    }

    public void testRef() {
        for (int i = 0; i < 10; i++) {
            m_pm.currentTransaction().begin();
            BigEmployee e = new BigEmployee("big" + i, m_bigDept);
            e.setSalary(new Float(1.0f));
            m_pm.makePersistent(e);
            m_pm.currentTransaction().commit();
        }
    }
}
