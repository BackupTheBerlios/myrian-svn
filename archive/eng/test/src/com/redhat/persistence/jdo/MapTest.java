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

import com.redhat.persistence.Session;
import com.redhat.persistence.jdo.PersistenceManagerImpl;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * MapTest
 *
 * @since 2004-07-13
 * @version $Revision: #9 $ $Date: 2004/08/30 $
 **/
public class MapTest extends WithTxnCase {
    private final static Logger s_log = Logger.getLogger(MapTest.class);

    private final static String TOPIC1 = "Samba";
    private final static String TOPIC2 = "OProfile";

    private final static Integer PAGE1 = new Integer(3);
    private final static Integer PAGE2 = new Integer(15);

    private Session m_ssn;
    private Magazine m_mag;

    public MapTest() {}

    public MapTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        PersistenceManagerImpl pm = (PersistenceManagerImpl) m_pm;
        m_ssn = pm.getSession();
        m_mag = new Magazine(0);
        m_mag.setTitle("Wide Open");

        m_mag.getIndex().put(TOPIC1, PAGE1);
        m_mag.getIndex().put(TOPIC2, PAGE2);

        m_pm.makePersistent(m_mag);
    }

    public void testMagazine1() {
        javax.jdo.Query qq = m_pm.newQuery
            (Extensions.OQL, "all(com.redhat.persistence.jdo.Magazine)");
        Collection magazines = (Collection) qq.execute();
        Iterator it = magazines.iterator();
        assertTrue("has next", it.hasNext());
        Magazine current = (Magazine) it.next();
        assertEquals("wide open", m_mag, current);
        Map idx = current.getIndex();
        assertTrue("has TOPIC1", idx.containsKey(TOPIC1));
        assertTrue("has TOPIC2", idx.containsKey(TOPIC2));
        assertEquals("TOPIC1 on PAGE 3", PAGE1, idx.get(TOPIC1));
        assertEquals("PAGE 3", PAGE1, idx.remove(TOPIC1));
        assertTrue("has TOPIC1", !idx.containsKey(TOPIC1));
        assertTrue("has TOPIC2", idx.keySet().contains(TOPIC2));
    }

    public void testEntrySet() {
        Map.Entry entry = new Entry(TOPIC2, PAGE2);
        assertTrue("entrySet has (TOPIC2, PAGE2)",
                   m_mag.getIndex().entrySet().contains(entry));
    }

    public void testKeySet() {
        Set keySet = m_mag.getIndex().keySet();
        assertEquals("key set size", 2, keySet.size());
        assertTrue("removed TOPIC1", keySet.remove(TOPIC1));
        assertEquals("entry set size", 1, m_mag.getIndex().size());

        Map.Entry entry = new Entry(TOPIC2, PAGE2);
        assertTrue("entrySet has (TOPIC2, PAGE2)",
                   m_mag.getIndex().entrySet().contains(entry));
    }


    public void testValues() {
        assertTrue("values has PAGE1",
                   m_mag.getIndex().values().contains(PAGE1));
        assertTrue("values has PAGE1",
                   m_mag.getIndex().values().contains(PAGE2));
        assertEquals("values size", 2, m_mag.getIndex().values().size());
    }

    public void testContainsValue() {
        assertTrue("has PAGE1", m_mag.getIndex().containsValue(PAGE1));
        assertFalse("has Orwell",
                    m_mag.getIndex().containsValue(new Integer(1984)));
    }

    public void testSize() {
        assertEquals("number of elements", 2, m_mag.getIndex().size());
    }

    public void testClear() {
        Map idx = m_mag.getIndex();
        assertFalse("empty", idx.isEmpty());
        idx.clear();
        assertEquals("size", 0, idx.size());
    }

    public void testEquals() {
        Map map = new HashMap();
        map.putAll(m_mag.getIndex());
        assertEquals("map.equals(m_mag.getIndex())", map, m_mag.getIndex());
        assertEquals("m_mag.getIndex().equals(map)", m_mag.getIndex(), map);
    }

    public void testHashCode() {
        Map map = new HashMap();
        map.putAll(m_mag.getIndex());
        assertEquals("hash codes", map.hashCode(), m_mag.getIndex().hashCode());
    }
}
