package com.redhat.persistence.oql;

import java.util.*;

/**
 * ConstraintNode
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/19 $
 **/

abstract class ConstraintNode extends Node {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/ConstraintNode.java#2 $ by $Author: rhs $, $DateTime: 2004/01/19 14:43:24 $";

    private Map m_constrained = new HashMap();
    private Map m_panes = new HashMap();

    void constrain(Pane expr, Pane value) {
        m_constrained.put(expr.expression, value.expression);
        m_panes.put(expr.expression, expr);
        m_panes.put(value.expression, value);
    }

    Pane getPane(Expression expr) {
        return (Pane) m_panes.get(expr);
    }

    void union(ConstraintNode node) {
        m_constrained.putAll(node.m_constrained);
        m_panes.putAll(node.m_panes);
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
        // XXX: this is no longer a valid way to do intersection
        // because the expressions involved can now come from
        // different frames. We need to add a check here that the
        // variables each expression depends on also resolve to the
        // same values.

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
