package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.oql.*;
import com.redhat.persistence.oql.Expression;

import java.util.*;

/**
 * CRPMap
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/07/08 $
 **/

class CRPMap implements Map {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/jdo/CRPMap.java#3 $ by $Author: vadim $, $DateTime: 2004/07/08 16:34:10 $";

    private Session m_ssn;
    private Object m_object;
    private Property m_property;
    private Property m_container;
    private Property m_key;
    private Property m_value;

    CRPMap() {}

    CRPMap(Session ssn, Object object, Property property) {
        m_ssn = ssn;
        m_object = object;
        m_property = property;

        ObjectType type = property.getType();
        m_key = null;
        m_value = null;
        for (Iterator it = type.getProperties().iterator(); it.hasNext(); ) {
            Property p = (Property) it.next();
            if (p.isKeyProperty()) {
                if (m_property.equals(((Role) p).getReverse())) {
                    if (m_container == null) {
                        m_container = p;
                    } else {
                        throw new IllegalStateException
                            ("ambiguously defined container property " +
                             " for map element: " + type);
                    }
                } else {
                    if (m_key == null) {
                        m_key = p;
                    } else {
                        throw new IllegalStateException
                            ("ambiguously defined key property " +
                             " for map element: " + type);
                    }
                }
            } else {
                if (m_value == null) {
                    m_value = p;
                } else {
                    throw new IllegalStateException
                        ("ambiguously defined value property " +
                         "for map element: " + type);
                }
            }
        }
    }

    private Expression entries() {
        return new Get(new Literal(m_object), m_property.getName());
    }

    public Set entrySet() {
        return new CRPSet(m_ssn, m_object, m_property);
    }

    public Collection values() {
        return new ValueCollection();
    }

    public Set keySet() {
        return new KeySet();
    }

    public void clear() {
        entrySet().clear();
    }

    public void putAll(Map map) {
        for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            put(me.getKey(), me.getValue());
        }
    }

    public Object remove(Object o) {
        Map.Entry me = getEntry(o);
        if (me == null) {
            return null;
        } else {
            Object result = me.getValue();
            m_ssn.delete(me);
            return result;
        }
    }

    public Object put(Object key, Object value) {
        Map.Entry me = getEntry(key);
        if (me == null) {
            me = create(key);
        }
        Object result = me.getValue();
        me.setValue(value);
        return result;
    }

    private Map.Entry create(Object key) {
        PropertyMap pmap = new PropertyMap(m_property.getType());
        pmap.put(m_key, key);
        pmap.put(m_container, m_object);
        Adapter ad = m_ssn.getRoot().getAdapter(Map.Entry.class);
        Map.Entry me = (Map.Entry) ad.getObject
            (pmap.getObjectType().getBasetype(), pmap, m_ssn);
        m_ssn.create(me);
        return me;
    }

    private Map.Entry getEntry(Object key) {
        Expression expr = new Filter
            (entries(), new Equals
             (new Variable(m_key.getName()), new Literal(key)));
        DataSet ds = new DataSet
            (m_ssn, new Signature(m_property.getType()), expr);
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

    public Object get(Object key) {
        Map.Entry me = getEntry(key);
        if (me == null) {
            return null;
        } else {
            return me.getValue();
        }
    }

    public boolean containsValue(Object o) {
        return values().contains(o);
    }

    public boolean containsKey(Object o) {
        return keySet().contains(o);
    }

    public boolean isEmpty() {
        return entrySet().isEmpty();
    }

    public int size() {
        return entrySet().size();
    }

    private class ValueCollection extends CRPCollection {

        Session ssn() {
            return CRPMap.this.m_ssn;
        }

        ObjectType type() {
            return m_value.getType();
        }

        Expression expression() {
            return new Get(entries(), m_value.getName());
        }

        public boolean remove(Object o) {
            throw new UnsupportedOperationException
                ("technically this should work");
        }

        public boolean add(Object o) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            CRPMap.this.clear();
        }

    };

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

}
