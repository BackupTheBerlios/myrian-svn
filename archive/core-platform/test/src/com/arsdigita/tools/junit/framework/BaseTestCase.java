/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.tools.junit.framework;

import junit.framework.*;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import org.apache.log4j.Logger;

public abstract class BaseTestCase extends TestCase {

    private static Logger s_log =
        Logger.getLogger(BaseTestCase.class.getName());

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
        s_log.warn (this.getClass().getName() + "." + getName() +  " started");

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
