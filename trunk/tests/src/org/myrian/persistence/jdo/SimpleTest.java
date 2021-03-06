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

import java.util.*;
import javax.jdo.*;
import org.apache.log4j.Logger;

public class SimpleTest extends WithTxnCase {
    private static final Logger s_log = Logger.getLogger(SimpleTest.class);

    public void testModification() {
        Employee e = new Employee("name", null);
        e.setSalary(new Float(1.0f));
        m_pm.makePersistent(e);
        e.setSalary(new Float(e.getSalary().floatValue() + 2.0f));
        m_pm.currentTransaction().commit();

        m_pm.currentTransaction().begin();
        assertTrue("set after makePersistent", e.getSalary().floatValue() > 2);
        m_pm.currentTransaction().commit();
    }

    public void testRef() {
        String eName = "seb";
        String dName = "ASR";

        Employee e = new Employee(eName, new Department(dName));
        m_pm.makePersistent(e);
        Object eId = m_pm.getObjectId(e);
        assertNotNull("null object identifier", eId);
        m_pm.currentTransaction().commit();

        e = null;
        m_pm.currentTransaction().begin();
        e = (Employee) m_pm.getObjectById(eId, true);
        assertNotNull("null instance returned by getObjectById", e);
        assertTrue("Bad employee name", eName.equals(e.getName()));
        assertNotNull("null instance returned by getDept", e.getDept());
        assertTrue("Bad department name", dName.equals(e.getDept().getName()));
        m_pm.deletePersistent(e.getDept());
        m_pm.deletePersistent(e);
        m_pm.currentTransaction().commit();
    }

    public void testNullRef() {
        String eName = "nullRefEmp";
        Employee e = new Employee(eName, null);
        m_pm.makePersistent(e);
        Object eId = m_pm.getObjectId(e);
        assertNotNull("null object identifier", eId);
        m_pm.currentTransaction().commit();

        e = null;
        m_pm.currentTransaction().begin();
        e = (Employee) m_pm.getObjectById(eId, true);
        assertNotNull("null instance returned by getObjectById", e);
        assertEquals("Bad employee name", eName, e.getName());
        assertNull("not null instance returned by e.dept", e.getDept());
        m_pm.deletePersistent(e);
        m_pm.currentTransaction().commit();
    }

    /**
     * See 12.6.6 JDO Instance life cycle management.
     **/
    public void testMakePersistentAll() {
        Object obj1 = new Employee("employee", null);
        Object obj2 = null;
        Object obj3 = new Object();
        Object obj4 = new Employee("supervisor", null);
        Object[] pcs = new Object[] {obj1, obj2, obj3, obj4};
        try {
            m_pm.makePersistentAll(pcs);
            fail("JDOUserException expected");
        } catch (JDOUserException ex) {
            Throwable[] nested = ex.getNestedExceptions();
            assertNotNull("nested exceptions", nested);
            assertEquals("nested exceptions", 2, nested.length);
            assertTrue("obj1.isNew", JDOHelper.isNew(obj1));
            assertTrue("obj4.isNew", JDOHelper.isNew(obj4));
        }
    }

    public void testOQLQuery() {
        Employee e = new Employee("oql", null);
        m_pm.makePersistent(e);

        Query q = m_pm.newQuery
            (Extensions.OQL, "all(org.myrian.persistence.jdo.Employee)");
        Object emps = q.execute();

        Query q2 = m_pm.newQuery
            (Extensions.OQL, "filter(emps, dept == null)");
        q2.setFilter("this.name == \"oql\"");
        Map values = new HashMap();
        values.put("emps", emps);
        Collection c2 = (Collection) q2.executeWithMap(values);

        Iterator it = c2.iterator();
        assertTrue(it.hasNext());
        assertEquals(e, it.next());
        assertFalse(it.hasNext());
    }

    public void testNullResurrection() {
        String name = "Departmentless";
        Employee e = new Employee(name, null);
        m_pm.makePersistent(e);
        commit();

        Collection emps = (Collection) m_pm.newQuery(Employee.class).execute();
        Query q = m_pm.newQuery(Extensions.OQL, "filter($1, name == $2)");
        Extensions.addPath(q, "dept");
        Collection emp = (Collection) q.execute(emps, name);
        e = (Employee) emp.iterator().next();
        assertNull(e.getDept());
    }

    public void testIsPersistent() {
        String name = "dummy";
        Employee e = new Employee(name, null);
        m_pm.makePersistent(e);
        m_pm.currentTransaction().commit();

        m_pm.currentTransaction().begin();
        assertTrue(JDOHelper.isPersistent(e));
        m_pm.currentTransaction().commit();

        PersistenceManager pm2 =
            m_pm.getPersistenceManagerFactory().getPersistenceManager();
        pm2.currentTransaction().begin();
        pm2.deletePersistent(pm2.getObjectById(m_pm.getObjectId(e), true));
        pm2.currentTransaction().commit();

        m_pm.currentTransaction().begin();
        assertFalse(JDOHelper.isPersistent(e));
        m_pm.currentTransaction().commit();
    }

    public void testAssociateUnknown() {
        String eName = "pk";
        String dName = "ER";

        Employee e = new Employee(eName, null);
        Department d = new Department(dName);
        m_pm.makePersistent(e);
        m_pm.makePersistent(d);
        m_pm.currentTransaction().commit();

        m_pm.currentTransaction().begin();
        e.setDepartment(d);
        m_pm.currentTransaction().commit();

        m_pm.currentTransaction().begin();
        assertTrue("Bad employee name", eName.equals(e.getName()));
        assertNotNull("null instance returned by getDept", e.getDept());
        assertTrue("Bad department name", dName.equals(e.getDept().getName()));
        m_pm.deletePersistent(e.getDept());
        m_pm.deletePersistent(e);
        m_pm.currentTransaction().commit();
    }

}
