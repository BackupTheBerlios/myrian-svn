package com.redhat.persistence.jdo;

public class SimpleTest extends WithTxnCase {
    public void testModification() {
        Employee e = new Employee("name", null);
        e.setSalary(new Float(1.0f));
        m_pm.makePersistent(e);
        e.setSalary(new Float(e.getSalary().floatValue() + 2.0f));
        Object eId = m_pm.getObjectId(e);
        m_pm.currentTransaction().commit();

        e = null;
        m_pm.currentTransaction().begin();
        e = (Employee) m_pm.getObjectById(eId, true);
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
}
