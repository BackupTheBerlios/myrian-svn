package com.redhat.persistence.jdo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import org.apache.log4j.Logger;

/**
 * CRPList
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #11 $ $Date: 2004/07/21 $
 **/

class CRPList implements List {
    private final static Logger s_log = Logger.getLogger(CRPList.class);
    private final static Integer NOT_FOUND = new Integer(-1);

    private Map elements;
    private transient String m_fieldName;

    CRPList() {
        elements = new HashMap();
    }

    void setFieldName(String fieldName) {
        m_fieldName = C.concat(fieldName, "$elements$entries");
    }

    private PersistenceManagerImpl getPMI() {
        return (PersistenceManagerImpl) JDOHelper.getPersistenceManager(this);
    }

    private void append(int end, Object element) {
        elements.put(new Integer(end), element);
    }

    private Integer m_indexOf(Object element) {
        Query query = getPMI().newQuery
            ("oql",
             C.concat("limit(sort(filter($1.",
                      m_fieldName,
                      ", value == $2), key), 1)"));
        Collection coll = (Collection) query.execute(elements, element);
        Iterator it = coll.iterator();
        if (it.hasNext()) {
            MapEntry entry = (MapEntry) it.next();
            return (Integer) entry.getKey();
        } else {
            return NOT_FOUND;
        }
    }

    private Object m_remove(Integer index) {
        // XXX: deal with with null values
        Object oldValue = elements.remove(index);
        if (oldValue == null) {
            throw new IndexOutOfBoundsException(index.toString());
        }
        return oldValue;
    }


    // ========================================================================
    // List interface (except methods in Collection)
    // ========================================================================

    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof List)) { return false; }
        List list = (List) obj;

        if (size() != list.size()) { return false; }

        Iterator it1 = iterator();
        Iterator it2 = iterator();
        while (it1.hasNext()) {
            Object o1 = it1.next();
            if (it2.hasNext()) {
                Object o2 = it2.next();
                if (!o1.equals(o2)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return !it2.hasNext();
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
        // XXX: there is no syntax for specifying sort order in OQL
        Query query = getPMI().newQuery
            ("oql",
             C.concat("sort(filter($1.",
                      m_fieldName,
                      ", value == $2), key)"));
        Collection coll = (Collection) query.execute(elements, element);
        Iterator it = coll.iterator();

        MapEntry entry = null;
        while (it.hasNext()) {
            entry = (MapEntry) it.next();
        }

        if (entry == null) {
            return -1;
        } else {
            return ((Integer) entry.getKey()).intValue();
        }
    }

    public Object set(int index, Object element) {
        // XXX deal with nulls
        final Integer idx = new Integer(index);
        Object previous = elements.put(idx, element);
        if (previous == null) {
            elements.remove(idx);
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        return previous;
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
        throw new UnsupportedOperationException();
    }

    public Object remove(int index) {
        return m_remove(new Integer(index));
    }

    public boolean remove(Object element) {
        Integer index = m_indexOf(element);
        if (NOT_FOUND.equals(index)) { return false; }

        m_remove(index);
        return true;
    }

    public boolean removeAll(Collection coll) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection coll) {
        throw new UnsupportedOperationException();
    }

    public Iterator iterator() {
        // XXX: this is unordered
        return elements.values().iterator();
    }

    public List subList(int from, int to) {
        throw new UnsupportedOperationException();
    }

    public ListIterator listIterator() {
        throw new UnsupportedOperationException();
    }

    public ListIterator listIterator(int index) {
        throw new UnsupportedOperationException();
    }
}
