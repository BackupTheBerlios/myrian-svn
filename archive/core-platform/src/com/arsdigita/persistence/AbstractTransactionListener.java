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
 * @version $Revision: #1 $ $Date: 2003/06/19 $
 **/

public class AbstractTransactionListener implements TransactionListener {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/AbstractTransactionListener.java#1 $ by $Author: rhs $, $DateTime: 2003/06/19 17:51:50 $";

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
