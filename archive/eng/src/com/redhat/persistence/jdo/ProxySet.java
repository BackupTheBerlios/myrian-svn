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

import com.redhat.persistence.oql.Expression;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements {@link Set} by delegating to another set implementation.
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2004-07-14
 * @version $Id: //eng/persistence/dev/src/com/redhat/persistence/jdo/ProxySet.java#6 $
 **/
class ProxySet implements Set, OQLCollection {
    private final OQLCollection m_coll;

    ProxySet(OQLCollection proxied) {
        if (proxied == null) { throw new NullPointerException("proxied"); }
        m_coll = proxied;
    }

    public Expression expression() {
        return m_coll.expression();
    }

    public void close() {
        m_coll.close();
    }

    public int size() {
        return m_coll.size();
    }

    public void clear() {
        m_coll.clear();
    }

    public boolean isEmpty() {
        return m_coll.isEmpty();
    }

    public boolean add(Object element) {
        return m_coll.add(element);
    }

    public boolean addAll(Collection coll) {
        return m_coll.addAll(coll);
    }

    public boolean contains(Object element) {
        return m_coll.contains(element);
    }

    public boolean containsAll(Collection coll) {
        return m_coll.containsAll(coll);
    }

    public boolean remove(Object element) {
        return m_coll.remove(element);
    }

    public boolean removeAll(Collection coll) {
        return m_coll.removeAll(coll);
    }

    public boolean retainAll(Collection coll) {
        return m_coll.retainAll(coll);
    }

    public Iterator iterator() {
        return m_coll.iterator();
    }

    public Object[] toArray() {
        return m_coll.toArray();
    }

    public Object[] toArray(Object[] ary) {
        return m_coll.toArray(ary);
    }

    public boolean equals(Object obj) {
        return m_coll.equals(obj);
    }

    public int hashCode() {
        return m_coll.hashCode();
    }
}
