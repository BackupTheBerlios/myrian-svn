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

public abstract class AbstractCase extends TestCase {

    private static int s_id = 0;
    private static PersistenceManagerFactory s_pmf;
    protected PersistenceManager m_pm;

    protected BigInteger id() {
        return BigInteger.valueOf(s_id++);
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
