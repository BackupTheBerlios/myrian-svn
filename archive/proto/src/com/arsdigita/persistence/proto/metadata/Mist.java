package com.arsdigita.persistence.proto.metadata;

import java.util.*;

/**
 * Mist
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/15 $
 **/

class Mist implements Collection {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Mist.java#2 $ by $Author: rhs $, $DateTime: 2003/01/15 17:57:03 $";

    private Object m_parent = null;
    private ArrayList m_children = new ArrayList();
    private HashMap m_childrenMap = new HashMap();

    public Mist(Object parent) {
        m_parent = parent;
    }

    public boolean add(Object o) {
        if (o == null) {
            throw new IllegalArgumentException
                ("null child");
        }

        if (!(o instanceof Element)) {
            throw new IllegalArgumentException
                ("not an element");
        }

        Element child = (Element) o;
        Object key = child.getKey();

        if (key == null) {
            throw new IllegalArgumentException
                ("null key");
        }

        if (child.getParent() != null) {
            throw new IllegalArgumentException
                ("child is already contained");
        }

        if (m_childrenMap.containsKey(key)) {
            throw new IllegalArgumentException
                ("duplicate key");
        }

        m_children.add(child);
        m_childrenMap.put(key, child);
        child.setParent(m_parent);
        return true;
    }

    public boolean addAll(Collection c) {
        boolean result = false;
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            if (add(it.next())) {
                result = true;
            }
        }
        return result;
    }

    public int size() {
        return m_children.size();
    }

    public Iterator iterator() {
        return m_children.iterator();
    }

    public boolean isEmpty() {
        return m_children.isEmpty();
    }

    public boolean contains(Object o) {
        return m_children.contains(o);
    }

    public boolean containsAll(Collection c) {
        return m_children.containsAll(c);
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {
        return m_children.toArray();
    }

    public Object[] toArray(Object[] a) {
        return m_children.toArray(a);
    }

    public Object get(Object key) {
        return m_childrenMap.get(key);
    }

    public boolean containsKey(Object key) {
        return m_childrenMap.containsKey(key);
    }

    public String toString() {
        return m_children.toString();
    }

}