package com.redhat.persistence.jdo;

import com.redhat.persistence.FlushException;

import java.util.*;

/**
 * NestedObjectTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/08/06 $
 **/

public class NestedObjectTest extends WithTxnCase {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdo/NestedObjectTest.java#1 $ by $Author: rhs $, $DateTime: 2004/08/06 08:43:09 $";

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
        Collection emps = (Collection) m_pm.newQuery(Employee.class).execute();
        Collection lukes = (Collection) m_pm.newQuery
            ("oql", "filter($1, name == $2)").execute(emps, "Luke Skywalker");
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

        Collection addr =
            (Collection) m_pm.newQuery("oql", "$1.address").execute(luke);

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
        contacts.add(new Contact("asdf"));
    }

}
