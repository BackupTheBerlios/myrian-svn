package com.redhat.persistence.jdo;

public abstract class WithTxnCase extends AbstractCase {
    public WithTxnCase() {}

    public WithTxnCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        m_pm.currentTransaction().begin();
    }

    protected void tearDown() {
        if (m_pm.currentTransaction().isActive()) {
            m_pm.currentTransaction().rollback();
        }
    }
}