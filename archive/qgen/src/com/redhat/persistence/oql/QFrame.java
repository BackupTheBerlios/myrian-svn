package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * QFrame
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/02/24 $
 **/

class QFrame {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/QFrame.java#3 $ by $Author: rhs $, $DateTime: 2004/02/24 10:13:24 $";

    private static final Logger s_log = Logger.getLogger(QFrame.class);

    private Generator m_generator;
    private Expression m_expression;
    private ObjectType m_type;
    private QFrame m_container;

    private boolean m_outer = false;
    private List m_values = null;
    private String m_table = null;
    private Expression m_tableExpr = null;
    private QFrame m_parent = null;
    private List m_children = new ArrayList();
    private Expression m_condition = null;
    private Expression m_order = null;
    private boolean m_asc = true;
    private Expression m_limit = null;
    private Expression m_offset = null;
    private boolean m_hoisted = false;
    private QFrame m_alias = null;

    QFrame(Generator generator, Expression expression, ObjectType type,
           QFrame container) {
        m_generator = generator;
        m_expression = expression;
        m_type = type;
        m_container = container;
    }

    Generator getGenerator() {
        return m_generator;
    }

    Expression getExpression() {
        return m_expression;
    }

    ObjectType getType() {
        return m_type;
    }

    QFrame getContainer() {
        return m_container;

    }

    void setOuter(boolean outer) {
        m_outer = outer;
    }

    boolean isOuter() {
        return m_outer || (m_parent != null && m_parent.isOuter());
    }

    void setValues(String[] columns) {
        m_values = new ArrayList();
        for (int i = 0; i < columns.length; i++) {
            m_values.add(new QValue(this, columns[i]));
        }
    }

    void setValues(List values) {
        m_values = values;
    }

    List getValues() {
        return m_values;
    }

    void setTable(String table) {
        m_table = table;
    }

    String getTable() {
        return m_table;
    }

    void setTable(Expression expr) {
        m_tableExpr = expr;
    }

    void addChild(QFrame child) {
        m_children.add(child);
        child.m_parent = this;
    }

    void addChild(int index, QFrame child) {
        m_children.add(index, child);
        child.m_parent = this;
    }

    QFrame getChild(int index) {
        return (QFrame) m_children.get(index);
    }

    List getChildren() {
        return m_children;
    }

    QFrame getParent() {
        return m_parent;
    }

    QFrame getRoot() {
        if (m_parent == null) {
            return this;
        } else {
            return m_parent.getRoot();
        }
    }

    void setCondition(Expression condition) {
        m_condition = condition;
    }

    Expression getCondition() {
        return m_condition;
    }

    void setOrder(Expression order, boolean asc) {
        m_order = order;
        m_asc = asc;
    }

    void setLimit(Expression limit) {
        m_limit = limit;
    }

    Expression getLimit() {
        return m_limit;
    }

    void setOffset(Expression offset) {
        m_offset = offset;
    }

    Expression getOffset() {
        return m_offset;
    }

    String alias() {
        if (m_alias != null) { return m_alias.alias(); }
        return "t" + m_generator.getFrames().indexOf(this);
    }

    String emit() {
        return emit(true);
    }

