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

/**
 * Title:       TransactionContext class
 *              This class is intentionally NOT threadsafe;
 *              it should not be shared across threads.
 * Description: The TransactionContext class encapsulates a database transaction.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/04/08 $
 */

public class TransactionContext {

    String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/TransactionContext.java#5 $ by $Author: ashah $, $DateTime: 2003/04/08 10:31:44 $";

    private static boolean s_aggressive = false;

    private Session m_ossn;
    private com.arsdigita.persistence.proto.Session m_ssn;

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
    }

    /**
     * Commits the current transaction.
     *  @pre inTxn()
     *
     *  @post !inTxn()
     **/

    public void commitTxn() {
	try {
            m_ssn.commit();
	} finally {
            m_ossn.freeConnection();
            m_ossn.invalidateDataObjects(true);
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
	try {
	    m_ssn.rollback();
	} finally {
	    m_ossn.freeConnection();
            m_ossn.invalidateDataObjects(false);
	}
    }

    /**
     * Register a one time transaction event listener
     */
    public void addTransactionListener(TransactionListener listener) {
        //throw new Error("not implemented");
    }

    /**
     * Unregister a transaction event listener. There is
     * generally no need to call this method, since transaction
     * listeners are automatically removed after they have been
     * invoked to prevent infinite recursion.
     */
    public void removeTransactionListener(TransactionListener listener) {
        throw new Error("not implemented");
    }

    /**
     * Returns true if there is currently a transaction in progress.
     *
     * @return True if a transaction is in progress, false otherwise.
     **/

    public boolean inTxn() {
        return true;
    }

    /**
     * Returns the isolation level of the current transaction.
     *
     * @pre inTxn() == true
     *
     * @return The isolation level of the current transaction.
     **/

    public int getTransactionIsolation() {
        throw new Error("not implemented");
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
        throw new Error("not implemented");
    }

    static boolean getAggressiveClose() {
        return s_aggressive;
    }

    static void setAggressiveClose(boolean value) {
        s_aggressive = value;
    }

}
