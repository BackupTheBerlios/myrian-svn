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
import javax.jdo.PersistenceManagerFactory;

import junit.extensions.TestSetup;
import junit.framework.Test;

public class JDOTestSetup extends TestSetup {
    private static int s_id = 0;
    private static PersistenceManagerFactory s_pmf = null;

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
        String pdlFile = "com/redhat/persistence/jdo/package.pdl";
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
        conn.createStatement().execute("create sequence jdotest_seq");
        conn.commit();
        conn.close();
    }

    protected void tearDown() throws Exception {
        PersistenceManagerFactoryImpl pmf =
            (PersistenceManagerFactoryImpl) s_pmf;
        Root root = pmf.getMetadataRoot();
        Connection conn = Connections.acquire
            (s_pmf.getConnectionURL());
        Schema.unload(root, conn);
        conn.createStatement().execute("drop sequence jdotest_seq");
        conn.commit();
    }
}
