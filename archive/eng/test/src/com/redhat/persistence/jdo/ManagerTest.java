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

import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.spi.JDOImplHelper;
import org.apache.log4j.Logger;

/**
 * This tests PersistenceManagerImpl and StateManagerImpl.
 **/
public class ManagerTest extends WithTxnCase {

    private static final Logger s_log = Logger.getLogger(ManagerTest.class);

    private Group m_group;

    public ManagerTest() {}

    public ManagerTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        m_group = new Group(0);
        m_group.setEmail("java-project@redhat.com");
        m_group.setName("Java Hackers");
        m_pm.makePersistent(m_group);
    }

    public void testGetObjectId() {
        final Object id1 = m_pm.getObjectId(m_group);
        assertNotNull("id1", id1);

        // JDOHelper delegates to StateManager
        final Object id2 = JDOHelper.getObjectId(m_group);
        assertNotNull("id2", id2);

        assertEquals("identities", id1, id2);
    }

    public void testPersistentNewDeleted() {
        m_pm.deletePersistent(m_group);
        try {
            m_group.getName();
            // See Section 5.5.7.
            fail("should've thrown an exception");
        } catch (JDOUserException ex) {
            s_log.debug("expected exception", ex);
        }
    }

    // public void testNewObjectIdInstance() {
    //     final String strid = m_pm.getObjectId(m_group).toString();
    //     assertNotNull("strid", strid);
    // 
    //     Object id = JDOImplHelper.getInstance().
    //         newObjectIdInstance(Group.class, strid);
    // 
    //     assertNotNull("id", id);
    // 
    //     Group group = (Group) m_pm.getObjectById(id, false);
    // 
    //     assertEquals("groups", m_group, group);
    // }
}
