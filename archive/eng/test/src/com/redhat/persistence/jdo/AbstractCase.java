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

import java.math.BigInteger;
import java.util.Properties;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.redhat.persistence.TestSession;

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

    public void setUpPersistenceManager() throws Exception {
        ClassLoader cl = getClass().getClassLoader();
        String props = "jdo.properties";
        Properties p = new Properties();
        p.load(cl.getResourceAsStream(props));

        s_pmf = JDOHelper.getPersistenceManagerFactory(p);
        m_pm = s_pmf.getPersistenceManager();
    }

    public void runBare() throws Throwable {
        setUpPersistenceManager();
        try {
            super.runBare();
        } finally {
            m_pm.currentTransaction().rollback();
        }
    }

    public void commit() {
        TestSession.testCommit(((PersistenceManagerImpl) m_pm).getSession());
    }

}
