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

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;

import com.arsdigita.db.ConnectionManager;
import com.arsdigita.util.Assert;

/**
 * Title:       TransactionContext class
 *              This class is intentionally NOT threadsafe;
 *              it should not be shared across threads.
 * Description: The TransactionContext class encapsulates a database transaction.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 */

class TransactionContextImpl implements TransactionContext {

    private String m_url, m_username, m_password;
    private boolean m_inTransaction = false;

    private ArrayList m_listeners;

    private static final Logger s_cat =
        Logger.getLogger(TransactionContext.class);

    /**
     * Flags whether aggressive closing will be used to return
     * transactions to the pool when there are no more users.
     */
    private static boolean s_aggressiveClose = false;

    private int m_isolationLevel = Integer.MIN_VALUE;

    public TransactionContextImpl() {
        m_listeners = new ArrayList();
    }

    /**
     * Sets the connection info for this transaction.
     **/

    protected void setConnectionInfo(String url, String username,
                                     String password) {
        m_url = url;
        m_username = username;
        m_password = password;
    }

    /**
     * Returns the current connection.
     *
     * @return The currenct connection.
     * @exception PersistenceException thrown if the calling
     *            code does not have an open transaction or if
     *            there is a problem getting a connection.
     **/
    protected Connection getConnection() throws PersistenceException {
        Connection retval = ConnectionManager.getCurrentThreadConnection();
        try {
            if (m_inTransaction) {
                if (retval == null) {
                    // Note that this code will only be run up until the point
                    // where this thread modifies data; after that point,
                    // ConnectionManager.getCurrentThreadConnection will
                    // return the connection.

                    // Also note that this actually working is relying on some
                    // special code in com.arsdigita.db.Statement and
                    // com.arsdigita.db.Connection
                    // whereby statement causes connection to keep a count of
                    // its users.
                    retval = ConnectionManager.getConnection();
                    ConnectionManager.setCurrentThreadConnection(retval);
                    if (m_isolationLevel != Integer.MIN_VALUE) {
                        // user has explicitly set the isolation level to some
                        // value.
                        retval.setTransactionIsolation(m_isolationLevel);
                    }
                    if (s_aggressiveClose) {
                        ((com.arsdigita.db.Connection)retval).addConnectionUseListener(this);
                    }
                    if (s_cat.isDebugEnabled()) {
                        Throwable t = new Throwable("getConnection stack trace");
                        s_cat.debug("Got connection " + retval, t);
                    }
                }
            } else {
                String error = "Can't retrieve a connection outside of a " +
                    "transaction.  This typically means that you have cached " +
                    "a DataObject across page loads and are now trying to " +
                    "access it.  This operation is not currently supported.";
                throw new PersistenceException(error);
            }
            // TODO: benchmark w/ this call moved up to just after getting from pool.
            retval.setAutoCommit(false);
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        }
        return retval;
    }

