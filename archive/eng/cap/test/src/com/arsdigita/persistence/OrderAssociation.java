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

/*  This is a utiliy class for creating an Order and a DataAssociation set of
 *  LineItems. Used internally by testing classes.
 *
 */
final class OrderAssociation {

    public static final String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/OrderAssociation.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";
    public static final int NUM_ITEMS = 10;

    private final String m_model;

    OrderAssociation(Session session) {
        this(session, "examples");
    }

    OrderAssociation(Session session, String model) {
        m_model = model + ".";
        BigDecimal id = new BigDecimal(1);

        DataObject order = session.create(m_model + "Order");
        order.set("id", id);
        order.set("buyer", "Michael Bryzek");
        order.set("shippingAddress",
                  "2036 Shattuck Ave.\nBerkeley, CA 94704");
        order.set("shippingDate",
                  new java.sql.Date(System.currentTimeMillis()));
        order.set("hasShipped", Boolean.FALSE);
        order.save();

        m_order = (DataObject) session.retrieve
            (new OID(m_model + "Order", id));

        DataAssociation items = (DataAssociation) m_order.get("items");
        for (int i = 0; i < NUM_ITEMS; i++) {
            DataObject li = session.create(m_model + "LineItem");
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
