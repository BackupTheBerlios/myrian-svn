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
 * This test verifies that the Statements which are created by Persistence
 * are closed when their ResultSet is closed, by looking for appropriate
 * log statements.
 *
 * @author David Eison
 */
public class StatementClosingTest extends Log4jBasedTestCase {

    public static final String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/StatementClosingTest.java#2 $";

    private Session ssn;

    boolean originalCloseValue = false;

    public StatementClosingTest(String name) {
        super(name);
    }

    /**
     * Turns on all logging so that the desired log messages will be found.
     */
    public void setUp() throws Exception {
        super.setUp();

        ssn = getSession();
        if (true) throw new Error("fix: originalCloseValue = ssn.getTransactionContext().getAggressiveClose();");
        runFinalization(false);
    }

    /**
     * Restores all logging & aggressive connection close setting to original state.
     */
    public void tearDown() throws Exception {
        super.tearDown();

        throw new Error("fix: ssn.getTransactionContext().setAggressiveClose(originalCloseValue);");
    }

    public void testStatementClosing() {
        StringMatchFilter closeFilter = new StringMatchFilter();
        String closeString = "Closing Statement because resultset was closed";
        closeFilter.setStringToMatch(closeString);
        closeFilter.setAcceptOnMatch(true);

        log.addFilter(closeFilter);
        log.addFilter(new DenyAllFilter());

        if (true) throw new Error("fix: ssn.getTransactionContext().setAggressiveClose(true);");

        // do something simple
        DataObject dt = ssn.create("examples.Datatype");
        dt.set("id", BigInteger.ZERO);
        dt.save();

        runFinalization();

        assertLogDoesNotContain(closeString);

        DataCollection dc = ssn.retrieve("examples.Datatype");
        dc.addEqualsFilter("id", BigInteger.ZERO);
        dc.next();
        dc.close();
        assertLogContains(closeString);
    }

    /**
     * The point of this test is to see if making a data association and
     * calling cursor on it requires one to close the data association
     * explicitly.
     */
    public void testDataAssociationClosing() {
        StringMatchFilter daFilter = new StringMatchFilter();
        String daString = "Statement was not closed by programmer";
        daFilter.setStringToMatch(daString);
        daFilter.setAcceptOnMatch(true);
        log.addFilter(daFilter);

        StringMatchFilter closeFilter = new StringMatchFilter();
        String closeString = "close: ";
        closeFilter.setStringToMatch(closeString);
        closeFilter.setAcceptOnMatch(true);
        log.addFilter(closeFilter);

        log.addFilter(new DenyAllFilter());

        OrderAssociation oa = new OrderAssociation( ssn );
        DataAssociation items = oa.getLineItems();
        DataAssociationCursor cursor = items.cursor();
        int i = 0;
        while (cursor.next()) {
            i++;
        }
        assertTrue("Iterations should match cursor size but did not, " + i +
                   " vs " + cursor.size(),
                   cursor.size() == i);
        assertTrue("Sizes should match but did not, only found " + i +
                   " line items",
                   OrderAssociation.NUM_ITEMS == i);
        items = null;
        cursor = null;

        runFinalization();

        assertLogDoesNotContain(daString);
        assertLogContains(closeString);
    }

    private void runFinalization() {
        runFinalization(true);
    }

    private void runFinalization(boolean logging) {
        if (!logging) { Logger.getRoot().removeAppender(log); }

        // do everything we can to encourage garbage collection
        System.gc();
        try {
            Thread.sleep(100);
        } catch  (InterruptedException e) {}
        System.runFinalization();

        if (!logging) { Logger.getRoot().addAppender(log); }
    }
}