    /**
     * 
     *
     * Called when a connection has zero users.
     * Will recycle the connection back into the pool if
     * conn.getNeedsAutoCommitOff reports false.
     * May be called via a finalizer, so can't count on thread safety.
     */
    public void connectionUserCountHitZero(com.arsdigita.db.Connection conn)
        throws java.sql.SQLException {
        if (!conn.getNeedsAutoCommitOff()) {
            com.arsdigita.db.Connection oldConn =
                (com.arsdigita.db.Connection)ConnectionManager.
                getCurrentThreadConnection();
            if (conn.equals(oldConn)) {
                if (s_cat.isDebugEnabled()) {
                    s_cat.debug("connectionUserCountHitZero returning " +
                                "connection " + conn + " to pool because no " +
                                "data modification was done",
                                new Throwable("Stack trace"));
                }
                conn.softClose();
                ConnectionManager.setCurrentThreadConnection(null);
            } else if (oldConn == null) {
                // we can't rely on getting connection from
                // ConnectionManager.getCurrentThreadConnection
                // because this can be invoked on the finalizer thread, so
                // the ThreadLocal can be null.
                // Unfortunately, this also means that we can't clean up the
                // 'currentThreadConnection' variable.  Conveniently, this shouldn't
                // be a problem since it can safely be re-used later.

                // TODO: sort out why this close call causes problems.
                //conn.softClose();
                s_cat.debug("connectionUserCountHitZero but conn " + conn +
                            " didn't match old conn of null (only a problem " +
                            "if the stack dump is not from a finalizer)",
                            new Throwable("Stack trace"));
            } else {
                s_cat.warn("connectionUserCountHitZero but conn " + conn +
                           " didn't match current thread connection " +
                           ConnectionManager.getCurrentThreadConnection() +
                           " in thread " + Thread.currentThread(),
                           new Throwable("Stack trace"));
            }
        } else {
            s_cat.debug("connectionUserCountHitZero holding on to " +
                        "connection " + conn +
                        " because data modification was done");
        }
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
     *
     * @throws PersistenceException is no longer really thrown, but
     *         this shouldn't affect anyone since it's a runtime type
     *         anyway.
     **/

    public void beginTxn() throws PersistenceException {
        if (s_cat.isDebugEnabled()) {
            Throwable t = new Throwable("beginTxn stack trace");
            s_cat.debug("Begin transaction", t);
        }
        if (!m_inTransaction) {
            m_inTransaction = true;
            // actual connection opening has been delayed until the
            // getConnection method.
        } else {
            throw new PersistenceException("Nesting transactions " +
                                           "is not supported.");
        }
    }

    /**
     * Commits the current transaction.
     *  @pre inTxn()
     *
     *  @post !inTxn()
     **/

    public void commitTxn() throws PersistenceException {
        boolean valid = false;
        try {
            Connection conn = ConnectionManager.getCurrentThreadConnection();
            if (s_cat.isDebugEnabled()) {
                Throwable t = new Throwable("commitTxn stack trace");
                s_cat.debug("Committing connection " + conn, t);
            }
            if (!inTxn()) {
                throw new PersistenceException("commitTxn() called while " +
                                               "not in a transaction");
            }
            m_inTransaction = false;
            if (conn != null) {
                s_cat.debug("Actually committing " + conn);
                conn.commit();
                valid = true;
                conn.setAutoCommit(true);
                conn.close();
                ConnectionManager.setCurrentThreadConnection(null);
            } else {
                valid = true;
            }
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        } finally {
            SessionManager.getInternalSession().disconnectDataObjects(valid);
            fireCommitEvent();
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
            Connection conn = ConnectionManager.getCurrentThreadConnection();
            if (s_cat.isDebugEnabled()) {
                Throwable t = new Throwable("abortTxn stack trace");
                s_cat.debug("Aborting connection " + conn, t);
            }
            if (!inTxn()) {
                throw new PersistenceException("abortTxn() called while not " +
                                               "in a transaction");
            }
            m_inTransaction = false;
            if (conn != null) {
                s_cat.debug("Actually aborting " + conn);
                conn.rollback();
                conn.setAutoCommit(true);
                conn.close();
                ConnectionManager.setCurrentThreadConnection(null);
            }
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        } finally {
            SessionManager.getInternalSession().disconnectDataObjects(false);
            fireAbortEvent();
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
     * after the transaction
     */
    private void fireCommitEvent() {
        Assert.assertTrue(!m_inTransaction, "transaction commit event fired during transaction");

        Object listeners[] = m_listeners.toArray();
        m_listeners.clear();

        for (int i = 0 ; i < listeners.length ; i++) {
            s_cat.debug("Firing transaction commit event");
            TransactionListener listener = (TransactionListener)listeners[i];
            listener.afterCommit(this);
        }

        Assert.assertTrue(!m_inTransaction, "transaction commit listener didn't close transaction");
    }


    /*
     * NB, this method is delibrately private, since we don't
     * want it being fired at any other time than immediately
     * after the transaction
     */
    private void fireAbortEvent() {
        Assert.assertTrue(!m_inTransaction, "transaction abort event fired during transaction");

        Object listeners[] = m_listeners.toArray();
        m_listeners.clear();

        for (int i = 0 ; i < listeners.length ; i++) {
            s_cat.debug("Firing transaction abort event");
            TransactionListener listener = (TransactionListener)listeners[i];
            listener.afterAbort(this);
        }

        Assert.assertTrue(!m_inTransaction, "transaction abort listener didn't close transaction");
    }

    /**
     * Returns true if there is currently a transaction in progress.
     *
     * @return True if a transaction is in progress, false otherwise.
     * @throws PersistenceException is no longer really thrown, but
     *         this shouldn't affect anyone since it's a runtime type
     *         anyway.
     **/

    public boolean inTxn() throws PersistenceException {
        return m_inTransaction;
    }

    /**
     * Returns the isolation level of the current transaction.
     *
     * @pre inTxn() == true
     *
     * @return The isolation level of the current transaction.
     **/

    public int getTransactionIsolation() throws PersistenceException {
        int retval;
        try {
            Connection conn = ConnectionManager.getCurrentThreadConnection();
            if (conn != null) {
                retval = conn.getTransactionIsolation();
            } else {
                if (m_isolationLevel != Integer.MIN_VALUE) {
                    // snag a temporary connection since we don't
                    // have one available.  This should basically inform
                    // the user of the default isolation level.
                    conn = ConnectionManager.getConnection();
                    retval = conn.getTransactionIsolation();
                    conn.close();
                } else {
                    retval = m_isolationLevel;
                }
            }
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        }
        return retval;
    }

    /**
     * Sets the isolation level of the current transaction.
     *
     * @pre inTxn() == true
     * @post getTransactionIsolation() == level
     *
     * @param level The desired isolation level.
     **/
    public void setTransactionIsolation(int level)
        throws PersistenceException {
        m_isolationLevel = level;
        try {
            Connection conn = ConnectionManager.getCurrentThreadConnection();
            if (conn != null) {
                conn.setTransactionIsolation(m_isolationLevel);
            }
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        }
    }

    /**
     * 
     *
     * Indicates whether aggressive closing will be used.
     */
    protected static void setAggressiveClose(boolean value) {
        s_aggressiveClose = value;
    }

    /**
     * 
     *
     * Indicates whether aggressive closing will be used.
     */
    protected static boolean getAggressiveClose() {
        return s_aggressiveClose;
    }

}
