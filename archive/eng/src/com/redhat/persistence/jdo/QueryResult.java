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
import com.redhat.persistence.Signature;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.oql.Expression;
import java.util.Collection;
import java.util.Iterator;
import javax.jdo.JDOUserException;

class QueryResult implements OQLCollection {

    private CRPCollection m_coll;

    QueryResult(CRPCollection c) {
        m_coll = c;
    }

    void makeUseless() {
        m_coll = null;
    }

    private boolean useless() {
        return m_coll == null;
    }

    private void fail() {
        throw new JDOUserException("query result was closed");
    }

    public Expression expression() {
        if (useless()) {
            fail();
        }

        return m_coll.expression();
    }

    public void close() {
        if (useless()) {
            fail();
        }

        m_coll.close();
    }

    public boolean add(Object o) {
        if (useless()) {
            fail();
        }

        return m_coll.add(o);
    }

    public boolean remove(Object o) {
        if (useless()) {
            fail();
        }

        return m_coll.remove(o);
    }

    public void clear() {
        if (useless()) {
            fail();
        }

        m_coll.clear();
    }

    public Iterator iterator() {
        if (useless()) {
            fail();
        }

        return m_coll.iterator();
    }

    public int size() {
        if (useless()) {
            fail();
        }

        return m_coll.size();
    }

    public boolean isEmpty() {
        if (useless()) {
            fail();
        }

        return m_coll.isEmpty();
    }

    public boolean contains(Object o) {
        if (useless()) {
            fail();
        }

        return m_coll.contains(o);
    }

    public boolean retainAll(Collection c) {
        if (useless()) {
            fail();
        }

        return m_coll.retainAll(c);
    }

    public boolean removeAll(Collection c) {
        if (useless()) {
            fail();
        }

        return m_coll.removeAll(c);
    }

    public boolean addAll(Collection c) {
        if (useless()) {
            fail();
        }

        return m_coll.addAll(c);
    }

    public boolean containsAll(Collection c) {
        if (useless()) {
            fail();
        }

        return m_coll.containsAll(c);
    }

    public Object[] toArray(Object[] in) {
        if (useless()) {
            fail();
        }

        return m_coll.toArray(in);
    }

    public Object[] toArray() {
        if (useless()) {
            fail();
        }

        return m_coll.toArray();
    }
}
