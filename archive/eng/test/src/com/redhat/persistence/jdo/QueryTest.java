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
package com.redhat.persistence.jdo;

import java.util.*;
import javax.jdo.*;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

public class QueryTest extends AbstractCase {

    private static final Logger s_log = Logger.getLogger(QueryTest.class);

    private static PersistenceManager s_pm = null;

    // XXX hack that depends on pmfi details
    private static PersistenceManager pm() {
        if (s_pm == null) {
            AbstractCase ac = new AbstractCase() {};
            try {
                ac.setUpPersistenceManager();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            s_pm = ac.m_pm;
        }

        return s_pm;
    }

    static Test suite() {
        TestSuite ts = new TestSuite();
        ts.addTestSuite(QueryTest.class);
        return new TestSetup(ts) {
            protected void setUp() throws Exception {
                boolean done = false;
                s_log.info("begin loading test data");
                try {
                    pm().currentTransaction().begin();
                    loadTestData();
                    done = true;
                    s_log.info("end loading test data");
                } finally {
                    if (!done) {
                        s_log.error("could not load test data");
                        pm().currentTransaction().rollback();
                    }
                }
            }

            protected void tearDown() throws Exception {
                s_log.info("rollback");
                pm().currentTransaction().rollback();
            }
        };
    }

    private static void loadTestData() {
        Department[] d = new Department[]
            { new Department("even"), new Department("odd") };

        for (int i = 0; i < d.length; i++) {
            pm().makePersistent(d[i]);
        }

        for (int i = 0; i < 3; i++) {
            Employee e = new Employee(String.valueOf(i), d[i % 2]);
            if (i == 0) {
                e.setSalary(null);
            } else {
                e.setSalary(new Float(i));
            }
            pm().makePersistent(e);
        }
    }

    public QueryTest() {}

    public QueryTest(String name) {
        super(name);
    }

    public void testFilter() {
        Collection c = (Collection)
            pm().newQuery(Employee.class, "this.name == \"0\"").execute();
        Iterator it = c.iterator();
        if (it.hasNext()) {
            assertEquals("0", ((Employee) it.next()).getName());
            if (it.hasNext()) { fail("too many rows returned"); }
        } else {
            fail("no rows returned");
        }
    }

    public void testOrder() {
        Iterator it;
        Collection c;
        final Query q = pm().newQuery
            (Employee.class,
             "this.dept.name == \"odd\" || this.dept.name == \"even\"");

        c = (Collection) q.execute();
        assertEquals(3, c.size());

        // evens then odds: 2, 0, 1
        q.setOrdering("this.dept.name ascending, this.name descending");
        c = (Collection) q.execute();
        it = c.iterator();
        assertEquals("2", ((Employee) it.next()).getName());
        assertEquals("0", ((Employee) it.next()).getName());
        assertEquals("1", ((Employee) it.next()).getName());

        // odds then evens: 1, 0, 2
        q.setOrdering("this.dept.name descending, this.name ascending");
        c = (Collection) q.execute();
        it = c.iterator();
        assertEquals("1", ((Employee) it.next()).getName());
        assertEquals("0", ((Employee) it.next()).getName());
        assertEquals("2", ((Employee) it.next()).getName());
    }

    public void testCandidates() {
        Iterator it;
        Collection c;
        Query q;

        q = pm().newQuery(Employee.class, "this.dept.name == \"even\"");
        c = (Collection) q.execute();

        q = pm().newQuery(Employee.class);
        q.setCandidates(c);
        q.setFilter("this.salary != null");

        c = (Collection) q.execute();
        assertEquals(1, c.size());
        it = c.iterator();
        assertTrue(((Employee) it.next()).getSalary() != null);
    }

    public void testCandidatesOQL() {
        Iterator it;
        Collection c;
        Query q;

        q = pm().newQuery
            (Extensions.OQL, "all(com.redhat.persistence.jdo.Employee)");
        q.setFilter("this.dept.name == \"even\"");
        c = (Collection) q.execute();

        q = pm().newQuery(Employee.class);
        q.setCandidates(c);
        q.setFilter("this.salary != null");

        c = (Collection) q.execute();
        assertEquals(1, c.size());
        it = c.iterator();
        assertTrue(((Employee) it.next()).getSalary() != null);
    }

    public void testCandidatesLimit() {
        Collection c;
        Query q;

        q = pm().newQuery
            (Extensions.OQL,
             "limit(all(com.redhat.persistence.jdo.Employee), 1)");
        c = (Collection) q.execute();
        assertEquals(1, c.size());
    }

    public void testClose() {
        Collection c[] = new Collection[2];
        Query q;
        Iterator it[][] = new Iterator[c.length][2];

        q = pm().newQuery(Employee.class);

        for (int i = 0; i < c.length; i++) {
            c[i] = (Collection) q.execute();
        }

        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < it[i].length; j++) {
                it[i][j] = c[i].iterator();
                it[i][j].next();
            }
        }

        q.close(c[0]);

        for (int j = 0; j < it[0].length; j++) {
            assertFalse(it[0][j].hasNext());
            try {
                it[0][j].next();
            } catch (NoSuchElementException nsee) {
                continue;
            }
            fail("next succeeded after close");
        }

        for (int j = 0; j < it[1].length; j++) {
            assertTrue(it[1][j].hasNext());
            it[1][j].next();
        }

        q.closeAll();

        for (int j = 0; j < it[0].length; j++) {
            try {
                it[0][j] = c[0].iterator();
            } catch (JDOUserException jue) {
                continue;
            }

            fail("query result was closed");
        }

        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < it[i].length; j++) {
                assertFalse(it[i][j].hasNext());
                try {
                    it[i][j].next();
                } catch (NoSuchElementException nsee) {
                    continue;
                }
                fail("next succeeded after close");
            }
        }
    }
}
