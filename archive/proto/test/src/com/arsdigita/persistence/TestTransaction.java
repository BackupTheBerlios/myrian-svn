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
