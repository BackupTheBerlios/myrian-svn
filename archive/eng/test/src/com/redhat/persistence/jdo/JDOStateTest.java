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
