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
 * @version $Revision: #8 $ $Date: 2003/02/26 $
 */

public interface TransactionContext extends com.arsdigita.db.ConnectionUseListener {
    String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/TransactionContext.java#8 $ by $Author: bche $, $DateTime: 2003/02/26 17:08:29 $";

    /**
     * 
     *
     * Called when a connection has zero users.
     * Will recycle the connection back into the pool if
     * conn.getNeedsAutoCommitOff reports false.
     * May be called via a finalizer, so can't count on thread safety.
     */
    void connectionUserCountHitZero(com.arsdigita.db.Connection conn)
        throws java.sql.SQLException;

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
     *
     * @throws PersistenceException is no longer really thrown, but
     *         this shouldn't affect anyone since it's a runtime type
     *         anyway.
     **/

    void beginTxn() throws PersistenceException;

    /**
     * Commits the current transaction.
     *  @pre inTxn()
     *
     *  @post !inTxn()
     **/

    void commitTxn() throws PersistenceException;

    /**
     * Aborts the current transaction.
     *
     *  @pre inTxn()
     *
     *  @post !inTxn()
     **/

    void abortTxn();

    /**
     * Register a one time transaction event listener
     */
    void addTransactionListener(TransactionListener listener);

    /**
     * Unregister a transaction event listener. There is
     * generally no need to call this method, since transaction
     * listeners are automatically removed after they have been
     * invoked to prevent infinite recursion.
     */
    void removeTransactionListener(TransactionListener listener);

    /**
     * Returns true if there is currently a transaction in progress.
     *
     * @return True if a transaction is in progress, false otherwise.
     * @throws PersistenceException is no longer really thrown, but
     *         this shouldn't affect anyone since it's a runtime type
     *         anyway.
     **/

    boolean inTxn() throws PersistenceException;

    /**
     * Returns the isolation level of the current transaction.
     *
     * @pre inTxn() == true
     *
     * @return The isolation level of the current transaction.
     **/

    int getTransactionIsolation() throws PersistenceException;

    /**
     * Sets the isolation level of the current transaction.
     *
     * @pre inTxn() == true
     * @post getTransactionIsolation() == level
     *
     * @param level The desired isolation level.
     **/
    void setTransactionIsolation(int level)
        throws PersistenceException;

    /**
     * Set an attribute inside of this <code>TransactionContext</code>.
     * The attribute will exist as long as the transaction is opened.
     * When the transaction is closed or aborted, the attribute will
     * be discarded. This method is analogous to 
     * {@link #ServletRequest.setAttribute(String, Object)}
     *
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @post getAttribute(name) == value
     */
    public void setAttribute(String name, Object value) throws PersistenceException;

    /**
     * Get an attribute inside of this <code>TransactionContext</code>.
     * The attribute will exist as long as the transaction is opened.
     * When the transaction is closed or aborted, the attribute will
     * be discarded. This method is analogous to 
     * {@link #ServletRequest.getAttribute(String)}
     *
     * @param name the name of the attribute
     * @return the value of the attribute, or null if no attribute with
     *   this value has been stored
     */
    public Object getAttribute(String name) throws PersistenceException;

    /**
     * Remove an attribute from this <code>TransactionContext</code>.
     * be discarded. This method is analogous to 
     * {@link #ServletRequest.removeAttribute(String)}
     *
     * @param name the name of the attribute to remove
     * @post getAttribute(name) == null
     */
    public void removeAttribute(String name) throws PersistenceException;
}
