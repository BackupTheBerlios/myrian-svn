package com.redhat.persistence.oql;

import java.util.*;

/**
 * KeyNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

abstract class KeyNode extends Node {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/KeyNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private Set m_keys = new HashSet();

    Set keys() {
        return Collections.unmodifiableSet(m_keys);
    }

    boolean add(Collection key) {
        return m_keys.add(Collections.unmodifiableSet(new HashSet(key)));
    }

    boolean contains(Collection key) {
        return m_keys.contains(Collections.unmodifiableSet(new HashSet(key)));
    }

    boolean addAll(KeyNode node) {
        return m_keys.addAll(node.m_keys);
    }

    boolean addAll(Collection keys) {
        boolean result = false;
        for (Iterator it = keys.iterator(); it.hasNext(); ) {
            Collection key = (Collection) it.next();
            result |= add(key);
        }
        return result;
    }

    boolean isEmpty() {
        return m_keys.isEmpty();
    }

    int size() {
        return m_keys.size();
    }

    boolean update() {
        int before = m_keys.size();
        updateKeys();
        int size = m_keys.size();
        if (size < before) {
            throw new IllegalStateException("key node shrunk");
        }
        return size > before;
    }

    abstract void updateKeys();

    public String toString() {
        return "" + m_keys;
    }

}
