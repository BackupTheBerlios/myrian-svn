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
import com.arsdigita.persistence.metadata.ObjectType;
import java.math.BigDecimal;
import junit.framework.*;
import java.util.*;

/*  This is a utiliy class for creating an Order and a DataAssociation set of
 *  LineItems. Used internally by testing classes.
 *
 */
final class OrderAssociation {

    public static final String versionId = "$Id: //core-platform/test-qgen/test/src/com/arsdigita/persistence/OrderAssociation.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";
    public static final int NUM_ITEMS = 10;

    OrderAssociation(Session session) {
        BigDecimal id = new BigDecimal(1);

        DataObject order = session.create("examples.Order");
        order.set("id", id);
        order.set("buyer", "Michael Bryzek");
        order.set("shippingAddress",
                  "2036 Shattuck Ave.\nBerkeley, CA 94704");
        order.set("shippingDate",
                  new java.sql.Date(System.currentTimeMillis()));
        order.set("hasShipped", Boolean.FALSE);
        order.save();

        m_order = (DataObject) session.retrieve
            (new OID("examples.Order", id));

        DataAssociation items = (DataAssociation) m_order.get("items");
        for (int i = 0; i < NUM_ITEMS; i++) {
            DataObject li = session.create("examples.LineItem");
            li.set("id", new BigDecimal(i));
            li.set("name", "Item " + i);
            li.set("price", new Float(2.99 + i));
            items.add(li);
            m_lineItemType = li.getObjectType();
        }
        m_order.save();

        m_lineItems = (DataAssociation) m_order.get("items");

    }

    /* Cleans out the database. Must be called by TestCase.tearDown().
     *
     */
    void tearDown() {
        m_order.delete();
        m_order = null;
        m_lineItems = null;
    }


    DataObject getOrder() {
        return m_order;
    }

    ObjectType getLineItemType() {
        return m_lineItemType;
    }

    DataAssociation getLineItems() {
        return m_lineItems;
    }

    private DataObject m_order;
    private DataAssociation m_lineItems;
    private ObjectType m_lineItemType;

}
