/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
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
import com.arsdigita.tools.junit.framework.BaseTestCase;

/*
 * TransactionContextTest
 *
*/

public class TransactionContextTest extends PersistenceTestCase {

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
