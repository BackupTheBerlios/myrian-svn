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

import org.apache.log4j.Logger;

import com.arsdigita.db.DbHelper;

import java.util.Date;
import java.math.BigDecimal;

public class FetchTest extends PersistenceTestCase {
    private static Logger s_log = Logger.getLogger(FetchTest.class);
    private static String LONG_STRING;
    static {
          StringBuffer sb = new StringBuffer(500);
            for(int count = 0; count < 500; count++) {
                sb.append("a");
            }

           LONG_STRING = sb.toString();
    }

    public FetchTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
         load("com/arsdigita/persistence/testpdl/mdsql/Order.pdl");
         super.persistenceSetUp();
     }


    /**
     * This test reveals a problem with the current postgres implementation.
     * It will throw a PersistenceException at the second order.get("seller") call
     * when using Postgres, but not Oracle.
     *
     */
    public void testFetch() {
        DataObject order = getSession().create("mdsql.Order");
        order.set("id", new BigDecimal("15"));
        order.set("buyer", "Me");
        order.set("shippingAddress", "3 Lan Drive Westford, MA");
        order.set("seller", "Jon");
        order.set("shippingDate", new Date());
        order.set("hasShipped", Boolean.FALSE);
        order.save();

        OID oid = order.getOID();
        s_log.warn("Retrieving order first time");
        order = getSession().retrieve(oid);
        s_log.warn("Retrieved order");
        String originalSeller = (String) order.get("seller");
        assertEquals("Sellers differ!", "Jon", originalSeller);
        try {
            order.set("seller", LONG_STRING);
            order.save();

        } catch(PersistenceException e) {
            // Ignore, expected here.
        }
        s_log.warn("Retrieving order second time");
        order = getSession().retrieve(oid);
        s_log.warn("Retrieved order");
        // When using postgres, a persistence exception will be thrown.
        try {
            s_log.warn("About to retrieve seller for second time.");
            String afterFailedSeller = (String) order.get("seller");
            assertEquals("Sellers differ!", originalSeller,
                         afterFailedSeller);
        } catch (PersistenceException e) {
            if (DbHelper.getDatabase() != DbHelper.DB_POSTGRES) {
                throw e;
            }
        }
    }
}
