/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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
 * A simple listener to allow tasks to be performed
 * after a transaction is committed or rolled back.
 *
 * A typical use of this listener would be handling
 * repopulation of a data object cache (cf SiteNode).
 *
 * To prevent infinite recursion in the case where
 * the listener itself uses a transaction, listener
 * invocations are one time events - ie the listener
 * is removed immediately after it has run.
 *
 * @see com.arsdigita.persistence.TransactionContext
 * @author Daniel Berrange
 */

public interface TransactionListener {

    /**
     * Called immediately before the transaction has committed
     */
    public void beforeCommit(TransactionContext txn);

    /**
     * Called immediately after the transaction has committed
     */
    public void afterCommit(TransactionContext txn);

    /**
     * Called immediately before the transaction has aborted
     */
    public void beforeAbort(TransactionContext txn);

    /**
     * Called immediately after the transaction has aborted
     */
    public void afterAbort(TransactionContext txn);
}
