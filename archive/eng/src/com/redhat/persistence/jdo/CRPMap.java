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
 * @version $Revision: #18 $ $Date: 2004/07/14 $
 **/
class CRPMap implements Map {
    private Set entries;

    CRPMap() {
        entries = new HashSet();
    }

    private Map.Entry getEntry(Object key) {
        Query query = getPMI().newQuery("oql", "filter($1, key==$2)");
        Collection coll = (Collection) query.execute(entries, key);
        Iterator it = coll.iterator();
        return it.hasNext() ? (Map.Entry) it.next() : null;
    }

    private PersistenceManagerImpl getPMI() {
        return (PersistenceManagerImpl) JDOHelper.getPersistenceManager(this);
    }

    private MapEntry newMapEntry(Object key, Object value) {
        PersistenceManagerImpl pmi = getPMI();
        StateManagerImpl smi = pmi.getStateManager(this);

        MapEntry entry = new MapEntry
            (pmi.getSession().retrieve(smi.getPropertyMap()), key);

        entry.setValue(value);
        return entry;
    }
    // =========================================================================
    // Map interface
    // =========================================================================

    public Object get(Object key) {
        Map.Entry me = getEntry(key);
        return me == null ? null : me.getValue();
    }

    public Object put(Object key, Object value) {
        // XXX: locking
        final Map.Entry me = getEntry(key);

        if (me == null) {
            entries.add(newMapEntry(key, value));
            return null;
        } else {
            return me.setValue(value);
        }
    }

    public Object remove(Object key) {
        Map.Entry entry = getEntry(key);
        if (entry == null) { return null; }

        Object value = entry.getValue();
        JDOHelper.getPersistenceManager(entry).deletePersistent(entry);
        return value;
    }

    private Collection keys() {
        Query query = getPMI().newQuery("oql", "$1.key");
        return (Collection) query.execute(entries);
    }

    public Set keySet() {
        // XXX: this is a temporary hack.  We need to return a CRPSet or some
        // such instead.
        return new HashSet(keys());
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
        return keys().size();
    }
}
