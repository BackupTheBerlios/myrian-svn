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
 * @author <a href="mailto:jorris@arsdigita.com">Jon Orris</a>
 * @version $Revision: #2 $ $Date: 2002/07/18 $
 */
public class DataAssociationCursorTest extends DataCollectionTest {

    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/DataAssociationCursorTest.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

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

        assertTrue("Id " + deadId + " removed from DataAssociation before save()!", found);

        // need to save parent DataObject for removal to have effect.
        m_orderAssoc.getOrder().save();
        cursor.rewind();
        while(cursor.next()) {
            if( cursor.get("id").equals(deadId) ){
                fail("Id " + deadId + " not removed from DataAssociation!");
            }
        }
        
        
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
