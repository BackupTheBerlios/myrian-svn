/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence.jdo;

import org.myrian.persistence.engine.rdbms.ConnectionSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;

/**
 * Tests lifecycle of JDO instances across transactions.
 *
 * @since 2004-08-04
 * @version $Id: //eng/persistence/dev/test/src/org/myrian/persistence/jdo/BiTxnTest.java#1 $
 **/
public class BiTxnTest extends AbstractCase {
    private final static Logger s_log = Logger.getLogger(BiTxnTest.class);

    // XXX: This is a quick and dirty hack.  This class contains tests that
    // span more than one transaction.  This means that we can't bail out of db
    // changes by merely performing a rollback.  Therefore, we attempt to clean
    // up after ourselves by deleting all rows from all tables in the order
    // that respects foreign key constraints.  The list of table and their
    // ordering is currently hardcoded and need to be kept in sync with the
    // data model by hand.
    private final static String[] TABLES =
    {"items", "orders", "products", "pictures", "groups_users", "groups",
     "users", "partys_auxiliaryemails", "partys", "employees", "departments",
     "magazines_index", "magazines"};

    public BiTxnTest() {}

    public BiTxnTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        m_pm.currentTransaction().begin();
    }

    protected void tearDown() throws Exception {
        m_pm.currentTransaction().rollback();

        PersistenceManagerImpl pm = (PersistenceManagerImpl) m_pm;
        ConnectionSource cs =
            ((PersistenceManagerFactoryImpl) pm.getPersistenceManagerFactory())
            .getConnectionSource();
        Connection conn = cs.acquire();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            for (int ii=0; ii<TABLES.length; ii++) {
                stmt.execute("delete from " + TABLES[ii]);
            }
            conn.commit();
        } finally {
            try {
                if (stmt != null) { stmt.close(); }
            } finally {
                cs.release(conn);
            }
        }
    }

    private void checkpoint() {
        m_pm.currentTransaction().commit();
        m_pm.currentTransaction().begin();
    }

    public void testNontransAfterCommit() {
        Group group = new Group(0);
        final String email = "java-project@redhat.com";
        group.setEmail(email);
        group.setName("Java Hackers");
        m_pm.makePersistent(group);

        assertTrue("is transactional", JDOHelper.isTransactional(group));
        m_pm.currentTransaction().commit();
        assertTrue("is nontransactional", !JDOHelper.isTransactional(group));
        assertEquals("email", email, group.getEmail());
    }
}
