package com.redhat.persistence.jdo;

import javax.jdo.JDOUserException;

public class JDOStateTest extends AbstractCase {
    public JDOStateTest() {}

    public JDOStateTest(String name) {
        super(name);
    }

    public void testMakePersistent() {
        JDOState state = new JDOState();
        assertTrue("transient", !state.isPersistent());
        state.makePersistent();
        assertTrue("persistent", state.isPersistent());
    }
}
