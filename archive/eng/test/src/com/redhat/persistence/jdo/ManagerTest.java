package com.redhat.persistence.jdo;

import javax.jdo.JDOHelper;

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
}
