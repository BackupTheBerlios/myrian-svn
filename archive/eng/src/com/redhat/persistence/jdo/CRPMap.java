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
 * @version $Revision: #10 $ $Date: 2004/07/13 $
 **/
class CRPMap implements Map {
    private Set entries;

    CRPMap() {
        entries = new HashSet();
    }

    private Map.Entry getEntry(Object key) {
        PersistenceManager pm = JDOHelper.getPersistenceManager(this);
        Query query = pm.newQuery("oql", "filter($1, key==$2)");
        Collection coll = (Collection) query.execute(entries, key);
        Iterator it = coll.iterator();
        return it.hasNext() ? (Map.Entry) it.next() : null;
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
            PersistenceManagerImpl pmi = (PersistenceManagerImpl)
                JDOHelper.getPersistenceManager(this);
            StateManagerImpl smi = pmi.getStateManager(this);

            Map.Entry entry = new MapEntry
                (pmi.getSession().retrieve(smi.getPropertyMap()), key);

            entry.setValue(value);
            entries.add(entry);
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

    public Set keySet() {
        throw new UnsupportedOperationException();
    }

    public Set entrySet() {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map map) {
        for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            put(me.getKey(), me.getValue());
        }
    }

    public Collection values() {
        throw new UnsupportedOperationException();
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key) {
        return getEntry(key) != null;
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public int size() {
        throw new UnsupportedOperationException();
    }
}
