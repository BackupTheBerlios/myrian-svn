/*
 * Copyright (C) 2003, 2003 Red Hat Inc. All Rights Reserved.
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
