package com.redhat.persistence.jdo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * CRPMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #24 $ $Date: 2004/07/30 $
 **/
class CRPMap implements Map {
    private final static NullableObject NULL = new NullableObject() {
            public boolean isNull() { return true; }
            public Object getObject() { return null; }
        };

    private Set entries;
    // XXX this is not a permanent solution
    private transient int m_count;

    CRPMap() {
        entries = new HashSet();
    }

    private Map.Entry getEntry(Object key) {
        Query query = getPMI().newQuery("oql", "filter($1, key==$2)");
        Collection coll = (Collection) query.execute(entries, key);
        Iterator it = coll.iterator();
        return it.hasNext() ? (Map.Entry) it.next() : null;
    }

    NullableObject nullSavvyGet(Object key) {
        final Map.Entry me = getEntry(key);

        return me == null ? NULL : new Nullable(me.getValue());
    }

    private PersistenceManagerImpl getPMI() {
        return (PersistenceManagerImpl) JDOHelper.getPersistenceManager(this);
    }

    void lock() {
        C.lock(getPMI().getSession(), this);
    }

    private Object getContainer() {
        PersistenceManagerImpl pmi = getPMI();
        StateManagerImpl smi = pmi.getStateManager(this);
        return smi.getContainer();
    }

    private MapEntry newMapEntry(Object key, Object value) {
        lock();
        PersistenceManagerImpl pmi = getPMI();
        StateManagerImpl smi = pmi.getStateManager(this);

        MapEntry entry = new MapEntry(getContainer(), key);
        entry.setValue(value);
        return entry;
    }

    interface NullableObject {
        boolean isNull();
        Object getObject();
    }

    private static class Nullable implements NullableObject {
        private final Object m_object;

        Nullable(Object object) {
            m_object = object;
        }

        public Object getObject() { return m_object; }

        public boolean isNull() { return false; }
    }

    NullableObject nullSavvyPut(Object key, Object value) {
        final Map.Entry me = getEntry(key);

        if (me == null) {
            entries.add(newMapEntry(key, value));
            m_count++;
            return NULL;
        } else {
            return new Nullable(me.setValue(value));
        }
    }

    NullableObject nullSavvyRemove(Object key) {
        Map.Entry entry = getEntry(key);

        if (entry == null) { return NULL; }

        Object value = entry.getValue();
        JDOHelper.getPersistenceManager(entry).deletePersistent(entry);
        m_count--;
        return new Nullable(value);
    }

    // =========================================================================
    // Map interface
    // =========================================================================

    public Object get(Object key) {
        Map.Entry me = getEntry(key);
        return me == null ? null : me.getValue();
    }

    public Object put(Object key, Object value) {
        return nullSavvyPut(key, value).getObject();
    }

    public Object remove(Object key) {
        return nullSavvyRemove(key).getObject();
    }

    private Collection keys() {
        Query query = getPMI().newQuery("oql", "$1.key");
        return (Collection) query.execute(entries);
    }

    public Set keySet() {
        // XXX: implement for the case when entries is a HashSet
        if (!(entries instanceof CRPSet)) {
            throw new UnsupportedOperationException();
        }

        return new ProxySet((OQLCollection) keys()) {
                public boolean add(Object elem) {
                    // By design. The javadoc says entrySet() does not implement
                    // add.
                    throw new UnsupportedOperationException();
                }

                public boolean addAll(Collection coll) {
                    // By design. The javadoc says entrySet() does not implement
                    // addAll.
                    throw new UnsupportedOperationException();
                }

                public boolean remove(Object key) {
                    return !nullSavvyRemove(key).isNull();
                }

                public boolean removeAll(Collection coll) {
                    boolean modified = false;
                    for (Iterator it=coll.iterator(); it.hasNext(); ) {
                        modified |= !nullSavvyRemove(it.next()).isNull();
                    }
                    return modified;
                }

                // XXX: implement retainAll()
                // public boolean retainAll(Collection coll) {}

                public void clear() {
                    CRPMap.this.clear();
                }
            };
    }

    public Set entrySet() {
        if (!(entries instanceof CRPSet)) { return entries; }

        return new ProxySet((CRPSet) entries) {
                public boolean add(Object elem) {
                    // By design. The javadoc says entrySet() does not implement
                    // add.
                    throw new UnsupportedOperationException();
                }

                public boolean addAll(Collection coll) {
                    // By design. The javadoc says entrySet() does not implement
                    // addAll.
                    throw new UnsupportedOperationException();
                }

                public boolean contains(Object elem) {
                    if (!(elem instanceof Map.Entry)) { return false; }

                    Map.Entry entry = (Map.Entry) elem;
                    Map.Entry mapEntry = getEntry(entry.getKey());
                    return mapEntry==null ? false : mapEntry.equals(entry);
                }
            };
    }

    public void putAll(Map map) {
        for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            put(me.getKey(), me.getValue());
        }
    }

    public Collection values() {
        Query query = getPMI().newQuery("oql", "$1.value");
        return (Collection) query.execute(entries);
    }

    public boolean containsValue(Object value) {
        Query query = getPMI().newQuery("oql", "filter($1, value==$2)");
        Collection coll = (Collection) query.execute(entries, value);
        return coll.size() > 0;
    }

    public boolean containsKey(Object key) {
        return getEntry(key) != null;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void clear() {
        entries.clear();
    }

    public int size() {
        if (!getPMI().getSession().isPersisted(getContainer())) {
            return m_count;
        }
        return keys().size();
    }
}
