package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Generator
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #14 $ $Date: 2004/03/23 $
 **/

class Generator {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Generator.java#14 $ by $Author: rhs $, $DateTime: 2004/03/23 16:12:52 $";

    private static final Logger s_log = Logger.getLogger(Generator.class);

    private List m_framepool = new ArrayList();
    private Map m_queries = new HashMap();
    private LinkedList m_stack = new LinkedList();
    private Set m_boolean = new HashSet();
    private MultiMap m_equalities = new MultiMap();
    private Set m_sufficient = new HashSet();
    private MultiMap m_uses = new MultiMap();
    private MultiMap m_null = new MultiMap();
    private MultiMap m_nonnull = new MultiMap();
    private Map m_substitutions = new HashMap();

    private Root m_root;
    private List m_frames;

    Generator() {}

    void init(Root root) {
        m_root = root;
        m_frames = m_framepool.subList(0, 0);

        m_queries.clear();
        m_stack.clear();
        m_boolean.clear();
        m_equalities.clear();
        m_sufficient.clear();
        m_uses.clear();
        m_null.clear();
        m_nonnull.clear();
        m_substitutions.clear();
    }

    Root getRoot() {
        return m_root;
    }

    List getFrames() {
        return m_frames;
    }

    QFrame frame(Expression expr, ObjectType type) {
        int size = m_frames.size();
        if (size == m_framepool.size()) {
            m_framepool.add(new QFrame(this));
        }
        m_frames = m_framepool.subList(0, size + 1);
        QFrame result = (QFrame) m_frames.get(size);
        result.init(expr, type, peek());
        m_queries.put(expr, result);
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

    void addBoolean(Expression expr) {
        m_boolean.add(expr);
    }

    boolean isBoolean(Expression expr) {
        return m_boolean.contains(expr);
    }

    Set getEqualities(Expression expr) {
        return m_equalities.get(expr);
    }

    void addEquality(Expression expr, QValue a, QValue b) {
        m_equalities.add(expr, new Equality(a, b));
    }

    void addEqualities(Expression expr, Collection equalities) {
        m_equalities.addAll(expr, equalities);
    }

    boolean isSufficient(Expression expr) {
        return m_sufficient.contains(expr);
    }

    void addSufficient(Expression expr) {
        m_sufficient.add(expr);
    }

    Set getUses(Expression expr) {
        return m_uses.get(expr);
    }

    void addUse(Expression expr, QValue v) {
        m_uses.add(expr, v);
    }

    void addUses(Expression expr, Collection values) {
        m_uses.addAll(expr, values);
    }

    Set getNull(Expression expr) {
        return m_null.get(expr);
    }

    void addNull(Expression expr, QValue v) {
        m_null.add(expr, v);
    }

    void addNulls(Expression expr, Collection values) {
        m_null.addAll(expr, values);
    }

    Set getNonNull(Expression expr) {
        return m_nonnull.get(expr);
    }

    void addNonNull(Expression expr, QValue v) {
        m_nonnull.add(expr, v);
    }

    void addNonNulls(Expression expr, Collection values) {
        m_nonnull.addAll(expr, values);
    }

    void setSubstitute(Expression expr, Expression substitute) {
        m_substitutions.put(expr, substitute);
    }

    Expression getSubstitute(Expression expr) {
        return (Expression) m_substitutions.get(expr);
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

    private Set m_ccolumns = new HashSet();
    private Set m_cframes = new HashSet();
    private List m_cconds = new ArrayList();

    QFrame getConstraining(QFrame frame) {
        m_cconds.clear();
        m_ccolumns.clear();
        m_cframes.clear();
        frame.addConditions(m_cconds);
        for (Iterator it = m_cconds.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            addConstraining(e, frame, m_ccolumns, m_cframes);
        }
        if (m_ccolumns.isEmpty() || !frame.isConstrained(m_ccolumns)) {
            return null;
        }
        return frame.getContainer();
    }

    private void addConstraining(Expression e, QFrame frame, Set columns,
                                 Set frames) {
        Set equalities = getEqualities(e);
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
            columns.add(other.getColumn());
            frames.add(ext);
        }
    }

    boolean isConstrained(String table, Collection columns) {
        Table t = m_root.getTable(table);
        if (t == null) { return false; }
        outer: for (Iterator it = t.getConstraints().iterator();
                    it.hasNext(); ) {
            Object o = it.next();
            if (o instanceof UniqueKey) {
                UniqueKey key = (UniqueKey) o;
                Column[] cols = key.getColumns();
                for (int i = 0; i < cols.length; i++) {
                    if (!columns.contains(cols[i].getName())) {
                        continue outer;
                    }
                }
                return true;
            }
        }
        return false;
    }

    void equate(EquiSet equiset, Expression e) {
        Set eqs = getEqualities(e);
        for (Iterator it = eqs.iterator(); it.hasNext(); ) {
            Equality eq = (Equality) it.next();
            equiset.equate(eq.getLeft(), eq.getRight());
        }
    }

    void split(QFrame frame, List equalities, List from, List to) {
        for (int i = 0; i < equalities.size(); i++) {
            Equality eq = (Equality) equalities.get(i);
            QValue left = eq.getLeft();
            QValue right = eq.getRight();
            if (frame.contains(left) && frame.contains(right)) {
                // it's inernal, we don't care about it
                continue;
            }
            if (frame.contains(left)) {
                from.add(left);
                to.add(right);
            } else if (frame.contains(right)) {
                from.add(right);
                to.add(left);
            } else {
                // not sure what this case means
                continue;
            }
        }
    }

}
