package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import javax.jdo.*;
import javax.transaction.Synchronization;

class TransactionImpl implements Transaction {

    private PersistenceManagerImpl m_pmi;

    private boolean m_inTxn = false;
    private Synchronization m_sync = null;

    TransactionImpl(PersistenceManagerImpl pmi) {
        m_pmi = pmi;
    }

    private final Session ssn() {
        return m_pmi.getSession();
    }

    public PersistenceManager getPersistenceManager() {
        return m_pmi;
    }

    public void begin() {
        if (m_inTxn) {
            throw new IllegalStateException("double begin");
        }

        m_inTxn = true;
    }

    public void commit() {
        try {
            ssn().commit();
        } finally {
            m_inTxn = false;
        }
    }

    public void rollback() {
        try {
            ssn().rollback();
        } finally {
            m_inTxn = false;
        }
    }

    public boolean isActive() {
        return m_inTxn;
    }

    public Synchronization getSynchronization() {
        return m_sync;
    }

    public void setSynchronization(Synchronization sync) {
        // need to figure out how to call the sync callbacks
        throw new Error("not implemented");
    }

    public boolean getNontransactionalRead() { return false; }

    public boolean getNontransactionalWrite() { return false; }

    public boolean getOptimistic() { return false; }

    public boolean getRestoreValues() { return false; }

    public boolean getRetainValues() { return false; }

    public void setNontransactionalRead(boolean value) {
        throw new JDOUnsupportedOptionException();
    }

    public void setNontransactionalWrite(boolean value) {
        throw new JDOUnsupportedOptionException();
    }

    public void setOptimistic(boolean value) {
        throw new JDOUnsupportedOptionException();
    }

    public void setRestoreValues(boolean value) {
        throw new JDOUnsupportedOptionException();
    }

    public void setRetainValues(boolean value) {
        throw new JDOUnsupportedOptionException();
    }
}
