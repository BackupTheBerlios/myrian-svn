/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
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

import com.redhat.persistence.TestSession;

public class TestTransaction {

    private TestTransaction() { } // no construction allowed
    /**
     * This method fakes a commit of a transaction. Transaction listeners are
     * executed and the underlying implementation does everything except
     * actually commit the transcaction. Consecutive calls to this method do
     * not require (or allow) intervening calls to begin transaction.
     */
    public static void testCommitTxn(final TransactionContext txn) {
        txn.testCommitTxn(new Runnable() {
            public void run() {
                TestSession.testCommit(txn.m_ssn);
            }
        });
        txn.beginTxn();
    }
}
