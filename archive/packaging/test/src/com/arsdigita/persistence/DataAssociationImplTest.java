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

import java.math.BigDecimal;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * DataAssociationImplTest <p> This class performs unit tests on
 * com.arsdigita.persistence.DataAssociationImpl </p>
 *
 * <p> Explanation about the tested class and its responsibilities </p>
 *
 *     DataAssociationImpl implements
 *     com.arsdigita.persistence.DataAssociation </p>
 *
 * @author Michael Bryzek
 * @date $Date: 2003/08/19 $
 * @version $Revision: #2 $
 *
 * @see com.arsdigita.persistence.DataAssociationImpl
 **/

public class DataAssociationImplTest extends PersistenceTestCase {

    public static final String versionId = "$Id: //core-platform/test-packaging/test/src/com/arsdigita/persistence/DataAssociationImplTest.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";
    private static Logger log =
        Logger.getLogger(DataAssociationImplTest.class.getName());

    private OrderAssociation m_orderAssoc;

    /**
     * Constructor (needed for JTest)
     * @param name    Name of Object
     **/
    public DataAssociationImplTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/Order.pdl");
        super.persistenceSetUp();
    }

    protected void persistenceTearDown() {
        load("com/arsdigita/persistence/testpdl/static/Order.pdl");
        super.persistenceTearDown();
    }


    /**
     * Used by JUnit (called before each test method)
     **/
    protected void setUp() {

        m_orderAssoc = new OrderAssociation(getSession());
    }

    /**
     * Used by JUnit (called after each test method)
     **/
    protected void tearDown() {
        m_orderAssoc.tearDown();
    }


    public void testAdd() {
        DataObject li = getSession().create("examples.LineItem");
        BigDecimal newId = new BigDecimal(OrderAssociation.NUM_ITEMS);
        li.set("id", newId);
        li.set("name", "Item grick");
        li.set("price", new Float(2.99));

        m_orderAssoc.getLineItems().add(li);
        m_orderAssoc.getOrder().save();

        assertTrue("Item should be added after parent saved!",
                   findItemById(m_orderAssoc.getLineItems().cursor(), newId));

    }


    public void testClear() {
        // not implemented
    }

    public void testCursor() {
        DataAssociationCursor c = m_orderAssoc.getLineItems().cursor();
        while( c.next() ) {
            log.info("id: " + c.get("id"));
        }
        assertEquals("Cursor contains insufficient items!!!",
		     OrderAssociation.NUM_ITEMS, c.size());

    }

    /**
     * Test the getDataCollection() method.
     **/

    public void testGetDataCollection() {
        DataAssociation items =
	    (DataAssociation) m_orderAssoc.getOrder().get("items");
        DataCollection dc1 = items.getDataCollection();
        DataCollection dc2 = items.getDataCollection();

        dc1.addOrder("name");
        dc2.addOrder("name");

        assertEquals("incorrect size", OrderAssociation.NUM_ITEMS, dc1.size());
        assertEquals("incorrect size", OrderAssociation.NUM_ITEMS, dc2.size());

        int i = 0;
        while (dc1.next()) {
            assertEquals("incorrect name", "Item " + i++, dc1.get("name"));
        }

        assertEquals("incorrect number of iterations",
		     OrderAssociation.NUM_ITEMS, i);

        i = 0;
        while (dc2.next()) {
            assertEquals("incorrect name", "Item " + i++, dc2.get("name"));
        }

        assertEquals("incorrect number of iterations",
		     OrderAssociation.NUM_ITEMS, i);
    }

    public void FAILStestIsModifiedInMemory() {
        BigDecimal newId = new BigDecimal(OrderAssociation.NUM_ITEMS);
        DataObject li = getSession().create("examples.LineItem");
        li.set("id", newId);

        m_orderAssoc.getLineItems().add(li);
        m_orderAssoc.getLineItems().remove(li);

        assertTrue("assocation modified",
                   !m_orderAssoc.getLineItems().isModified());
    }

    public void testIsModified() {
        BigDecimal newId = new BigDecimal(OrderAssociation.NUM_ITEMS);
        DataObject li = getSession().create("examples.LineItem");

        li.set("id", newId);
        li.set("price", new Float(1.00));
        li.set("name", "newli");
        m_orderAssoc.getLineItems().add(li);

        assertTrue("assocation not modified after add!!",
		   m_orderAssoc.getLineItems().isModified());

        DataAssociationCursor c = m_orderAssoc.getLineItems().cursor();
        c.next();
        li = c.getDataObject();
        m_orderAssoc.getLineItems().remove(li);
        assertTrue("assocation not modified after remove!!",
                   m_orderAssoc.getLineItems().isModified());

        c.close();
    }

    public void testRemove() {
        DataAssociationCursor c = m_orderAssoc.getLineItems().cursor();
        c.next();
        DataObject li = c.getDataObject();
        BigDecimal id = (BigDecimal) li.get("id");
        m_orderAssoc.getLineItems().remove(li);
        assertTrue("assocation not modified after remove!!",
		   m_orderAssoc.getLineItems().isModified());
        m_orderAssoc.getOrder().save();
        assertTrue("Item should be removed from cursor after parent saved!",
                   !findItemById(m_orderAssoc.getLineItems().cursor(), id));
        c.close();
    }

    public void testRemoveWithOID() {
        DataAssociationCursor c = m_orderAssoc.getLineItems().cursor();
        c.next();
        DataObject li = c.getDataObject();
        BigDecimal id = (BigDecimal) li.get("id");
        m_orderAssoc.getLineItems().remove(li.getOID());
        assertTrue("assocation not modified after remove!!",
		   m_orderAssoc.getLineItems().isModified());
        m_orderAssoc.getOrder().save();
        c.close();
        c = m_orderAssoc.getLineItems().cursor();
        assertTrue("Item should be removed from cursor after parent saved!",
                   !findItemById(m_orderAssoc.getLineItems().cursor(), id));
        c.close();
    }


    /**
     * Test case to ensure you can get independent data associations
     * by calling get.
     *
     * http://developer.arsdigita.com/acs5/sdm/one-ticket?ticket_id=138526
     **/
    public void testConsecutiveCalls() {
        // TODO: Re-enable this test once we figure out how to handle
        // having data association iterators


        // We move the cursor in lineItems two rows. This makes it
        // easy to test that the call to get returns an independent
        // DataAssociation.
        DataAssociationCursor firstCursor =
	    m_orderAssoc.getLineItems().cursor();
        firstCursor.next();
        firstCursor.next();
        assertTrue("m_orderAssoc.getLineItems() is still at first element " +
		   "after calling next",
		   ! (firstCursor.isFirst() || firstCursor.isBeforeFirst()));
        DataAssociation items =
	    (DataAssociation) m_orderAssoc.getOrder().get("items");
        DataAssociationCursor secondCursor = items.cursor();
        assertEquals("Consecutive calls to get returned data associations " +
                     "of different sizes",
                     firstCursor.size(), secondCursor.size());
        assertTrue("Items is at position: " + secondCursor.getPosition() +
               ". It should be before the first position",
               secondCursor.isBeforeFirst());

        firstCursor.close();
        secondCursor.close();
    }

    DataAssociation getLineItems() {
        return (DataAssociation) m_orderAssoc.getOrder().get("items");
    }


    /**
     *  This tests the situation where the developer gets
     *  the association on object 1, sets it and saves it one object 2
     *  and then gets it again on object 1
     */
    public void testMultipleAssociations() throws Exception {
        DataObject order = getSession().create("examples.Order");
        order.set("id", new BigDecimal(OrderAssociation.NUM_ITEMS+1));
        order.set("buyer", "Michael Bryzek");
        order.set("shippingAddress",
                  "2036 Shattuck Ave.\nBerkeley, CA 94704");
        order.set("shippingDate",
                  new java.sql.Date(System.currentTimeMillis()));
        order.set("hasShipped", Boolean.FALSE);

        DataObject other = getSession().create("examples.OtherItem");
        other.set("id", new BigDecimal(OrderAssociation.NUM_ITEMS+2));
        other.set("name", "Item ");
        other.set("price", new Float(2.99));
        other.set("inStock", new Boolean(true));
        other.save();

        DataAssociation items =
	    (DataAssociation) order.get("relatedOtherItems");
        assertNull("Other Item not yet association with the order " +
                   "so this should return null", other.get("order"));

        items.add(other);
        order.save();
        assertNotNull("Other Item has been associated with the order so " +
                      "it should return not null", other.get("order"));

        DataObject order2 = getSession().create("examples.Order");
        order2.set("id", new BigDecimal(OrderAssociation.NUM_ITEMS+3));
        order2.set("buyer", "theBuyer");
        order2.set("shippingAddress",
                   "theAddress");
        order2.set("shippingDate",
                   new java.sql.Date(System.currentTimeMillis()));
        order2.set("hasShipped", Boolean.FALSE);
        order2.save();

        items.remove(other);
        order.save();
        assertNull("Other Item was just removed so this should return null",
                   other.get("order"));

        // let's put a value back into other.get("order"), get it, and
        // swap it
        items.add(other);
        order.save();
        DataObject orderObject = (DataObject)other.get("order");
        assertNotNull(orderObject);
        assertTrue("The retrieved object should be the first order",
               order.equals(orderObject));
        items.remove(other);
        order.save();
        DataAssociation items2 =
	    (DataAssociation) order2.get("relatedOtherItems");
        items2.add(other);
        order2.save();
        assertTrue("the order changed but the value held by 'order' did not",
               !orderObject.equals(other.get("order")));
    }

    private boolean findItemById(DataAssociationCursor c, BigDecimal id) {
        while(c.next()) {
            if( c.get("id").equals(id) ) {
                c.close();
                return true;
            }
        }
        return false;
    }

    /**
     * Main method needed to make a self runnable class
     *
     * @param args This is required for main method
     **/
    public static void main(String[] args) {
        junit.textui.TestRunner.run
	    (new TestSuite(DataAssociationImplTest.class));
    }

}
