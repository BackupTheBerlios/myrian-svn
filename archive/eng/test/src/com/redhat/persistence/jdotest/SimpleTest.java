package com.redhat.persistence.jdotest;

import com.arsdigita.util.jdbc.Connections;
import com.redhat.persistence.jdo.PersistenceManagerFactoryImpl;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.pdl.PDL;
import com.redhat.persistence.pdl.Schema;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.math.BigInteger;
import javax.jdo.*;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SimpleTest extends AbstractCase {
    public void test1() {
        String eName = "seb";
        String dName = "ASR";

        Employee e = new Employee(id(), eName, new Department(id(), dName));
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
        Employee e = new Employee(id(), eName, null);
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
