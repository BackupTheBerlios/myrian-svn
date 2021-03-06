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

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

/**
 * DataQueryTest
 *
 * This class tests DataQuery, using data contained in
 * //enterprise/infrastructure/dev/persistence/sql/data-query-test.sql
 *
 *  This data must be loaded as a precondition of this test running.
 *
 * This test class focuses only on the DataQuery interface. It doesn't
 * test named data queries, DataQueryImpl protected methods, etc. It is
 * intended to be inherited by TestCases that test DataQuery derived classes.
 * This is important as most DataQuery derived classes may be changed to no
 * longer implement this interface. Limiting the scope of this test case
 * facilitates this future refactoring.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 */
public abstract class DataQueryTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/test-qgen/test/src/com/arsdigita/persistence/DataQueryTest.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    public DataQueryTest(String name) {
        super(name);
    }
    /*
     *  The following test methods are use filters and orders, which are specific
     *  to the named DataQuery examples.DataQuery. Child tests must override
     *  these methods.
     */


    /**
     *  Tests the ordering capability of DataQuery.
     *  Checks forward, reverse, and multiple field ordering.
     *
     */
    public abstract void testSetOrder();

    /**
     *  Tests the ordering capability of DataQuery.
     *  Checks forward, reverse, and multiple field ordering.
     *
     */
    public abstract void testAddOrder();

    /**
     *  This tests the ability to add multiple filters to a data query
     */
    public abstract void testAddFilter();


    /*
     *  The following test methods are generic to any DataQuery. I.e. they don't
     *  use filters or order bys, which restrict the actual query.
     *
     */

    public void testGetPosition() {
        DataQuery dq = getDefaultQuery();
        for (int i = 1; dq.next(); i++) {
            assertEquals("getPosition failed.",
                         i,
                         dq.getPosition());
        }
    }

    /**
     *  This tests cursor methods: first(), last(), isFirst(), isLast(),
     *  previous(), next().
     *
     * Fails because first/last/isLast/previous are unsupported for
     * forward-only resultsets.
     *
     * @see <a href="http://developer.arsdigita.com/acs5/sdm/one-ticket?
     ticket_id=140759">Bug #140759</a>
    */
    public void FAILStestCursorMovement() {

        // TODO: uncomment This test will fail, since the cursor
        // methods are not properly implemented.  The code is
        // commented out until we work out whether and how to
        // incorporate tests that are _known_ to fail into the build
        // system. Currently, this test would break the weekly formal
        // build.

        DataQuery dq = getDefaultQuery();

        // first is unsupported for foward-only resultsets.
        assertTrue("first() Failed! There should be rows in this query!",
               dq.first());

        assertTrue("Should be on the first row!", dq.isFirst() );

        assertTrue("last() Failed! There should be rows in this query!",
               dq.last());
        assertTrue("Should be on the last row!", dq.isLast() );
        try {
            dq.next();
            dq.get("id");
            fail("Should throw exception if get() called when cursor off " +
                 "DataQuery");
        }
        catch (PersistenceException e) {
        }

        // Back to the beginning
        assertTrue("first() Failed! There should be rows in this query!",
               dq.first());
        assertTrue("Should be on the first row!", dq.isFirst() );

        BigDecimal firstValue = (BigDecimal) dq.get("id");
        dq.next();

        BigDecimal secondValue = (BigDecimal) dq.get("id");
        dq.next();

        dq.previous();
        assertTrue( "previous() did not work first time!",
                secondValue.equals(dq.get("id")) );
        dq.previous();
        assertTrue( "previous() did not work second time!",
                firstValue.equals(dq.get("id")) );
        assertTrue("Should be on the first row!", dq.isFirst() );

    }

    public void testSizeAndPosition() {
        DataQuery dq = getDefaultQuery();
        final long size = dq.size();
        // Seems silly, but there were severe errors with size() in another
        // persistence class.
        assertEquals("Size differs on second call!", size, dq.size() );

        long count = 0;
        while (dq.next()) {
            count++;
            assertEquals("Position information invalid!", count, dq.getPosition() );
        }

        assertEquals("Count of records and size differ!", count, size );
        // Once more for paranoia's sake...
        assertEquals("Size differs on third call!", size, dq.size() );

    }

    public void testIsEmpty() {
        DataQuery dq = getDefaultQuery();
        dq.setFilter("1 = 2");
        assertTrue("isEmpty failed.", dq.isEmpty());
        dq.next();
        assertTrue("isEmpty failed.", dq.isEmpty());
        dq.close();

        dq = getDefaultQuery();
        assertTrue("isEmpty failed.", !dq.isEmpty());

        while (dq.next()) {
            assertTrue("isEmpty failed.", !dq.isEmpty());
        }
    }

    public void testOutOfCursors() {
        for (int i = 0; i < 1000; i++ ) {
            DataQuery dq = getDefaultQuery();
            Filter f = dq.setFilter("1 = 2");
            try {
                while (dq.next());
            } catch (Exception e) {
                dq.close();
                fail("Problem on iteration " + i + "; this is probably the \n" +
                     "out of cursors problem, and will probably cause some \n" +
                     "trouble with test teardown as well. \n" +
                     "Error is:" + e.toString());
            }
        }
    }


    /**
     *  Creates a default DataQuery for use by the framework
     *
     *  @return The DataQuery.
     */
    protected abstract DataQuery getDefaultQuery();


    public static void main(String args[]) {
        junit.textui.TestRunner.run(DataQueryTest.class);
    }

}
