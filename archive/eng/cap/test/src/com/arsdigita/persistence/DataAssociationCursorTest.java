/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
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
import com.arsdigita.persistence.metadata.ObjectType;
import java.math.BigDecimal;
import junit.framework.*;
import java.util.*;

/**
 * DataCollectionImplTest
 *
 * This class tests DataCollectionImplTest, using the Node.pdl data definition.
 *
 *  This data must be loaded as a precondition of this test running.
 *
 * @author Jon Orris
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 */
public class DataAssociationCursorTest extends DataCollectionTest {

    public static final String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/DataAssociationCursorTest.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    OrderAssociation m_orderAssoc;

    public DataAssociationCursorTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/Order.pdl");
        super.persistenceSetUp();
    }

    protected void persistenceTearDown() {
        super.persistenceTearDown();
    }

    /**
     * Used by JUnit (called before each test method)
     **/
    protected void setUp() {
        m_orderAssoc = new OrderAssociation( getSession() );
    }

    /**
     * Used by JUnit (called after each test method)
     **/
    protected void tearDown() {
        m_orderAssoc.tearDown();
    }

    public void testGetDataAssociation() {
        DataAssociation items = m_orderAssoc.getLineItems();
        DataAssociationCursor cursor = items.cursor();

        assertEquals("Cursor didn't return parent association!", items, cursor.getDataAssociation());
    }

    public void testRemove() {
        DataAssociationCursor cursor = getItemsCursor();
        BigDecimal deadId = new BigDecimal(3);
        while(cursor.next()) {
            if( cursor.get("id").equals(deadId) ) {
                cursor.remove();
            }
        }

        cursor.rewind();
        boolean found = false;
        while(cursor.next() && !found) {
            found = cursor.get("id").equals(deadId);
        }

        assertFalse("Id " + deadId + " not removed from DataAssociation!",
		    found);
    }

    /**
     *  Tests the ordering capability of DataQuery.
     *  Checks forward, reverse, and multiple field ordering.
     *
     */
    public void testSetOrder() {

    }

    /**
     *  Tests the ordering capability of DataQuery.
     *  Checks forward, reverse, and multiple field ordering.
     *
     */
    public void testAddOrder() {

    }

    /**
     *  This tests the ability to add multiple filters to a data query
     */
    public void testAddFilter() {

    }

    protected DataQuery getDefaultQuery() {
        return getDefaultCollection();
    }

    protected DataCollection getDefaultCollection() {
        return getItemsCursor();
    }

    protected ObjectType getDefaultObjectType() {
        return m_orderAssoc.getLineItemType();
    }

    private DataAssociationCursor getItemsCursor()  {
        return m_orderAssoc.getLineItems().cursor();
    }
}
