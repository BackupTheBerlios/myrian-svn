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
package org.myrian.persistence.jdo;

import org.myrian.persistence.*;
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
            // http://java.sun.com/products/jdo/javadocs/javax/jdo/Transaction.html#begin%28%29
            throw new JDOUserException("double begin");
        }

        m_inTxn = true;
    }

    public void commit() {
        try {
            ssn().commit();
            m_pmi.commit();
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
        if (value) {
            throw new JDOUnsupportedOptionException();
        }
    }

    public void setRestoreValues(boolean value) {
        throw new JDOUnsupportedOptionException();
    }

    public void setRetainValues(boolean value) {
        throw new JDOUnsupportedOptionException();
    }
}
