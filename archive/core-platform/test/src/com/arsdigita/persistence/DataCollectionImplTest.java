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
import com.arsdigita.persistence.metadata.ObjectType;
import java.math.BigDecimal;
import junit.framework.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * DataCollectionImplTest
 *
 * This class tests DataCollectionImplTest, using the Node.pdl data definition.
 *
 *  This data must be loaded as a precondition of this test running.
 *
 * @author <a href="mailto:jorris@arsdigita.com">Jon Orris</a>
 * @version $Revision: #4 $ $Date: 2002/08/14 $
 */
public class DataCollectionImplTest extends DataCollectionTest
{
    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/DataCollectionImplTest.java#4 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";
    private static Logger s_log =
        Logger.getLogger(DataCollectionImplTest.class.getName());
    private ObjectType m_nodeType;

    public DataCollectionImplTest(String name)
    {
        super(name);
    }
    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/Node.pdl");
        load("com/arsdigita/persistence/testpdl/static/Order.pdl");
        super.persistenceSetUp();
    }

    protected void persistenceTearDown() {
        load("com/arsdigita/persistence/testpdl/static/Node.pdl");
        load("com/arsdigita/persistence/testpdl/static/Order.pdl");
        super.persistenceTearDown();
    }


    public void testGetDataObject()
    {
        DataCollection allItems = getDefaultCollection();
        int count = 0;
        while (allItems.next())
            {
                DataObject obj = allItems.getDataObject();
                assertEquals( "Somehow failed to retrieve a Node?", "Node", obj.getObjectType().getName());
                count++;
            }
        assertTrue( "No data objects?", count > 0);
    }

    public void testGetObjectType()
    {

        DataCollection allItems = getDefaultCollection();
        int count = 0;
        while (allItems.next())
            {
                ObjectType type = allItems.getObjectType();
                s_log.info("Type: " + type.getQualifiedName());
                assertEquals( "Somehow failed to retrieve a Node?", "Node", type.getName());
                count++;
            }

        assertTrue( "No data objects?", count > 0);
    }

    /**
     *  Tests the ordering capability of DataCollection.
     *  Checks forward, reverse, and multiple field ordering.
     *
     */
    public void testSetOrder() {
        DataCollection collection = getDefaultCollection();
        final String ORDER_FIELD = "id";
        collection.setOrder(ORDER_FIELD);
        assert( "Should be several items in this query set!", collection.next() );
        BigDecimal priorValue = (BigDecimal) collection.get(ORDER_FIELD);

        while ( collection.next() ) {
            final BigDecimal currentValue = (BigDecimal) collection.get(ORDER_FIELD);
            assert("Query was retrieved out of order.",
                   priorValue.compareTo( currentValue ) < 0 );

            priorValue = currentValue;

        }
        collection.close();

        collection = getDefaultCollection();
        collection.setOrder("id desc");
        assert( "Should be several items in this query set!", collection.next() );
        priorValue = (BigDecimal) collection.get(ORDER_FIELD);

        while ( collection.next() ) {
            final BigDecimal currentValue = (BigDecimal) collection.get(ORDER_FIELD);
            assert("Query was retrieved out of order.",
                   priorValue.compareTo( currentValue ) > 0 );

            priorValue = currentValue;

        }

        collection.close();

        collection = getOrderCollection();
        collection.setOrder("buyer desc, seller asc");
        assert("Should be several items in this query set!",
               collection.next() );

        String priorBuyer = (String) collection.get("buyer");
        String priorSeller = (String) collection.get("seller");

        while (collection.next()) {
            final String currentBuyer =
                (String) collection.get("buyer");
            final String currentSeller = (String) collection.get("seller");
            assert("Buyer order wrong!",
                   priorBuyer.compareTo( currentBuyer ) >= 0 );
            if( priorBuyer.equals(currentBuyer) ) {
                assert("Seller order wrong! " + priorSeller + " " + currentSeller,
                       priorSeller.compareTo( currentSeller) <= 0);

            }

            priorBuyer = currentBuyer;
            priorSeller = currentSeller;

        }



    }

    /**
     *  Tests the ordering capability of DataQuery.
     *  Checks forward, reverse, and multiple field ordering.
     *
     */
    public  void testAddOrder() {

    }

    /**
     *  This tests the ability to add multiple filters to a data query
     */
    public  void testAddFilter() {

    }

    protected void setUp()
    {
        DataObject parent = getSession().create("examples.Node");
        m_nodeType = parent.getObjectType();
        parent.set("id", new BigDecimal(0));
        parent.set("name", "Root");
        parent.save();

        for(int i = 1; i < 10; i++)
            {
                DataObject child = getSession().create("examples.Node");
                child.set("id", new BigDecimal(i));
                child.set("name", "child" + i);
                child.set("parent", parent);
                child.save();
                parent.save();

                parent = child;
            }

    }

    protected DataQuery getDefaultQuery() {
        return getDefaultCollection();
    }

    protected DataCollection getDefaultCollection()
    {
        return getSession().retrieve("examples.Node");
    }

    protected ObjectType getDefaultObjectType() {
        return m_nodeType;
    }

    private DataCollection getOrderCollection() {
        int id = 1;
        Calendar now = Calendar.getInstance();
        int currentDay = now.get(Calendar.DAY_OF_YEAR);

        DataObject order = getSession().create("examples.Order");
        order.set("id", new BigDecimal(id++));
        order.set("buyer", "Rafael H. Schloming");
        order.set("shippingAddress",
                  "102 R Inman St.\nCambridge MA, 02139");
        order.set("seller", "ArsDigita");
        order.set("shippingDate", now.getTime());
        order.set("hasShipped", Boolean.FALSE);
        order.save();

        now.set(Calendar.DAY_OF_YEAR, ++currentDay);

        order = getSession().create("examples.Order");
        order.set("id", new BigDecimal(id++));
        order.set("buyer", "Jon Orris");
        order.set("shippingAddress",
                  "80 Prospect St");
        order.set("shippingDate", now.getTime());
        order.set("hasShipped", Boolean.TRUE);
        order.set("seller", "ArsDigita");
        order.save();
        now.set(Calendar.DAY_OF_YEAR, ++currentDay);

        order = getSession().create("examples.Order");
        order.set("id", new BigDecimal(id++));
        order.set("buyer", "Foo Bar");
        order.set("shippingAddress",
                  "Nowhere, MA");
        order.set("shippingDate", now.getTime());
        order.set("seller", "ArsDigita");
        order.set("hasShipped", Boolean.TRUE);
        order.save();
        now.set(Calendar.DAY_OF_YEAR, ++currentDay);

        order = getSession().create("examples.Order");
        order.set("id", new BigDecimal(id++));
        order.set("buyer", "Micro Soft");
        order.set("shippingAddress",
                  "Redmond, WA");
        order.set("shippingDate", now.getTime());
        order.set("seller", "ArsDigita");
        order.set("hasShipped", Boolean.TRUE);
        order.save();
        now.set(Calendar.DAY_OF_YEAR, ++currentDay);

        order = getSession().create("examples.Order");
        order.set("id", new BigDecimal(id++));
        order.set("buyer", "Micro Soft");
        order.set("shippingAddress",
                  "Redmond, WA");
        order.set("shippingDate", now.getTime());
        order.set("seller", "FooBar consulting");
        order.set("hasShipped", Boolean.TRUE);
        order.save();
        now.set(Calendar.DAY_OF_YEAR, ++currentDay);

        order = getSession().create("examples.Order");
        order.set("id", new BigDecimal(id++));
        order.set("buyer", "Foo Bar");
        order.set("shippingAddress",
                  "Nowhere, MA");
        order.set("shippingDate", now.getTime());
        order.set("seller", "Smurf!");
        order.set("hasShipped", Boolean.TRUE);
        order.save();

        return getSession().retrieve("examples.Order");

    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(new TestSuite(DataCollectionImplTest.class));
    }
}
