package com.redhat.persistence.oql;

import java.util.*;

/**
 * ConstraintNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/16 $
 **/

abstract class ConstraintNode extends Node {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/ConstraintNode.java#1 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private Map m_constrained = new HashMap();

    void constrain(Expression expr, Expression value) {
        m_constrained.put(expr, value);
    }

    void union(ConstraintNode node) {
        m_constrained.putAll(node.m_constrained);
    }

    Collection expressions() {
        return m_constrained.keySet();
    }

    private Iterator entries() {
        return m_constrained.entrySet().iterator();
    }

    private Object key(Object o1, Object o2) {
        // XXX: This serves as a pretty good substitute for tree
        // comparison although it is not entirely correct since for
        // example a + b should equal b + a, but won't by this test.
        return "(" + o1 + ", " + o2 + ")";
    }

    private Set keys() {
        Set keys = new HashSet();
        for (Iterator it = entries(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            keys.add(key(me.getKey(), me.getValue()));
        }
        return keys;
    }

    private void retainAll(Set mask) {
        for (Iterator it = entries(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            if (!mask.contains(key(me.getKey(), me.getValue()))) {
                it.remove();
            }
        }
    }

    void intersect(ConstraintNode node) {
        Set keys = keys();
        keys.retainAll(node.keys());
        union(node);
        retainAll(keys);
    }

    final boolean update() {
        int before = m_constrained.size();
        updateConstraints();
        int size = m_constrained.size();
        if (size < before) {
            throw new IllegalStateException
                ("constraint set smaller than before");
        }
        return size > before;
    }

    abstract void updateConstraints();

    public String toString() {
        return "" + m_constrained;
    }

}
