package com.redhat.persistence.jdo;

import java.math.BigInteger;
import java.util.Properties;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import junit.framework.TestCase;

public abstract class AbstractCase extends TestCase {

    private static int s_id = 0;
    private static PersistenceManagerFactory s_pmf;
    protected PersistenceManager m_pm;

    public AbstractCase() {}

    public AbstractCase(String name) {
        super(name);
    }

    protected BigInteger id() {
        return BigInteger.valueOf(s_id++);
    }

    protected int intID() {
        return s_id++;
    }

    protected void setUp() throws Exception {
        ClassLoader cl = AbstractCase.class.getClassLoader();
        String props = "jdo.properties";
        Properties p = new Properties();
        p.load(cl.getResourceAsStream(props));

        s_pmf = JDOHelper.getPersistenceManagerFactory(p);
        m_pm = s_pmf.getPersistenceManager();
        m_pm.currentTransaction().begin();
    }

    protected void tearDown() {
        if (m_pm.currentTransaction().isActive()) {
            m_pm.currentTransaction().rollback();
        }
    }
}
