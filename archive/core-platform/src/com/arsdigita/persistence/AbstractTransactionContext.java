package com.arsdigita.persistence;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.arsdigita.db.Connection;

/**
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * @author bche@redhat.com
 */
public abstract class AbstractTransactionContext
	implements TransactionContext {

    private Map m_attrs = new HashMap();

	/**
	 * @see com.arsdigita.db.ConnectionUseListener#connectionUserCountHitZero(Connection)
	 */
    abstract	public void connectionUserCountHitZero(Connection conn) throws java.sql.SQLException;

	/**
	 * @see com.arsdigita.persistence.TransactionContext#beginTxn()
	 */
	abstract public void beginTxn() throws PersistenceException;

	/**
	 * @see com.arsdigita.persistence.TransactionContext#commitTxn()
	 */
	public void commitTxn() throws PersistenceException {
        try {
            commitTxnImpl();
        } catch(PersistenceException e) {
            throw e;
        } finally {
            clearAttributes();
        }
    }
    
    /**
     * Performs the actual work of committing a transaction.  This method is called
     * from inside commitTxn()
     */
    abstract protected void commitTxnImpl() throws PersistenceException;

	/**
	 * @see com.arsdigita.persistence.TransactionContext#abortTxn()
	 */
	public void abortTxn() throws PersistenceException {
        try {
            abortTxnImpl();
        } catch (PersistenceException e) {
            throw e;
        } finally {
            clearAttributes();
        }
    }
    
    /**
     *  Performs the actual work of aborting a transaction.  This method is called
     * from inside of abortTxn*(
     */
    abstract protected void abortTxnImpl() throws PersistenceException;

	/**
	 * @see com.arsdigita.persistence.TransactionContext#addTransactionListener(TransactionListener)
	 */
	abstract public void addTransactionListener(TransactionListener listener);
    
	/**
	 * @see com.arsdigita.persistence.TransactionContext#removeTransactionListener(TransactionListener)
	 */
	abstract public void removeTransactionListener(TransactionListener listener);

	/**
	 * @see com.arsdigita.persistence.TransactionContext#inTxn()
	 */
	abstract public boolean inTxn() throws PersistenceException;

	/**
	 * @see com.arsdigita.persistence.TransactionContext#getTransactionIsolation()
	 */
	abstract public int getTransactionIsolation() throws PersistenceException;

	/**
	 * @see com.arsdigita.persistence.TransactionContext#setTransactionIsolation(int)
	 */
	abstract public void setTransactionIsolation(int level) throws PersistenceException;

	/**
	 * @see com.arsdigita.persistence.TransactionContext#setAttribute(String, Object)
	 */
	public void setAttribute(String name, Object value) throws PersistenceException  {
        m_attrs.put(name, value);
    }

	/**
	 * @see com.arsdigita.persistence.TransactionContext#getAttribute(String)
	 */
	public Object getAttribute(String name) throws PersistenceException {
        return m_attrs.get(name);
	}

	/**
	 * @see com.arsdigita.persistence.TransactionContext#removeAttribute(String)
	 */
	public void removeAttribute(String name) throws PersistenceException {
        m_attrs.remove(name);
	}
    
    /**
     * Clears all attributes in the transaction context.  This method should be called when
     * comitting or aborting a transaction
     */
    protected void clearAttributes() throws PersistenceException {
        m_attrs.clear();
    }

}
