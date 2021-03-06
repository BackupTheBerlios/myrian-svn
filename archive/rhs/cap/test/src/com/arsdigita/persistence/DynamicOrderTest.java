/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.persistence;

/**
 * PartyTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2004/05/03 $
 */

public class DynamicOrderTest extends OrderTest {

    public final static String versionId = "$Id: //users/rhs/persistence/cap/test/src/com/arsdigita/persistence/DynamicOrderTest.java#1 $ by $Author: rhs $, $DateTime: 2004/05/03 11:48:01 $";

    public DynamicOrderTest(String name) {
        super(name);
        m_testType = "dynamic";
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/mdsql/Order.pdl");
        super.persistenceSetUp();
    }

    String getModelName() {
        return "mdsql";
    }

    public void testAddPathThroughCollection() {
        DataObject order = makeOrder(3);
        DataCollection dc = getSession().retrieve(getModelName() + ".Order");
        dc.addPath("items.name");
        dc.addFilter("id = items.id");
        dc.next();
        assertEquals(dc.get("id"), dc.get("items.id"));
    }
}