    String emit(boolean select) {
        List inner = new ArrayList();
        MultiMap outer = new MultiMap();
        List where = new ArrayList();

        flatten(inner, outer, where);

        StringBuffer buf = new StringBuffer();
        if (select) {
            if (!inner.isEmpty()) {
                buf.append("(select ");
            } else if (m_values.size() > 1) {
                buf.append("(");
            }
            for (Iterator it = m_values.iterator(); it.hasNext(); ) {
                buf.append(it.next());
                if (it.hasNext()) {
                    buf.append(", ");
                }
            }
            if (m_values.isEmpty()) {
                buf.append("1");
            }
        }

        if (select && !inner.isEmpty()) {
            buf.append("\nfrom ");
        }

        for (Iterator it = inner.iterator(); it.hasNext(); ) {
            QFrame qf = (QFrame) it.next();
            buf.append(qf.join());
            if (it.hasNext()) {
                if (outer.isEmpty()) {
                    buf.append(", ");
                } else {
                    buf.append("\ncross join ");
                }
            }
        }

        for (Iterator it = outer.keys().iterator(); it.hasNext(); ) {
            QFrame qf = (QFrame) it.next();
            List on = outer.get(qf);
            buf.append("\nleft join ");
            buf.append(qf.join());
            buf.append(" on ");
            buf.append(join(on, " and "));
        }

        if (!where.isEmpty()) {
            buf.append("\nwhere ");
            buf.append(join(where, "\nand "));
        }

        List orders = getOrders();
        if (!orders.isEmpty()) {
            buf.append("\norder by ");
        }
        for (Iterator it = orders.iterator(); it.hasNext(); ) {
            buf.append(it.next());
            if (it.hasNext()) {
                buf.append(", ");
            }
        }

        // XXX: nested offsets and limits are ignored
        if (m_offset != null) {
            buf.append("\noffset ");
            buf.append(m_offset.emit(m_generator));
        }

        if (m_limit != null) {
            buf.append("\nlimit ");
            buf.append(m_limit.emit(m_generator));
        }

        if (select && (!inner.isEmpty() || m_values.size() > 1)) {
            buf.append(")");
        }

        return buf.toString();
    }

    private String join() {
        if (m_table != null && m_alias == null) {
            return m_table + " " + alias();
        } else if (m_tableExpr != null) {
            return m_tableExpr.emit(m_generator) + " " + alias();
        } else {
            throw new IllegalStateException
                ("not a source frame: " + this);
        }
    }

    private String join(List exprs, String sep) {
        StringBuffer buf = new StringBuffer();
        for (Iterator it = exprs.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            buf.append(e.emit(m_generator));
            if (it.hasNext()) {
                buf.append(sep);
            }
        }
        return buf.toString();
    }

