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
 * Copyright:    Copyright (c) 2001
 * Company:      ArsDigita
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2002/08/22 $
 */

public interface TransactionContext extends com.arsdigita.db.ConnectionUseListener {
    String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/TransactionContext.java#5 $ by $Author: jorris $, $DateTime: 2002/08/22 10:38:41 $";

    /**
     * <b><font color="red">Experimental</font></b>
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

}
