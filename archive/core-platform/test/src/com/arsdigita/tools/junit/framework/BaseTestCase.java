package com.arsdigita.tools.junit.framework;

import junit.framework.*;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import org.apache.log4j.Category;

public abstract class BaseTestCase extends TestCase {

    private static Category s_log = 
        Category.getInstance(BaseTestCase.class.getName());

    /**
     * Constructs a test case with the given name.
     */
    public BaseTestCase(String name) {
        super(name);
    }

    /**
     * Runs the bare test sequence.
     * @exception Throwable if any exception is thrown
     */
    public void runBare() throws Throwable {
        baseSetUp();
        try {
            try {
                setUp();
                runTest();
            } catch(Throwable t) {
                try {
                    tearDown();
                } catch (Throwable t2) {
                    System.err.println ( "Error in teardown: " );
                    t2.printStackTrace ( System.err );
                }
                throw t;
            }
            tearDown();
        } finally {
            baseTearDown ();
        }
    }

    protected void baseSetUp() {
        s_log.info (this.getClass().getName() + " started");

        Session sess = SessionManager.getSession();
        sess.getTransactionContext().beginTxn();
    }

    protected void baseTearDown() {
        Session sess = SessionManager.getSession();
        if (sess.getTransactionContext().inTxn()) {
            sess.getTransactionContext().abortTxn();
        }

        s_log.info (this.getClass().getName() + " finished");
    }
}
