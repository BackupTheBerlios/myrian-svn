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

import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.spi.JDOImplHelper;

/**
 * This tests PersistenceManagerImpl and StateManagerImpl.
 **/
public class ManagerTest extends WithTxnCase {

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
            ; // expected
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
