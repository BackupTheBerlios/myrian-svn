/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence;


/**
 * A convenience class for implementing the {@link TransactionListener}
 * interface.
 *
 * @see com.arsdigita.persistence.TransactionContext
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 */

public class TransactionListenerImpl implements TransactionListener {
    
  /**
   * Called immediately before the transaction has committed
   */
  public void beforeCommit(TransactionContext txn) {
    // Do nothing
  }

  /**
   * Called immediately after the transaction has committed
   */
  public void afterCommit(TransactionContext txn) {
    // Do nothing
  }
  
  /**
   * Called immediately before the transaction has aborted
     */
  public void beforeAbort(TransactionContext txn) {
    // Do nothing
  }
  
  /**
   * Called immediately after the transaction has aborted
   */
  public void afterAbort(TransactionContext txn) {
    // Do nothing
  }
}


