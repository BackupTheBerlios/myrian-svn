/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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
 * AbstractTransactionListener is a default implementation of {@link
 * TransactionListener}. Callback methods do nothing by default and
 * are intended to be selectively overridden in order to add the
 * desired behavior.
 *
 * @see TransactionContext
 * @see TransactionListener
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/15 $
 **/

public class AbstractTransactionListener implements TransactionListener {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/AbstractTransactionListener.java#2 $ by $Author: dennis $, $DateTime: 2003/08/15 13:46:34 $";

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
