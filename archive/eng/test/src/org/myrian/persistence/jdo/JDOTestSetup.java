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

import org.myrian.util.jdbc.Connections;
import org.myrian.persistence.jdo.PersistenceManagerFactoryImpl;
import org.myrian.persistence.metadata.*;
import org.myrian.persistence.pdl.PDL;
import org.myrian.persistence.pdl.Schema;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.*;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import junit.extensions.TestSetup;
import junit.framework.Test;

public class JDOTestSetup extends TestSetup {
    private static int s_id = 0;
    private static PersistenceManagerFactory s_pmf = null;

    private List m_classes = new ArrayList();

    public JDOTestSetup(Test test) {
        super(test);
    }

    public void load(Class klass) {
        m_classes.add(klass);
    }

    protected void setUp() throws Exception {
        ClassLoader cl = SimpleTest.class.getClassLoader();
        String props = "jdo.properties";
        Properties p = new Properties();
        p.load(cl.getResourceAsStream(props));

        s_pmf = JDOHelper.getPersistenceManagerFactory(p);

        Connection conn = Connections.acquire(s_pmf.getConnectionURL());
        Extensions.load(m_classes, conn);
        conn.createStatement().execute("create sequence jdotest_seq");
        conn.commit();
        conn.close();
    }

    protected void tearDown() throws Exception {
        Connection conn = Connections.acquire(s_pmf.getConnectionURL());
        Extensions.unload(m_classes, conn);
        conn.createStatement().execute("drop sequence jdotest_seq");
        conn.commit();
    }
}
