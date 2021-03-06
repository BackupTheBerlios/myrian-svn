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

import org.myrian.persistence.oql.Expression;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements {@link Set} by delegating to another set implementation.
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2004-07-14
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
