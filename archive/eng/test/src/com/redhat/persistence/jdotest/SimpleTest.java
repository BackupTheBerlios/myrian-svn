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
import junit.framework.*;

public class SimpleTest extends TestCase {

    private static int s_id = 0;
    private static PersistenceManagerFactory s_pmf = null;
    private static PersistenceManager m_pm = null;

    public SimpleTest(String s) {
        super(s);
    }

    public static Test suite() {
        Test t = new TestSuite(SimpleTest.class);
        Test wrapper = new TestSetup(t) {
            protected void setUp() throws Exception {
                SimpleTest.setUpPMF();
                PersistenceManagerFactoryImpl pmf =
                    (PersistenceManagerFactoryImpl) s_pmf;
                Root root = pmf.getMetadataRoot();
                Connection conn = Connections.acquire
                    (s_pmf.getConnectionURL());
                Schema.load(root, conn);
                conn.commit();
            }

            protected void tearDown() throws Exception {
                PersistenceManagerFactoryImpl pmf =
                    (PersistenceManagerFactoryImpl) s_pmf;
                Root root = pmf.getMetadataRoot();
                Connection conn = Connections.acquire
                    (s_pmf.getConnectionURL());
                Schema.unload(root, conn);
                conn.commit();
            }
        };

        return wrapper;
    }

    private static void setUpPMF() throws IOException {
        ClassLoader cl = SimpleTest.class.getClassLoader();
        String props = "jdo.properties";
        Properties p = new Properties();
        p.load(cl.getResourceAsStream(props));

        s_pmf = JDOHelper.getPersistenceManagerFactory(p);

        PDL pdl = new PDL();
        String pdlFile = "com/redhat/persistence/jdotest/package.pdl";
        InputStream is = cl.getResourceAsStream(pdlFile);
        if (is != null) {
            pdl.load(new InputStreamReader(is), pdlFile);
        }

        pdl.emit(((PersistenceManagerFactoryImpl) s_pmf).getMetadataRoot());
    }

    private PersistenceManager getPM() {
        return m_pm;
    }

    private static BigInteger id() {
        return BigInteger.valueOf(s_id++);
    }

    protected void setUp() {
        m_pm = s_pmf.getPersistenceManager();
        m_pm.currentTransaction().begin();
    }

    protected void tearDown() {
        if (m_pm.currentTransaction().isActive()) {
            m_pm.currentTransaction().rollback();
        }
    }

    public void test1() {
        String eName = "seb";
        String dName = "ASR";

        PersistenceManager pm = getPM();
        Employee e = new Employee(id(), eName, new Department(id(), dName));
        pm.makePersistent(e);
        Object eId = pm.getObjectId(e);
        assertNotNull("null object identifier", eId);
        pm.currentTransaction().commit();

        e = null;
        pm.currentTransaction().begin();
        e = (Employee) pm.getObjectById(eId, true);
        assertNotNull("null instance returned by getObjectById", e);
        assertTrue("Bad employee name", eName.equals(e.getName()));
        assertNotNull("null instance returned by getDept", e.getDept());
        assertTrue("Bad department name", dName.equals(e.getDept().getName()));
        pm.deletePersistent(e.getDept());
        pm.deletePersistent(e);
        pm.currentTransaction().commit();
    }

    public void testNullRef() {
        String eName = "nullRefEmp";
        PersistenceManager pm = getPM();
        Employee e = new Employee(id(), eName, null);
        pm.makePersistent(e);
        Object eId = pm.getObjectId(e);
        assertNotNull("null object identifier", eId);
        pm.currentTransaction().commit();

        e = null;
        pm.currentTransaction().begin();
        e = (Employee) pm.getObjectById(eId, true);
        assertNotNull("null instance returned by getObjectById", e);
        assertEquals("Bad employee name", eName, e.getName());
        assertNull("not null instance returned by e.dept", e.getDept());
        pm.deletePersistent(e);
        pm.currentTransaction().commit();
    }
}