    private void fill(List joins, List conditions, List orders) {
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            child.fill(joins, conditions, orders);
        }
        if (m_table != null && m_alias == null) {
            joins.add(m_table + " " + alias());
        } else if (m_tableExpr != null) {
            joins.add(m_tableExpr.emit(m_generator) + " " + alias());
        }
        if (m_condition != null) {
            conditions.add(m_condition);
        }
        if (m_order != null) {
            String order = m_order.emit(m_generator);
            if (!m_asc) {
                order = order + " desc";
            }
            orders.add(0, order);
        }
    }

    private List getOrders() {
        List result = new ArrayList();
        addOrders(result);
        return result;
    }

    private void addOrders(List result) {
        if (m_order != null) {
            String order = m_order.emit(m_generator);
            if (!m_asc) {
                order = order + " desc";
            }
            result.add(order);
        }
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            child.addOrders(result);
        }
    }

    private void addInner(List inner) {
        if (m_table != null || m_tableExpr != null) {
            QFrame frame = getAlias();
            if (!inner.contains(frame)) {
                inner.add(frame);
            }
        }
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (child.isOuter()) { continue; }
            child.addInner(inner);
        }
    }

    void flatten(List inner, MultiMap outer, List where) {
        if (m_hoisted) { return; }
        addInner(inner);

        List remaining = new ArrayList();
        addConditions(where, remaining);

        while (true) {
            QFrame frame = null;
            List on = new ArrayList();

            for (Iterator it = remaining.iterator(); it.hasNext(); ) {
                Expression e = (Expression) it.next();
                List uses = m_generator.getUses(e);
                if (uses.isEmpty()) { continue; }
                Set frames = new HashSet();
                for (Iterator iter = uses.iterator(); iter.hasNext(); ) {
                    QValue value = (QValue) iter.next();
                    QFrame qf = value.getFrame().getAlias();
                    frames.add(qf);
                }
                frames.removeAll(inner);
                if (frames.isEmpty()) {
                    where.add(e);
                    it.remove();
                    continue;
                }
                frames.removeAll(outer.keys());
                if (frames.size() != 1) {
                    // condition requires more than one table (or no
                    // tables), so it can't be next

                    // XXX: in order to be fully correct here we might
                    // need to consider the possibility of factoring
                    // conditions, for example if we know that a
                    // condition imposes an equality constraint we may
                    // render that equality constraint as part of the
                    // join and later eliminate the portion that is
                    // part of the actual condition.
                    continue;
                }
                QFrame qf = (QFrame) frames.iterator().next();
                if (frame == null) {
                    frame = qf;
                } else if (!frame.equals(qf)) {
                    continue;
                }
                on.add(e);
                it.remove();
            }

            if (frame == null) { break; }
            outer.addAll(frame, on);
        }

        if (remaining.size() > 0) {
            throw new IllegalStateException
                ("unable to form outer joins on: " + remaining +
                 "\nframe: " + this +
                 "\ninner: " + inner +
                 "\nouter: " + outer +
                 "\nwhere: " + where);
        }
    }

    List getConditions() {
        List result = new ArrayList();
        addConditions(result, result);
        return result;
    }

    private void addConditions(List inner, List outer) {
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            child.addConditions(inner, outer);
        }
        if (m_condition != null) {
            if (isOuter()) {
                outer.add(m_condition);
            } else {
                inner.add(m_condition);
            }
        }
    }

    boolean isSubframe(QFrame f) {
        QFrame root = getRoot();
        for (QFrame c = f.getContainer(); c != null; c = c.getContainer()) {
            if (c.getRoot().equals(root)) {
                return true;
            }
        }
        return false;
    }

    boolean isDescendant(QFrame frame) {
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (child.equals(frame)) { return true; }
            if (child.isDescendant(frame)) { return true; }
        }
        return false;
    }

    boolean isSelect() {
        List inner = new ArrayList();
        MultiMap outer = new MultiMap();
        List where = new ArrayList();
        flatten(inner, outer, where);
        return !inner.isEmpty();
    }

    boolean hoist() {
        // Rather than this m_select business we could construct
        // another QFrame, copy children, condition, etc to it and
        // remove our own children.
        QFrame frame = m_generator.getConstraining(this);
        if (frame == null) { return false; }
        if (m_parent != null) {
            m_parent.m_children.remove(this);
        }
        frame.addChild(this);
        m_hoisted = true;
        setOuter(true);
        return true;
    }

    boolean isConstrained(List values) {
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (!child.isConstrained(values)) { return false; }
        }
        if (m_table != null) {
            if (!isConstrained(m_table, getColumns(values))) { return false; }
        }
        if (m_tableExpr != null) { return false; }
        return true;
    }

    private Set getColumns(List values) {
        Set result = new HashSet();
        for (Iterator it = values.iterator(); it.hasNext(); ) {
            QValue value = (QValue) it.next();
            result.add(value.getColumn());
        }
        return result;
    }

    private boolean isConstrained(String table, Collection columns) {
        Table t = m_generator.getRoot().getTable(table);
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

    void shrink() {
        Set frames = new HashSet();
        shrink(frames);
    }

    private void shrink(Set frames) {
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            child.shrink(frames);
        }

        if (m_table != null) {
            List dups = m_generator.getDuplicates(this);
            dups.retainAll(frames);
            if (dups.size() > 1) {
                throw new IllegalStateException("Hmm, eeenteresting...");
            }
            if (dups.size() > 0) {
                setAlias((QFrame) dups.get(0));
            }
            frames.add(this);
        }
    }

    private void setAlias(QFrame target) {
        m_alias = target;
    }

    QFrame getAlias() {
        if (m_alias == null) { return this; }
        return m_alias.getAlias();
    }

    public String toString() {
        return toString(0);
    }

    private static void indent(StringBuffer buf, int depth) {
        for (int i = 0; i < depth; i++) {
            buf.append("  ");
        }
    }

    private String toString(int depth) {
        StringBuffer result = new StringBuffer();
        indent(result, depth);
        result.append("frame ");
        result.append(isOuter() ? "0" : "I");
        result.append(m_outer ? "o" : "i");
        result.append(" ");
        result.append(m_expression.summary());
        result.append(" ");
        result.append(m_type);
        if (m_table != null) {
            result.append(" ");
            result.append(m_table);
            result.append(" ");
            result.append(alias());
        }
        if (m_values != null) {
            result.append(" ");
            result.append(m_values);
        }
        if (m_condition != null) {
            result.append(" cond ");
            result.append(m_condition);
        }
        if (m_children.isEmpty()) {
            return result.toString();
        }
        result.append(" {");
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            result.append("\n");
            result.append(child.toString(depth + 1));
        }
        result.append("\n");
        indent(result, depth);
        result.append("}");
        return result.toString();
    }

}
