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

import org.myrian.persistence.*;
import org.myrian.persistence.metadata.*;
import org.myrian.persistence.oql.*;
import org.myrian.persistence.oql.Expression;
import java.util.*;

/**
 * CRPCollection
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

abstract class CRPCollection implements Collection, OQLCollection {


    private transient WeakResourceList m_iterators = new WeakResourceList() {
        protected void onRelease(Object o) {
            ((CRPIterator) o).close();
        }
    };

    CRPCollection() {}

    abstract Session ssn();

    abstract ObjectType type();

    protected Signature signature() {
        return new Signature(type());
    }

    private DataSet data() {
        return new DataSet(ssn(), signature(), expression());
    }

    public abstract void clear();

    public abstract boolean add(Object o);

    public abstract boolean remove(Object o);

    public void close() {
        m_iterators.release();
    }

    public Iterator iterator() {
        return new CRPIterator(data().getCursor());
    }

    public int size() {
        return (int) data().size();
    }

    public boolean isEmpty() {
        return data().isEmpty();
    }

    public boolean contains(Object o) {
        DataSet ds = new DataSet
            (ssn(), new Signature(), new Filter
             (new Define(expression(), "this"),
              new Equals(new Variable("this"), new Literal(o))));
        return !ds.isEmpty();
    }

    public boolean retainAll(Collection c) {
        boolean modified = false;
        for (Iterator it = iterator(); it.hasNext(); ) {
            Object o = it.next();
            if (!c.contains(o)) {
                modified |= remove(o);
            }
        }
        return modified;
    }

    public boolean removeAll(Collection c) {
        boolean modified = false;
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            modified |= remove(it.next());
        }
        return modified;
    }

    public boolean addAll(Collection c) {
        boolean modified = false;
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            modified |= add(it.next());
        }
        return modified;
    }

    public boolean containsAll(Collection c) {
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            if (!contains(it.next())) { return false; }
        }
        return true;
    }

    public Object[] toArray(Object[] in) {
        int size = size();
        Object[] result;
        if (size <= in.length) {
            result = in;
        } else {
            result = (Object[]) java.lang.reflect.Array.newInstance
                (in.getClass().getComponentType(), size);
        }
        int index = 0;
        for (Iterator it = iterator(); it.hasNext(); ) {
            result[index++] = it.next();
        }
        return result;
    }

    public Object[] toArray() {
        return toArray(new Object[size()]);
    }

    private class CRPIterator implements Iterator, Closeable {

        private Cursor m_cursor;
        private Object m_current = null;
        private Object m_next = null;
        private boolean m_started = false;

        CRPIterator(Cursor cursor) {
            m_cursor = cursor;
            CRPCollection.this.m_iterators.add(this);
        }

        public void close() {
            if (m_cursor != null) { m_cursor.close(); }
            m_cursor = null;
            m_next = null;
            m_started = true;
        }

        private void advance() {
            if (m_cursor == null) {
                throw new NoSuchElementException();
            } else if (m_cursor.next()) {
                m_next = m_cursor.get();
            } else {
                close();
            }
        }

        private void start() {
            if (!m_started) {
                advance();
                m_started = true;
            }
        }

        public boolean hasNext() {
            start();
            return m_next != null;
        }

        public Object next() {
            start();
            m_current = m_next;
            advance();
            return m_current;
        }

        public void remove() {
            CRPCollection.this.remove(m_current);
        }

    }

}
