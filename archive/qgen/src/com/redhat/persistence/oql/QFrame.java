package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * QFrame
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #15 $ $Date: 2004/03/09 $
 **/

class QFrame {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/QFrame.java#15 $ by $Author: rhs $, $DateTime: 2004/03/09 15:48:58 $";

    private static final Logger s_log = Logger.getLogger(QFrame.class);

    private Generator m_generator;
    private Expression m_expression;
    private ObjectType m_type;
    private QFrame m_container;

    private String m_alias;

    private boolean m_outer = false;
    private List m_values = null;
    private String m_table = null;
    private Expression m_tableExpr = null;
    private Map m_columns = null;
    private QFrame m_parent = null;
    private List m_children = null;
    private Expression m_condition = null;
    private Expression m_order = null;
    private boolean m_asc = true;
    private Expression m_limit = null;
    private Expression m_offset = null;
    private boolean m_hoisted = false;
    private QFrame m_duplicate = null;
    private EquiSet m_equiset;

    QFrame(Generator generator, Expression expression, ObjectType type,
           QFrame container) {
        m_generator = generator;
        m_expression = expression;
        m_type = type;
        m_container = container;
        m_alias = "t" + m_generator.getFrames().size();
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
            m_values.add(getValue(columns[i]));
        }
    }

    void setValues(List values) {
        m_values = values;
    }

    List getValues() {
        return m_values;
    }

    QValue getValue(String column) {
        if (m_columns == null) { m_columns = new HashMap(); }
        QValue v = (QValue) m_columns.get(column);
        if (v == null) {
            v = new QValue(this, column);
            m_columns.put(column, v);
        }
        return v;
    }

    Set getColumns() {
        if (m_columns == null) {
            return Collections.EMPTY_SET;
        } else {
            return m_columns.keySet();
        }
    }

    boolean hasValue(String column) {
        if (m_columns == null) {
            return false;
        } else {
            return m_columns.containsKey(column);
        }
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
        if (m_children == null) { m_children = new ArrayList(); }
        m_children.add(child);
        child.m_parent = this;
    }

    void addChild(int index, QFrame child) {
        if (m_children == null) { m_children = new ArrayList(); }
        m_children.add(index, child);
        child.m_parent = this;
    }

    QFrame getChild(int index) {
        return (QFrame) m_children.get(index);
    }

    List getChildren() {
        if (m_children == null) {
            return Collections.EMPTY_LIST;
        } else {
            return m_children;
        }
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
        if (m_duplicate != null) { return m_duplicate.alias(); }
        return m_alias;
    }

    EquiSet getEquiSet() {
        if (m_equiset != null) {
            return m_equiset;
        } else {
            return m_parent.getEquiSet();
        }
    }

    void setEquiSet(EquiSet equiset) {
        m_equiset = equiset;
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
        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
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
        if (m_table != null && m_duplicate == null) {
            first = false;
            result.append(m_table + " " + alias());
            defs.add(this);
        } else if (m_tableExpr != null && m_duplicate == null) {
            first = false;
            result.append(m_tableExpr.emit(m_generator) + " " + alias());
            defs.add(this);
        }

        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
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

    Set frames(Collection values) {
        Set result = new HashSet();
        for (Iterator it = values.iterator(); it.hasNext(); ) {
            QValue value = (QValue) it.next();
            result.add(value.getFrame().getDuplicate());
        }
        return result;
    }

    List getConditions() {
        List result = new ArrayList();
        addConditions(result);
        return result;
    }

    private void addConditions(List result) {
        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
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
        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
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

    QFrame getInnerRoot() {
        if (m_parent == null || m_outer) {
            return this;
        } else {
            return m_parent.getInnerRoot();
        }
    }

    List getInnerConditions() {
        List result = new ArrayList();
        addInnerConditions(result);
        return result;
    }

    void addInnerConditions(List result) {
        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (!child.m_outer) {
                child.addInnerConditions(result);
            }
        }
        if (m_condition != null) {
            result.add(m_condition);
        }
    }

    Set nonnulls() {
        QFrame iroot;
        if (m_parent == null) {
            iroot = this;
        } else {
            iroot = m_parent.getInnerRoot();
        }
        List conds = iroot.getInnerConditions();
        Set result = new HashSet();
        for (Iterator it = conds.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            Set set = m_generator.getNonNull(e);
            for (Iterator iter = set.iterator(); iter.hasNext(); ) {
                QValue nn = (QValue) iter.next();
                result.add(nn);
                Set equiv = getEquiSet().get(nn);
                if (equiv == null) { continue; }
                for (Iterator ii = equiv.iterator(); ii.hasNext(); ) {
                    QValue v = (QValue) ii.next();
                    if (v.getFrame().getInnerRoot().equals(iroot)) {
                        result.add(v);
                    }
                }
            }
        }
        return result;
    }

    boolean innerize() {
        Set nonnulls = nonnulls();
        boolean modified = false;
        if (!m_outer) {
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
                if (!m_generator.isNullable(this, equals, nonnulls)) {
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
        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (child.innerize(frames)) {
                modified = true;
            }
        }
        return modified;
    }

    private boolean containsAny(Set frames) {
        if (frames.contains(this)) { return true; }
        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (child.containsAny(frames)) {
                return true;
            }
        }
        return false;
    }

    boolean contains(QValue value) {
        if (value.getFrame().equals(this)) { return true; }
        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (child.contains(value)) { return true; }
        }
        return false;
    }

    boolean isConstrained(Set columns) {
        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (!child.isConstrained(columns)) { return false; }
        }
        if (m_table != null) {
            if (!m_generator.isConstrained(m_table, columns)) {
                return false;
            }
        }
        if (m_tableExpr != null) { return false; }
        return true;
    }

    void shrink() {
        if (m_parent != null) { return; }
        List framesets = getEquiSet().getFrameSets();
        QFrame[] frames = new QFrame[framesets.size()];
        shrink(frames, framesets);
    }

    private void shrink(QFrame[] frames, List framesets) {
        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            child.shrink(frames, framesets);
        }

        if (m_table != null) {
            QFrame dup = null;
            for (int i = 0; i < framesets.size(); i++) {
                Set set = (Set) framesets.get(i);
                if (set.contains(this)) {
                    dup = frames[i];
                    if (dup == null) {
                        dup = this;
                        frames[i] = dup;
                    }
                }
            }
            if (dup != null && !dup.equals(this)) {
                setDuplicate(dup);
            }
        }
    }

    private void setDuplicate(QFrame dup) {
        m_duplicate = dup;
    }

    QFrame getDuplicate() {
        if (m_duplicate == null) { return this; }
        return m_duplicate.getDuplicate();
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
        result.append(isOuter() ? "O" : "I");
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
        if (getChildren().isEmpty()) {
            return result.toString();
        }
        result.append(" {");
        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
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
