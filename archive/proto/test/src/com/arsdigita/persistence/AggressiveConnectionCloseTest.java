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

package com.arsdigita.persistence;

import com.arsdigita.db.SQLExceptionHandler;
import com.arsdigita.logging.SecureLogFilter;

import java.io.*;
import java.io.StringWriter;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.extensions.*;
import junit.framework.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.TTCCLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.varia.DenyAllFilter;
import org.apache.log4j.varia.StringMatchFilter;

/**
 * This test verifies the aggressive connection closing functionality
 * by looking for appropriate log statements.
 *
 * @author David Eison
 */
public class AggressiveConnectionCloseTest extends Log4jBasedTestCase {

    public static final String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/AggressiveConnectionCloseTest.java#6 $";

    private Session ssn;

    boolean originalCloseValue = false;

    public AggressiveConnectionCloseTest(String name) {
        super(name);
    }

    // the idea here is to pick an incredibly dirt-simple PDL file that
    // has an insert statement
    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/mdsql/Datatype.pdl");
        super.persistenceSetUp();
    }

    /**
     * Turns on all logging so that the desired log messages will be found.
     */
    public void setUp() throws Exception {
        super.setUp();

        ssn = getSession();
        originalCloseValue = ssn.getTransactionContext().getAggressiveClose();
    }

    /**
     * Restores all logging & aggressive connection close setting to original state.
     */
    public void tearDown() throws Exception {
        super.tearDown();

        ssn.getTransactionContext().setAggressiveClose(originalCloseValue);
    }

    public void testAggressiveClosing() {
        StringMatchFilter filterReturn = new StringMatchFilter();
        String returnString = "connectionUserCountHitZero returning connection";
        filterReturn.setStringToMatch(returnString);
        filterReturn.setAcceptOnMatch(true);
        log.addFilter(filterReturn);
        log.addFilter(new DenyAllFilter());

        ssn.getTransactionContext().setAggressiveClose(true);

        // do something simple, should result in a holding on to the
        // connection
        DataObject dt = ssn.create("examples.Datatype");
        dt.set("id", BigInteger.ZERO);
        dt.save();

        assertLogDoesNotContain(returnString);

        clearLog();

        // abort prev transaction, start a new one, so that we can have a
        // clean connection
        ssn.getTransactionContext().commitTxn();
        ssn.getTransactionContext().beginTxn();

        // do something else simple, should result in a return message
        dt = ssn.retrieve(new OID("examples.Datatype", BigInteger.ZERO));
        assertNotNull("Should have actually retrieved something", dt);
        try {
            assertLogContains(returnString);

            ssn.getTransactionContext().setAggressiveClose(false);

            // abort prev transaction, start a new one, so that we can have a
            // clean connection
            ssn.getTransactionContext().abortTxn();
            ssn.getTransactionContext().beginTxn();

            // test w/ aggressive closing off, shouldn't result in either message
            clearLog();

            dt = ssn.retrieve(new OID("examples.Datatype", BigInteger.ZERO));
            dt.set("date", new java.util.Date(1000));
            dt.save();

            assertLogDoesNotContain(returnString);
        } finally {
            // delete, since we had to commit earlier.
            dt.delete();
            ssn.getTransactionContext().commitTxn();
            ssn.getTransactionContext().beginTxn();
        }
    }
}
