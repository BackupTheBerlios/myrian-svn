package com.redhat.persistence.jdo;

public class WithoutTxnTest extends AbstractCase {
    public WithoutTxnTest() {}

    public WithoutTxnTest(String name) {
        super(name);
    }

    public void testMakePersistent() {
        Employee e = new Employee("name", null);
        e.setSalary(new Float(1.0f));
        m_pm.makePersistent(e);
    }
}
