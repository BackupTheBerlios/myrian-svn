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

import org.myrian.persistence.Session;
import org.myrian.persistence.Signature;
import org.myrian.persistence.metadata.ObjectType;
import org.myrian.persistence.oql.Expression;
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
