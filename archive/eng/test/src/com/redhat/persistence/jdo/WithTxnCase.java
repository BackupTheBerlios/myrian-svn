package com.redhat.persistence.jdo;

public abstract class WithTxnCase extends AbstractCase {
    public WithTxnCase() {}

    public WithTxnCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        m_pm.currentTransaction().begin();
    }

}
