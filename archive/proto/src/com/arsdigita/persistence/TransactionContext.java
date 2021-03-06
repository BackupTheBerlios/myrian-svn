/*
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
 */

package com.arsdigita.persistence;

import com.arsdigita.util.*;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Title:       TransactionContext class
 *              This class is intentionally NOT threadsafe;
 *              it should not be shared across threads.
 * Description: The TransactionContext class encapsulates a database transaction.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2003/08/04 $
 */

public class TransactionContext {

    String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/TransactionContext.java#10 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private static final Logger s_cat =
        Logger.getLogger(TransactionContext.class);

    private static boolean s_aggressive = false;

    private Session m_ossn;
    // used in test infrastructure
    com.redhat.persistence.Session m_ssn;
    private Map m_attrs = new HashMap();
    private ArrayList m_listeners = new ArrayList();
    private boolean m_inTxn = false;

    TransactionContext(com.arsdigita.persistence.Session ssn) {
        m_ossn = ssn;
        m_ssn = ssn.getProtoSession();
    }

    /**
     * Begins a new transaction.
     *
     * Update 8/7/01: This now makes a connection available, but doesn't
     * actually open a connection and associate it with the thread.
     * The 'transaction' will not actually start until the first data
     * modification, at which point the connection will be married to the
     * thread.
     *
     * This should be a transparent behavior change introduced as a
     * performance optimization, SDM #159142.
     **/

    public void beginTxn() {
        // Do nothing. This is implicit now.
        if (m_inTxn) {
            throw new IllegalStateException("double begin");
        }

        m_inTxn = true;
    }

    /**
     * Commits the current transaction.
     *  @pre inTxn()
     *
     *  @post !inTxn()
     **/

    public void commitTxn() {
        boolean success = false;
        try {
            fireBeforeCommitEvent();
            m_ssn.flush();
            m_ossn.invalidateDataObjects(true, false);
            m_ssn.commit();
            success = true;
            m_inTxn = false;
            fireCommitEvent();
        } finally {
            m_inTxn = false;
            clearAttributes();
            if (!success) { m_ossn.invalidateDataObjects(false, true); }
        }
    }

    /**
     * Used by test infrastructure. Replaces the actual commit with a
     * specified runnable.
     */
    void testCommitTxn(Runnable r) {
        boolean success = false;
        try {
            fireBeforeCommitEvent();
            m_ssn.flush();
            m_ossn.invalidateDataObjects(true, false);
            r.run();
            success = true;
            m_inTxn = false;
            fireCommitEvent();
        } finally {
            m_inTxn = false;
            clearAttributes();
            if (!success) { m_ossn.invalidateDataObjects(false, true); }
        }
    }

    /**
     * Aborts the current transaction.
     *
     *  @pre inTxn()
     *
     *  @post !inTxn()
     **/

    public void abortTxn() {
        boolean success = false;
        try {
            fireBeforeAbortEvent();
            m_ossn.invalidateDataObjects(false, false);
            m_ssn.rollback();
            success = true;
        } finally {
            m_inTxn = false;
            m_ossn.freeConnection();
            if (!success) { m_ossn.invalidateDataObjects(false, true); }
            fireAbortEvent();
            clearAttributes();
        }
    }

    /**
     * Register a one time transaction event listener
     */
    public void addTransactionListener(TransactionListener listener) {
        m_listeners.add(listener);
    }

    /**
     * Unregister a transaction event listener. There is
     * generally no need to call this method, since transaction
     * listeners are automatically removed after they have been
     * invoked to prevent infinite recursion.
     */
    public void removeTransactionListener(TransactionListener listener) {
        m_listeners.remove(listener);
    }

    /*
     * NB, this method is delibrately private, since we don't
     * want it being fired at any other time than immediately
     * before the transaction
     */
    private void fireBeforeCommitEvent() {
        Assert.assertTrue
	    (inTxn(), "The beforeCommit event was fired outside of " +
	     "the transaction");

        Object listeners[] = m_listeners.toArray();

        for (int i = 0 ; i < listeners.length ; i++) {
	    if (s_cat.isDebugEnabled()) {
		s_cat.debug("Firing transaction beforeCommit event");
	    }
            TransactionListener listener = (TransactionListener)listeners[i];
            listener.beforeCommit(this);
        }
    }

