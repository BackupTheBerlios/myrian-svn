package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Generator
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/02/21 $
 **/

class Generator {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Generator.java#1 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    private static final Logger s_log = Logger.getLogger(Generator.class);

    private Root m_root;
    private List m_frames = new ArrayList();
    private Map m_queries = new HashMap();
    private LinkedList m_stack = new LinkedList();
    private Map m_equalities = new HashMap();

    Generator(Root root) {
        m_root = root;
    }

    Root getRoot() {
        return m_root;
    }

    List getFrames() {
        return m_frames;
    }

    QFrame frame(Expression expr, ObjectType type) {
        QFrame result = new QFrame(this, expr, type, peek());
        m_queries.put(expr, result);
        m_frames.add(result);
        return result;
    }

    QFrame getFrame(Expression e) {
        QFrame result = (QFrame) m_queries.get(e);
        if (result == null) {
            throw new IllegalStateException
                ("no qframe for expression: " + e);
        } else {
            return result;
        }
    }

    boolean hasFrame(Expression e) {
        return m_queries.containsKey(e);
    }

    void push(QFrame frame) {
        m_stack.addFirst(frame);
    }

    QFrame peek() {
        if (m_stack.isEmpty()) {
            return null;
        } else {
            return (QFrame) m_stack.getFirst();
        }
    }

    QFrame pop() {
        return (QFrame) m_stack.removeFirst();
    }

    QFrame resolve(String name) {
        for (Iterator it = m_stack.iterator(); it.hasNext(); ) {
            QFrame frame = (QFrame) it.next();
            if (frame.getType().hasProperty(name)) {
                return frame;
            }
        }

        throw new IllegalArgumentException
            ("unable to resolve variable: " + name + "\n" + getTrace());
    }

    String getTrace() {
        StringBuffer result = new StringBuffer();
        for (Iterator it = m_stack.iterator(); it.hasNext(); ) {
            QFrame frame = (QFrame) it.next();
            result.append(frame.getType());
            if (it.hasNext()) {
                result.append("  \n");
            }
        }
        return result.toString();
    }

    boolean hasType(String name) {
        return m_root.getObjectType(name) != null;
    }

    ObjectType getType(String name) {
        ObjectType result = m_root.getObjectType(name);
        if (result == null) {
            throw new IllegalArgumentException
                ("unable to resolve type: " + name);
        }
        return result;
    }

    List getEqualities(Expression expr) {
        return (List) m_equalities.get(expr);
    }

    void addEquality(Expression expr, QValue a, QValue b) {
        List equalities = getEqualities(expr);
        if (equalities == null) {
            equalities = new ArrayList();
            m_equalities.put(expr, equalities);
        }
        equalities.add(new Equality(a, b));
    }

    void unionEqualities(Expression expr, Expression a, Expression b) {
        List equalities = new ArrayList();
        Expression[] exprs = new Expression[] {a, b};
        for (int i = 0; i < exprs.length; i++) {
            List quals = getEqualities(exprs[i]);
            if (quals != null) {
                equalities.addAll(quals);
            }
        }
        m_equalities.put(expr, equalities);
    }

    private static class Equality {
        private QValue m_left;
        private QValue m_right;
        Equality(QValue left, QValue right) {
            m_left = left;
            m_right = right;
        }
        QValue getLeft() {
            return m_left;
        }
        QValue getRight() {
            return m_right;
        }
        QValue getValue(QFrame frame) {
            if (m_left.getFrame().equals(frame)) {
                return m_left;
            } else if (m_right.getFrame().equals(frame)) {
                return m_right;
            } else {
                return null;
            }
        }
        QValue getExternal(QFrame frame) {
            QFrame root = frame.getRoot();
            if (m_left.getFrame().getRoot().equals(root)) {
                if (m_right.getFrame().getRoot().equals(root)) {
                    return null;
                } else {
                    return m_right;
                }
            } else {
                return m_left;
            }
        }
        QValue getOther(QValue value) {
            if (m_left.equals(value)) {
                return m_right;
            } else {
                return m_left;
            }
        }

        public String toString() {
            return "<equality " + m_left + " = " + m_right + ">";
        }
    }

    QFrame getConstraining(QFrame frame) {
        List conds = frame.getConditions();
        List values = new ArrayList();
        Set frames = new HashSet();
        for (Iterator it = conds.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            addConstraining(e, frame, values, frames);
        }
        if (!frame.isConstrained(values)) {
            return null;
        }
        Set roots = new HashSet();
        for (Iterator it = frames.iterator(); it.hasNext(); ) {
            QFrame f = (QFrame) it.next();
            roots.add(f.getRoot());
        }

        // if there is more than one root we need to pick the most
        // nested frame
        for(QFrame c = frame.getContainer(); c != null; c = c.getContainer()) {
            QFrame root = c.getRoot();
            if (roots.contains(root)) {
                return root;
            }
        }

        return null;
    }

    private void addConstraining(Expression e, QFrame frame, List values,
                                 Set frames) {
        List equalities = getEqualities(e);
        if (equalities == null) { return; }
        for (Iterator it = equalities.iterator(); it.hasNext(); ) {
            Equality eq = (Equality) it.next();
            QValue external = eq.getExternal(frame);
            if (external == null) { continue; }
            QFrame ext = external.getFrame();
            // We're already part of the same frame
            if (ext.getRoot().equals(frame.getRoot())) {
                continue;
            }
            // Conditions that correlate to our own subqueries don't count
            if (frame.isSubframe(ext)) {
                continue;
            }
            QValue other = eq.getOther(external);
            values.add(other);
            frames.add(ext);
        }
    }

    List getDuplicates(QFrame frame) {
        List conds = frame.getRoot().getConditions();
        MultiMap values = new MultiMap();
        for (Iterator it = conds.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            addDuplicates(e, frame, values);
        }
        List result = new ArrayList();
        for (Iterator it = values.keys().iterator(); it.hasNext(); ) {
            QFrame qf = (QFrame) it.next();
            if (frame.isConstrained(values.get(qf))) {
                result.add(qf);
            }
        }
        return result;
    }

    void addDuplicates(Expression e, QFrame frame, MultiMap values) {
        List equalities = getEqualities(e);
        if (equalities == null) { return; }
        for (Iterator it = equalities.iterator(); it.hasNext(); ) {
            Equality eq = (Equality) it.next();
            QValue me = eq.getValue(frame);
            if (me == null) { continue; }
            QValue other = eq.getOther(me);
            if (me.getTable() == null) { continue; }
            if (me.getTable().equals(other.getTable()) &&
                me.getColumn().equals(other.getColumn())) {
                values.add(other.getFrame(), me);
            }
        }
    }

}
