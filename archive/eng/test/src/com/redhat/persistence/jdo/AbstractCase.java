/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
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