    /*
     * NB, this method is delibrately private, since we don't
     * want it being fired at any other time than immediately
     * after the transaction
     */
    private void fireCommitEvent() {
        Assert.assertTrue
	    (!inTxn(), "transaction commit event fired during transaction");

        Object listeners[] = m_listeners.toArray();
        m_listeners.clear();

        for (int i = 0 ; i < listeners.length ; i++) {
	    if (s_cat.isDebugEnabled()) {
		s_cat.debug("Firing transaction commit event");
	    }
            TransactionListener listener = (TransactionListener)listeners[i];
            listener.afterCommit(this);
        }

        Assert.assertTrue
	    (!inTxn(), "transaction commit listener didn't close transaction");
    }

    /*
     * NB, this method is delibrately private, since we don't
     * want it being fired at any other time than immediately
     * before the transaction
     */
    private void fireBeforeAbortEvent() {
        Assert.assertTrue
	    (inTxn(), "The beforeAbort event was fired outside of " +
	     "the transaction");

        Object listeners[] = m_listeners.toArray();
        for (int i = 0 ; i < listeners.length ; i++) {
	    if (s_cat.isDebugEnabled()) {
		s_cat.debug("Firing transaction beforeAbort event");
	    }
            TransactionListener listener = (TransactionListener)listeners[i];
            listener.beforeAbort(this);
        }
    }

    /*
     * NB, this method is delibrately private, since we don't
     * want it being fired at any other time than immediately
     * after the transaction
     */
    private void fireAbortEvent() {
        Assert.assertTrue
	    (!inTxn(), "transaction abort event fired during transaction");

        Object listeners[] = m_listeners.toArray();
        m_listeners.clear();

        for (int i = 0 ; i < listeners.length ; i++) {
	    if (s_cat.isDebugEnabled()) {
		s_cat.debug("Firing transaction abort event");
	    }
            TransactionListener listener = (TransactionListener)listeners[i];
            listener.afterAbort(this);
        }

        Assert.assertTrue
	    (!inTxn(), "transaction abort listener didn't close transaction");
    }

    /**
     * Returns true if there is currently a transaction in progress.
     *
     * @return True if a transaction is in progress, false otherwise.
     **/

    public boolean inTxn() {
        return m_inTxn;
    }

    /**
     * Returns the isolation level of the current transaction.
     *
     * @pre inTxn() == true
     *
     * @return The isolation level of the current transaction.
     **/

    public int getTransactionIsolation() {
        try {
            Connection conn = m_ossn.getConnection();
	    return conn.getTransactionIsolation();
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        }
    }

    /**
     * Sets the isolation level of the current transaction.
     *
     * @pre inTxn() == true
     * @post getTransactionIsolation() == level
     *
     * @param level The desired isolation level.
     **/
    public void setTransactionIsolation(int level) {
        try {
            Connection conn = m_ossn.getConnection();
	    conn.setTransactionIsolation(level);
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        }
    }

    /**
     * Set an attribute inside of this <code>TransactionContext</code>.  The
     * attribute will exist as long as the transaction is opened.  When the
     * transaction is closed or aborted, the attribute will be discarded. This
     * method is analogous to {@link
     * javax.servlet.ServletRequest#setAttribute(String, Object)}
     *
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @post getAttribute(name) == value
     */
    public void setAttribute(String name, Object value) {
        m_attrs.put(name, value);
    }

    /**
     * Get an attribute inside of this <code>TransactionContext</code>.  The
     * attribute will exist as long as the transaction is opened.  When the
     * transaction is closed or aborted, the attribute will be discarded. This
     * method is analogous to {@link
     * javax.servlet.ServletRequest#getAttribute(String)}
     *
     * @param name the name of the attribute
     * @return the value of the attribute, or null if no attribute with
     *   this value has been stored
     */
    public Object getAttribute(String name) {
        return m_attrs.get(name);
    }

    /**
     * Remove an attribute from this <code>TransactionContext</code>.  be
     * discarded. This method is analogous to {@link
     * javax.servlet.ServletRequest#removeAttribute(String)}
     *
     * @param name the name of the attribute to remove
     * @post getAttribute(name) == null
     */
    public void removeAttribute(String name) {
        m_attrs.remove(name);
    }

    void clearAttributes() {
        m_attrs.clear();
    }

    static boolean getAggressiveClose() {
        return s_aggressive;
    }

    static void setAggressiveClose(boolean value) {
        s_aggressive = value;
    }

}
