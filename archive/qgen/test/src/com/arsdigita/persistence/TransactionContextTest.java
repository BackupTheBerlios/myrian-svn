/*
 * Copyright (C) 2001, 2002, 2003, 2003 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.tools.junit.framework.BaseTestCase;

/*
 * TransactionContextTest
 *
*/

public class TransactionContextTest extends BaseTestCase {

    public TransactionContextTest(String name) {
   	super(name);
    }
  

    /**
     * Tests TransactionContext to check that it is properly.
     * See bugzilla 117883.
     */
    public void testCommitErrorHandling() throws Exception {
   	Session ssn = SessionManager.getSession();
	TransactionContext txn = ssn.getTransactionContext();
	TestTransactionListener listener = new TestTransactionListener();
	txn.addTransactionListener(listener);
	
	try {
	    txn.commitTxn();	
	    fail("Didn't throw exception");
	} catch(BeforeCommitException e) {
	    assertTrue("Not in a transaction", txn.inTxn());
	    assertFalse("Transaction was aborted somehow?", listener.m_isAborted);
	    txn.abortTxn();
	    assertTrue("Transaction not aborted?", listener.m_isAborted);
	}
    }
   
    private static final class BeforeCommitException extends RuntimeException {
   	public BeforeCommitException(String name) {
	    super(name);
	}
    }

    private static final class TestTransactionListener implements  TransactionListener {
	boolean m_isAborted = false;
	public void beforeCommit(TransactionContext txn) {
	    throw new BeforeCommitException("Testing commit error handling");
	}
	
	public void afterCommit(TransactionContext txn) {}
	public void beforeAbort(TransactionContext txn) {
	    m_isAborted = true;
	}
	public void afterAbort(TransactionContext txn) {}
	
    }

}
