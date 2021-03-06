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

import org.myrian.persistence.FlushException;

import java.util.*;

/**
 * NestedObjectTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

public class NestedObjectTest extends WithTxnCase {


    private Collection query(Class klass) {
        return (Collection) m_pm.newQuery(klass).execute();
    }

    private Collection query(String oql) {
        return (Collection) m_pm.newQuery(Extensions.OQL, oql).execute();
    }

    private Collection query(String oql, Object o1) {
        return (Collection) m_pm.newQuery(Extensions.OQL, oql).execute(o1);
    }

    private Collection query(String oql, Object o1, Object o2) {
        return (Collection) m_pm.newQuery(Extensions.OQL, oql).execute(o1, o2);
    }

    private Collection query(String oql, Object o1, Object o2, Object o3) {
        return (Collection) m_pm.newQuery(Extensions.OQL, oql)
            .execute(o1, o2, o3);
    }

    private Object singleton(Class klass) {
        return query(klass).iterator().next();
    }

    private Object singleton(String oql) {
        return query(oql).iterator().next();
    }

    private Object singleton(String oql, Object o1) {
        return query(oql, o1).iterator().next();
    }

    private Object singleton(String oql, Object o1, Object o2) {
        return query(oql, o1, o2).iterator().next();
    }

    private Object singleton(String oql, Object o1, Object o2, Object o3) {
        return query(oql, o1, o2, o3).iterator().next();
    }

    private void assertSingleton(Object o, Collection c) {
        Iterator it = c.iterator();
        assertTrue(it.hasNext());
        assertEquals(o, it.next());
        assertFalse(it.hasNext());
    }

    private static final String L_STREET = "40 Tatooine Way";
    private static final String L_STATE = "NV";
    private static final String L_CITY = "Tatooine";
    private static final String L_ZIP = "12345";

    private Employee makeLuke() {
        Employee e = new Employee("Luke Skywalker", null);
        Address a = e.getAddress();
        a.setStreet(L_STREET);
        a.setCity(L_CITY);
        a.setState(L_STATE);
        a.setZip(L_ZIP);
        m_pm.makePersistent(e);
        return e;
    }

    private Collection lukes() {
        Collection emps = query(Employee.class);
        Collection lukes =
            query("filter($1, name == $2)", emps, "Luke Skywalker");
        return lukes;
    }

    private Employee luke() {
        return (Employee) lukes().iterator().next();
    }

    public void testIdentity() {
        Employee e = makeLuke();
        Address a = e.getAddress();

        Collection luke = lukes();

        assertSingleton(e, luke);

        assertEquals(a, e.getAddress());

        assertEquals(L_STREET, a.getStreet());
        assertEquals(L_CITY, a.getCity());
        assertEquals(L_STATE, a.getState());
        assertEquals(L_ZIP, a.getZip());

        Collection addr = query("$1.address", luke);

        assertSingleton(a, addr);
    }

    private static final String L_ZIP_UP = "44444";

    public void testUpdate() {
        makeLuke();
        commit();
        Employee e = luke();
        Address a = e.getAddress();

        assertEquals(L_STREET, a.getStreet());
        assertEquals(L_CITY, a.getCity());
        assertEquals(L_STATE, a.getState());
        assertEquals(L_ZIP, a.getZip());

        a.setZip(L_ZIP_UP);

        assertEquals(L_ZIP_UP, a.getZip());
        commit();

        e = luke();
        a = e.getAddress();

        assertEquals(L_STREET, a.getStreet());
        assertEquals(L_CITY, a.getCity());
        assertEquals(L_STATE, a.getState());
        assertEquals(L_ZIP_UP, a.getZip());
    }

    public void testViolation() {
        Contact c = new Contact("test");
        m_pm.makePersistent(c);
        try {
            commit();
            fail("succesfully flushed unassigned nested object");
        } catch (FlushException e) {
            // this is the error we want to see
        }
    }

    public void testCollection() {
        Rolodex r = new Rolodex();
        m_pm.makePersistent(r);

        Collection contacts = r.getContacts();

        Contact asdf = new Contact("asdf");
        asdf.setNumber("1-800-ASSDEAF");
        contacts.add(asdf);

        commit();

        r = (Rolodex) singleton(Rolodex.class);
        contacts = r.getContacts();

        assertEquals(1, contacts.size());
        //assertEquals(asdf, contacts.iterator().next());
        Contact c = (Contact) contacts.iterator().next();
        assertEquals("asdf", c.getName());
        assertEquals("1-800-ASSDEAF", c.getNumber());
    }

    public void testSessionKey() {
        Rolodex r = new Rolodex();
        m_pm.makePersistent(r);
        Collection contacts = r.getContacts();
        contacts.add(new Contact("one", "1"));
        contacts.add(new Contact("two", "2"));

        commit();

        contacts = query("$1.m_contacts", query(Rolodex.class));
        Contact one =
            (Contact) singleton("filter($1, m_name == $2)", contacts, "one");
        assertEquals("1", one.getNumber());
        Contact two =
            (Contact) singleton("filter($1, m_name == $2)", contacts, "two");
        assertEquals("1", one.getNumber());
        assertEquals("2", two.getNumber());
    }

    public void testDeletion() {
        Rolodex r = new Rolodex();
        m_pm.makePersistent(r);
        Collection contacts = r.getContacts();
        contacts.add(new Contact("asdf", "1-800-ASSDEAF"));
        contacts.add(new Contact("fdsa", "1-800-DEAFASS"));

        commit();

        contacts = r.getContacts();
        for (Iterator it = contacts.iterator(); it.hasNext(); ) {
            m_pm.deletePersistent(it.next());
        }

        Collection cs = query("$1.m_contacts", r);
        assertEquals("deleted nested objects", 2, cs.size());

        m_pm.deletePersistent(r);

        commit();

        Collection rs = query("$1", r);
        assertEquals("failed to delete", 0, rs.size());
    }

}
