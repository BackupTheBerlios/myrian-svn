package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.oql.*;
import com.redhat.persistence.oql.Static;
import com.redhat.persistence.oql.Expression;

import java.util.*;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.spi.PersistenceCapable;

/**
 * CRPMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/07/09 $
 **/

class CRPMap implements Map {
    private final static String KEY   = "key";
    private final static String VALUE = "val";

    transient private Session m_ssn;

    /* The setup:
     *
     * + We have a Java class Foo that implements PersistenceCapable and has a
     *   field dict of type java.util.Map.
     *
     * + The class Foo is backed by an object type "Foo".  The object type "Foo"
     *   has a property named "foo$entries" whose object type is "FooMapHelper".
     *
     * + The object type "FooMapHelper" has properties named "key" and "value".
     *
     * + The object type "FooMapHelper" has a [1..1] property "foo" of
     *   type "Foo".
     */

    // the enclosing JDO instance of type Foo that contains this Map as a field
    transient private Object m_object;
    // the property of "Foo" named "foo$entries"
    transient private Property m_mapProp;
    // the property of "FooMapHelper" named "foo"
    transient private Property m_container;
    // the property of "FooMapHelper" named "key"
    transient private Property m_key;
    // the property of "FooMapHelper" named "value"
    transient private Property m_value;

    CRPMap() {}

    CRPMap(Session ssn, Object object, Property property) {
        m_ssn = ssn;
        m_object = object;
        m_mapProp = property;
    }

    private void init() {
        final ObjectType type = m_mapProp.getType();
        if (type.getKeyProperties().size() != 2) {
            throw new IllegalStateException
                ("cannot map from " + type + " to java map entry");
        }

        Collection properties = type.getProperties();
        if (m_container != null) { throw new IllegalStateException(); }

        for (Iterator it = properties.iterator(); it.hasNext(); ) {
            final Property pp = (Property) it.next();
            if (pp.isKeyProperty()) {
                if (KEY.equals(pp.getName())) {
                    m_key = pp;
                } else {
                    if (m_container == null) {
                        m_container = pp;
                        System.err.println("set container to " + pp +
                                           "(name=" + pp.getName() + "); " +
                                           "KEY=" + KEY +
                                           "; KEY==name: " + (KEY.equals(pp.getName())));
                    } else {
                        throw new IllegalStateException
                            ("can't happen: container already set to " +
                             m_container);
                    }
                }
            } else {
                if (m_value == null) {
                    m_value = pp;
                } else {
                    throw new IllegalStateException
                        ("found two non-key properties in type " + type + ": " +
                         m_value + " and " + pp);
                }
            }
        }
    }

    Session ssn() {
        if (m_ssn == null) {
            PersistenceManagerImpl pmi = ((PersistenceManagerImpl) JDOHelper
                                          .getPersistenceManager(this));
            m_ssn = pmi.getSession();
            StateManagerImpl smi = pmi.getStateManager(this);
            PropertyMap pmap = smi.getPropertyMap();
            m_object = m_ssn.retrieve(pmap);
            ObjectType type = pmap.getObjectType();
            String name = smi.getPrefix() + "entries";
            m_mapProp = type.getProperty(name);
            if (m_mapProp == null) {
                throw new IllegalStateException("no " + name + " in " + type);
            }
            init();
        }

        return m_ssn;
    }

    private void lock() {
        C.lock(ssn(), m_object);
    }

    private Expression entries() {
        return new Get(new Literal(m_object), m_mapProp.getName());
    }

    private MapEntry create(Object key, Object value) {
        PropertyMap pmap = new PropertyMap(m_mapProp.getType());
        pmap.put(m_key, key);
        pmap.put(m_container, m_object);
        Adapter ad = ssn().getRoot().getAdapter(MapEntry.class);
        MapEntry entry = (MapEntry) ad.getObject
            (pmap.getObjectType().getBasetype(), pmap, ssn());
        ssn().create(entry);
        ssn().set(entry, m_value, value);
        return entry;
    }

    private Map.Entry getEntry(Object key) {
        Expression expr = new Filter
            (entries(), new Equals
             (new Variable(m_key.getName()), new Literal(key)));
        DataSet ds = new DataSet
            (m_ssn, new Signature(m_mapProp.getType()), expr);
        Cursor c = ds.getCursor();
        try {
            if (c.next()) {
                return (Map.Entry) c.get();
            } else {
                return null;
            }
        } finally {
            c.close();
        }
    }

    private class KeySet extends CRPCollection implements Set {

        Session ssn() {
            return CRPMap.this.m_ssn;
        }

        ObjectType type() {
            return m_key.getType();
        }

        Expression expression() {
            return new Get(entries(), m_key.getName());
        }

        public boolean remove(Object o) {
            CRPMap.this.remove(o);
            // XXX: figure out real result
            return true;
        }

        public boolean add(Object o) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            CRPMap.this.clear();
        }

    }

    // =========================================================================
    // Map interface
    // =========================================================================

    public Object get(Object key) {
        Map.Entry me = getEntry(key);
        return me == null ? null : me.getValue();
    }

    public Object put(Object key, Object value) {
        lock();
        Map.Entry me = getEntry(key);
        if (me == null) {
            me = create(key, value);
            return null;
        } else {
            return me.setValue(value);
        }
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public Set keySet() {
        return new KeySet();
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
        return keySet().contains(key);
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
