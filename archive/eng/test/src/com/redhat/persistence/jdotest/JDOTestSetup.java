package com.redhat.persistence.jdotest;

import com.arsdigita.util.jdbc.Connections;
import com.redhat.persistence.jdo.PersistenceManagerFactoryImpl;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.pdl.PDL;
import com.redhat.persistence.pdl.Schema;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Properties;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import junit.extensions.TestSetup;
import junit.framework.Test;

public class JDOTestSetup extends TestSetup {
    private static int s_id = 0;
    private static PersistenceManagerFactory s_pmf = null;
    private static PersistenceManager m_pm = null;

    public JDOTestSetup(Test test) {
        super(test);
    }

    protected void setUp() throws Exception {
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
}
