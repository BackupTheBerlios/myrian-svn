package com.redhat.persistence.jdo;

import javax.jdo.JDOUserException;

public class WithoutTxnTest extends AbstractCase {
    public WithoutTxnTest() {}

    public WithoutTxnTest(String name) {
        super(name);
    }

    protected void tearDown() throws Exception {
        ((PersistenceManagerImpl) m_pm).getConnection().close();
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
