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
package com.arsdigita.tools.junit.framework;

import junit.framework.*;
//import com.arsdigita.persistence.Session;
//import com.arsdigita.persistence.SessionManager;
//import com.arsdigita.kernel.TestHelper;
import org.apache.log4j.Logger;

import java.util.Locale;

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

        /*Session sess = SessionManager.getSession();
        sess.getTransactionContext().beginTxn();
        TestHelper.setLocale(Locale.ENGLISH);*/
    }

    protected void baseTearDown() {
        /*Session sess = SessionManager.getSession();
        if (sess.getTransactionContext().inTxn()) {
            sess.getTransactionContext().abortTxn();
            }*/

        s_log.info (this.getClass().getName() + " finished");
    }
}
