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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import org.apache.log4j.Logger;

/**
 * CRPList
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

class CRPList implements List, OQLCollection {
    private final static Logger s_log = Logger.getLogger(CRPList.class);
    private final static int NEGATIVE_ONE  = -1;
    private final static Integer NOT_FOUND = new Integer(NEGATIVE_ONE);

    private Map elements;
    private transient String m_fieldName;
    private transient OQLCollection m_values;
    private transient WeakResourceList m_iterators = new WeakResourceList() {
        protected void onRelease(Object o) {
            ((CRPListIterator) o).close();
        }
    };

    CRPList() {
        elements = new HashMap();
    }

    void setFieldName(String fieldName) {
        m_fieldName = C.concat(fieldName, "$elements$entries");

        Query query = getPMI().newQuery
            (Extensions.OQL,
             C.concat("sort($1.", m_fieldName, ", key).value"));
        m_values = (OQLCollection) query.execute(getContainer());
    }

    private PersistenceManagerImpl getPMI() {
        return (PersistenceManagerImpl) JDOHelper.getPersistenceManager(this);
    }

    public Expression expression() {
        return m_values.expression();
    }

    public void close() {
        m_values.close();
        m_iterators.release();
    }

    private Object getContainer() {
        PersistenceManagerImpl pmi = getPMI();
        StateManagerImpl smi = pmi.getStateManager(this);
        return smi.getContainer();
    }

    private void append(int end, Object element) {
        elements.put(new Integer(end), element);
    }

    private Integer m_indexOf(Object element) {
        Query query = getPMI().newQuery
            (Extensions.OQL,
             C.concat("limit(sort(filter($1.",
                      m_fieldName,
                      ", value == $2), key), 1)"));
        Collection coll = (Collection) query.execute(getContainer(), element);
        Iterator it = coll.iterator();
        if (it.hasNext()) {
            MapEntry entry = (MapEntry) it.next();
            return (Integer) entry.getKey();
        } else {
            return NOT_FOUND;
        }
    }

    private Object m_remove(Integer index) {
        CRPMap.NullableObject value = ((CRPMap) elements).nullSavvyGet(index);
        if (value.isNull()) {
            throw new IndexOutOfBoundsException(index.toString());
        }

        int idx = index.intValue();
        for (; idx < size() - 1; idx++) {
            elements.put(new Integer(idx), get(idx + 1));
        }
        elements.remove(new Integer(idx));
        return value.getObject();
    }

    private class CRPListIterator implements ListIterator, Closeable {
        private int m_index;
        private boolean m_forward;
        private Iterator m_elements;

        CRPListIterator(int from) {
            m_index = from;
            m_forward = true;
            resetIterator();
            CRPList.this.m_iterators.add(this);
        }

        public void close() {
            Extensions.close(m_elements);
        }

        private void resetIterator() {
            final String oql;
            if (m_forward) {
                oql = C.concat
                    ("sort(filter($1.", m_fieldName,
                     ", key >= $2), key)");
            } else {
                oql = C.concat
                    ("rsort(filter($1.", m_fieldName,
                     ", $2 >= key), key)");
            }

            Query query = getPMI().newQuery(Extensions.OQL, oql);

            Collection coll = (Collection) query.execute
                (getContainer(), new Integer(m_index));
            m_elements = coll.iterator();
        }

        private void possiblyReverse(boolean forward) {
            if (m_forward != forward) {
                m_forward = forward;
                resetIterator();
            }
        }

        // ====================================================================
        // ListIterator implementation
        // ====================================================================

        public int nextIndex() {
            return m_index;
        }

        public int previousIndex() {
            return m_index - 1;
        }

        public boolean hasNext() {
            return m_elements.hasNext();
        }

        public boolean hasPrevious() {
            return m_index > 0;
        }

        public Object next() {
            possiblyReverse(true);
            MapEntry entry = (MapEntry) m_elements.next();
            m_index++;
            return entry.getValue();
        }

        public Object previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException
                    ("previousIndex: " + previousIndex());
            }

            possiblyReverse(false);
            MapEntry entry = (MapEntry) m_elements.next();
            m_index--;
            return entry.getValue();
        }

        /* Unsupported operations */

        public void add(Object element) {
            throw new UnsupportedOperationException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void set(Object element) {
            throw new UnsupportedOperationException();
        }
    }


    // ========================================================================
    // List interface (except methods in Collection)
    // ========================================================================

    public int hashCode() {
        int hashCode = 1;
        for (Iterator it=iterator(); it.hasNext(); ) {
            Object elem = it.next();
            hashCode = 31*hashCode + (elem==null ? 0 : elem.hashCode());
        }
        return  hashCode;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof List)) { return false; }
        List list = (List) obj;

        if (size() != list.size()) { return false; }

        Iterator it1 = iterator();
        Iterator it2 = list.iterator();
        while (it1.hasNext()) {
            Object o1 = it1.next();
            if (it2.hasNext()) {
                Object o2 = it2.next();
                if (o1 == null) {
                    if (o2 != null) {
                        return false;
                    }
                } else if (!o1.equals(o2)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public int size() {
        return elements.size();
    }

    public void clear() {
        elements.clear();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public Object[] toArray() {
        return elements.values().toArray();
    }

    public Object[] toArray(Object[] ary) {
        return elements.values().toArray(ary);
    }

    public Object get(int index) {
        Object result = elements.get(new Integer(index));
        // XXX: this assumes that nulls can't be stored in CRPList.  Change this
        // to allow nulls.
        if (result == null) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        return result;
    }

    public void add(int index, Object element) {
        int size = size();
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException
                ("size=" + index + "; index=" + index);
        }

        Object obj = element;
        for (int ii=index; ii<size; ii++) {
            obj = set(ii, obj);
        }
        append(size, obj);
    }

    public int indexOf(Object element) {
        return m_indexOf(element).intValue();
    }

    public int lastIndexOf(Object element) {
        Query query = getPMI().newQuery
            (Extensions.OQL,
             C.concat("limit(rsort(filter($1.",
                      m_fieldName,
                      ", value == $2), key), 1)"));
        Collection coll = (Collection) query.execute(getContainer(), element);
        Iterator it = coll.iterator();
        if (it.hasNext()) {
            MapEntry entry = (MapEntry) it.next();
            return ((Integer) entry.getKey()).intValue();
        } else {
            return NEGATIVE_ONE;
        }
    }

    public Object set(int index, Object element) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }

        if ( elements instanceof CRPMap) {
            final Integer idx = new Integer(index);
            CRPMap.NullableObject previous =
                ((CRPMap) elements).nullSavvyPut(idx, element);

            if (previous.isNull()) {
                elements.remove(idx);
                throw new IndexOutOfBoundsException
                    (C.concat("index=", String.valueOf(index),
                              "; element=", element));
            }
            return previous.getObject();
        } else {
            if (index > elements.size()) {
                throw new IndexOutOfBoundsException(String.valueOf(index));
            }
            return elements.put(new Integer(index), element);
        }
    }

    public boolean add(Object element) {
        append(size(), element);
        return true;
    }

    public boolean addAll(int index, Collection coll) {
        boolean modified = false;
        // XXX: note that this is horribly inefficient. We end up shifting the
        // tail of the list coll.size() times.
        for (Iterator it = coll.iterator(); it.hasNext(); ) {
            add(index, it.next());
            modified = true;
        }
        return modified;
    }

    public boolean addAll(Collection coll) {
        boolean modified = false;
        for (Iterator it = coll.iterator(); it.hasNext(); ) {
            add(it.next());
            modified = true;
        }
        return modified;
    }

    public boolean contains(Object element) {
        return elements.values().contains(element);
    }

    public boolean containsAll(Collection coll) {
        return elements.values().containsAll(coll);
    }

    public Object remove(int index) {
        if ( elements instanceof CRPMap) {
            return m_remove(new Integer(index));
        } else {
            return elements.remove(new Integer(index));
        }
    }

    public boolean remove(Object element) {
        Integer index = m_indexOf(element);
        if (NOT_FOUND.equals(index)) { return false; }

        m_remove(index);
        return true;
    }

    public boolean removeAll(Collection coll) {
        boolean modified = false;
        for (Iterator it=coll.iterator(); it.hasNext(); ) {
            modified |= remove(it.next());
        }
        return modified;
    }

    public boolean retainAll(Collection coll) {
        boolean modified = false;
        Query query = getPMI().newQuery(Extensions.OQL, "rsort($1, key)");
        Collection eColl = (Collection) query.execute(elements.entrySet());
        Iterator entries = eColl.iterator();
        List toDelete = new ArrayList();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            if (!coll.contains(entry.getValue())) {
                modified = true;
                toDelete.add(entry);
            }
        }
        for (Iterator it = toDelete.iterator(); it.hasNext(); ) {
            m_remove((Integer) ((Map.Entry) it.next()).getKey());
        }
        return modified;
    }

    public Iterator iterator() {
        return m_values.iterator();
    }

    public List subList(int from, int to) {
        throw new UnsupportedOperationException();
    }

    public ListIterator listIterator() {
        return new CRPListIterator(0);
    }

    public ListIterator listIterator(int index) {
        return new CRPListIterator(index);
    }
}
