/*
 * Copyright (C) 2002-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
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
