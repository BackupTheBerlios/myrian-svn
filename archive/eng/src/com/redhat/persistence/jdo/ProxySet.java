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
 * @version $Id: //eng/persistence/dev/src/com/redhat/persistence/jdo/ProxySet.java#2 $
 **/
class ProxySet implements Set, OQLCollection {
    private final CRPSet m_set;

    ProxySet(CRPSet proxied) {
        if (proxied == null) { throw new NullPointerException("proxied"); }
        m_set = proxied;
    }

    public Expression expression() {
        return m_set.expression();
    }

    public int size() {
        return m_set.size();
    }

    public void clear() {
        m_set.clear();
    }

    public boolean isEmpty() {
        return m_set.isEmpty();
    }

    public boolean add(Object element) {
        return m_set.add(element);
    }

    public boolean addAll(Collection coll) {
        return m_set.addAll(coll);
    }

    public boolean contains(Object element) {
        return m_set.contains(element);
    }

    public boolean containsAll(Collection coll) {
        return m_set.containsAll(coll);
    }

    public boolean remove(Object element) {
        return m_set.remove(element);
    }

    public boolean removeAll(Collection coll) {
        return m_set.removeAll(coll);
    }

    public boolean retainAll(Collection coll) {
        return m_set.retainAll(coll);
    }

    public Iterator iterator() {
        return m_set.iterator();
    }

    public Object[] toArray() {
        return m_set.toArray();
    }

    public Object[] toArray(Object[] ary) {
        return m_set.toArray(ary);
    }
}

