package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Generator
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #11 $ $Date: 2004/03/08 $
 **/

class Generator {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Generator.java#11 $ by $Author: rhs $, $DateTime: 2004/03/08 23:10:10 $";

    private static final Logger s_log = Logger.getLogger(Generator.class);

    private Root m_root;
    private List m_frames = new ArrayList();
    private Map m_queries = new HashMap();
    private LinkedList m_stack = new LinkedList();
    private MultiMap m_equalities = new MultiMap();
    private Set m_sufficient = new HashSet();
    private MultiMap m_uses = new MultiMap();
    private MultiMap m_null = new MultiMap();
    private MultiMap m_nonnull = new MultiMap();
    private Map m_substitutions = new HashMap();

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

    QFrame getConstraining(QFrame frame) {
        List conds = frame.getConditions();
        Set columns = new HashSet();
        Set frames = new HashSet();
        for (Iterator it = conds.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            addConstraining(e, frame, columns, frames);
        }
        if (columns.isEmpty() || !frame.isConstrained(columns)) {
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

    void equateAll(EquiSet equiset, QFrame frame) {
        for (Iterator it = frame.getConditions().iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            Set eqs = getEqualities(e);
            for (Iterator iter = eqs.iterator(); iter.hasNext(); ) {
                Equality eq = (Equality) iter.next();
                equiset.equate(eq.getLeft(), eq.getRight());
            }
        }
    }

    boolean isNullable(QFrame frame, List equalities, Set nonnulls) {
        List cols = new ArrayList();
        List ocols = new ArrayList();
        QFrame fframe = null;
        QFrame oframe = null;
        for (Iterator it = equalities.iterator(); it.hasNext(); ) {
            Equality eq = (Equality) it.next();
            QValue from;
            QValue other;
            if (frame.contains(eq.getLeft()) &&
                !frame.contains(eq.getRight())) {
                from = eq.getLeft();
                other = eq.getRight();
            } else if (frame.contains(eq.getRight()) &&
                       !frame.contains(eq.getLeft())) {
                from = eq.getRight();
                other = eq.getLeft();
            } else {
                return true;
            }
            if (oframe == null) {
                fframe = from.getFrame();
                oframe = other.getFrame();
            } else if (!oframe.equals(other.getFrame()) ||
                       !fframe.equals(from.getFrame())) {
                return true;
            }
            if (other.isNullable() && !nonnulls.contains(other)) {
                return true;
            }
            cols.add(from.getColumn());
            ocols.add(other.getColumn());
        }

        if (oframe == null) { return true; }
        if (oframe.isOuter()) { return true; }
        if (oframe.getTable() == null || fframe.getTable() == null) {
            return true;
        }

        String table = fframe.getTable();
        String otable = oframe.getTable();
        if (table.equals(otable) && cols.equals(ocols)
            && isConstrained(table, cols)) {
            // XXX: technically we should make sure none of the columns
            // are nullable
            return false;
        }

        Table t = m_root.getTable(table);
        if (t == null) { return true; }
        Table ot = m_root.getTable(otable);
        if (ot == null) { return true; }
        ForeignKey from = ot.getForeignKey(columns(ot, ocols));
        if (from == null) { return true; }
        UniqueKey to = t.getUniqueKey(columns(t, cols));
        if (to == null) { return true; }
        if (isNullable(to)) { return true; }
        if (isConnected(from, to)) {
            return false;
        }

        return true;
    }

    private boolean isConnected(ForeignKey from, UniqueKey to) {
        UniqueKey uk = from.getUniqueKey();
        if (uk.equals(to)) { return true; }
        ForeignKey fk = uk.getTable().getForeignKey(uk.getColumns());
        if (fk == null) { return false; }
        else { return isConnected(fk, to); }
    }

    private boolean isNullable(Constraint c) {
        return isNullable(c.getColumns());
    }

    private boolean isNullable(Column[] cols) {
        for (int i = 0; i < cols.length; i++) {
            if (cols[i].isNullable()) { return true; }
        }
        return false;
    }

    private Column[] columns(Table t, List cols) {
        Column[] result = new Column[cols.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = t.getColumn((String) cols.get(i));
        }
        return result;
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

}
