package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * QFrame
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #9 $ $Date: 2004/03/02 $
 **/

class QFrame {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/QFrame.java#9 $ by $Author: rhs $, $DateTime: 2004/03/02 10:09:25 $";

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
        return emit(true, true);
    }

    String emit(boolean select, boolean range) {
        List where = new ArrayList();
        Set emitted = new HashSet();
        String join = null;
        if (!m_hoisted) {
            join = render(where, emitted);
        }

        StringBuffer buf = new StringBuffer();
        if (select) {
            if (join != null) {
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

        if (select && join != null) {
            buf.append("\nfrom ");
        }

        if (join != null) {
            buf.append(join);
        }

        String sql = join(where, "\nand ", emitted);
        if (!sql.equals("")) {
            buf.append("\nwhere ");
            buf.append(sql);
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

        if (range) {
            // XXX: nested offsets and limits are ignored
            if (m_offset != null) {
                buf.append("\noffset ");
                buf.append(m_offset.emit(m_generator));
            }

            if (m_limit != null) {
                buf.append("\nlimit ");
                buf.append(m_limit.emit(m_generator));
            }
        }

        if (select && (join != null || m_values.size() > 1)) {
            buf.append(")");
        }

        return buf.toString();
    }

    private String join(List exprs, String sep, Set emitted) {
        List conditions = new ArrayList();
        for (Iterator it = exprs.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            String sql = e.emit(m_generator);
            if (!Code.TRUE.equals(sql) && !emitted.contains(sql)) {
                conditions.add(sql);
                emitted.add(sql);
            }
        }
        return Code.join(conditions, sep);
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

    private String render(List conditions, Set emitted) {
        return render(conditions, new HashSet(), emitted);
    }

    private String render(List conditions, Set defined, Set emitted) {
        StringBuffer result = new StringBuffer();

        Set defs = new HashSet();

        boolean first = true;
        if (m_table != null && m_alias == null) {
            first = false;
            result.append(m_table + " " + alias());
            defs.add(this);
        } else if (m_tableExpr != null && m_alias == null) {
            first = false;
            result.append(m_tableExpr.emit(m_generator) + " " + alias());
            defs.add(this);
        }

        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            List conds = new ArrayList();
            String join = child.render(conds, defs, emitted);
            /*if (join == null && !conds.isEmpty()) {
                throw new IllegalStateException
                    ("Condition without joins: " + conds +
                     "\nchild: " + child +
                     "\nthis: " + this);
                     }*/
            if (join == null) {
                conditions.addAll(conds);
                continue;
            }
            for (Iterator iter = conds.iterator(); iter.hasNext(); ) {
                Expression e = (Expression) iter.next();
                Set used = frames(e);
                used.removeAll(defs);
                if (!used.isEmpty()) {
                    /*if (child.m_outer) {
                        throw new IllegalStateException
                            ("Deferring outer join condition:" +
                             "\nroot: " + getRoot() +
                             "\nchild: " + child +
                             "\nthis: " + this +
                             "\ncond: " + e);
                             }*/
                    conditions.add(e);
                    iter.remove();
                }
            }
            if (!first) {
                result.append("\n");
                if (child.m_outer) {
                    if (conds.isEmpty()) {
                        throw new IllegalStateException
                            ("Outer join on nothing: " + join +
                             "\nroot: " + getRoot() +
                             "\nchild: " + child +
                             "\nthis: " + this);
                    }
                    result.append("left ");
                } else if (conds.isEmpty()) {
                    result.append("cross ");
                }
                result.append("join ");
            }
            result.append(join);
            if (!conds.isEmpty()) {
                if (first) {
                    if (child.m_outer) {
                        throw new IllegalStateException
                            ("Propogating outer join conditions:" +
                             "\nroot: " + getRoot() +
                             "\nchild: " + child +
                             "\nthis: " + this +
                             "\nconds: " + conds);
                    }
                    conditions.addAll(conds);
                } else {
                    result.append(" on ");
                    result.append(join(conds, " and ", emitted));
                }
            }
            first = false;
        }

        if (m_condition != null) {
            conditions.add(m_condition);
        }

        defined.addAll(defs);

        if (first) {
            return null;
        } else {
            return result.toString();
        }
    }

    Set frames(Expression e) {
        return frames(m_generator.getUses(e));
    }

    Set frames(List values) {
        Set result = new HashSet();
        for (Iterator it = values.iterator(); it.hasNext(); ) {
            QValue value = (QValue) it.next();
            result.add(value.getFrame().getAlias());
        }
        return result;
    }

    List getConditions() {
        List result = new ArrayList();
        addConditions(result);
        return result;
    }

    private void addConditions(List result) {
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            child.addConditions(result);
        }
        if (m_condition != null) {
            result.add(m_condition);
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
        if (m_hoisted) {
            return false;
        } else if (render(new ArrayList(), new HashSet()) == null) {
            return false;
        } else {
            return true;
        }
    }

    boolean hoist() {
        // XXX: Rather than this m_select business we could construct
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

    boolean innerize() {
        boolean modified = false;
        if (m_condition != null && !m_outer) {
            List nonnulls = m_generator.getNonNull(m_condition);
            Set frames = frames(nonnulls);
            modified = innerize(frames);
        }
        if (m_outer) {
            List conditions = getConditions();
            List equals = new ArrayList();
            for (Iterator it = conditions.iterator(); it.hasNext(); ) {
                Expression c = (Expression) it.next();
                if (!m_generator.isSufficient(c)) {
                    equals = null;
                    break;
                }
                equals.addAll(m_generator.getEqualities(c));
            }
            if (equals != null) {
                if (!m_generator.isNullable(this, equals)) {
                    m_outer = false;
                    modified = true;
                }
            }
        }
        return modified;
    }

    boolean innerize(Set frames) {
        boolean modified = false;
        if (m_outer && containsAny(frames)) {
            m_outer = false;
            modified = true;
        }
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (child.innerize(frames)) {
                modified = true;
            }
        }
        return modified;
    }

    private boolean containsAny(Set frames) {
        if (frames.contains(this)) { return true; }
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (child.containsAny(frames)) {
                return true;
            }
        }
        return false;
    }

    boolean contains(QValue value) {
        if (value.getFrame().equals(this)) { return true; }
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (child.contains(value)) { return true; }
        }
        return false;
    }

    boolean isConstrained(List values) {
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (!child.isConstrained(values)) { return false; }
        }
        if (m_table != null) {
            if (!m_generator.isConstrained(m_table, getColumns(values))) {
                return false;
            }
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
            Set dups = m_generator.getDuplicates(this);
            dups.retainAll(frames);
            if (dups.size() > 1) {
                throw new IllegalStateException
                    ("Multiple duplicate frames returned: " + dups);
            }
            if (dups.size() > 0) {
                QFrame dup = (QFrame) dups.iterator().next();
                setAlias(dup);
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
