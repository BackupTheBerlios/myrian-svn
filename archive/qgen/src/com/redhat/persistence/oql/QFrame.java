package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * QFrame
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #17 $ $Date: 2004/03/16 $
 **/

class QFrame {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/QFrame.java#17 $ by $Author: rhs $, $DateTime: 2004/03/16 15:39:46 $";

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
    private EquiSet m_equiset = null;
    private Set m_nonnull = null;

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

    QValue getValue(Code sql) {
        return new QValue(this, sql);
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
        return m_equiset;
    }

    Code emit() {
        return emit(true, true);
    }

    Code emit(boolean select, boolean range) {
        List where = new ArrayList();
        Set emitted = new HashSet();
        Code join = null;
        if (!m_hoisted) {
            join = render(where, emitted);
        }

        Code result = new Code();
        if (select) {
            if (join != null) {
                result = result.add("(select ");
            } else if (m_values.size() > 1) {
                result = result.add("(");
            }
            for (Iterator it = m_values.iterator(); it.hasNext(); ) {
                QValue v = (QValue) it.next();
                result = result.add(v.emit());
                if (it.hasNext()) {
                    result = result.add(", ");
                }
            }
            if (m_values.isEmpty()) {
                result = result.add("1");
            }
        }

        if (select && join != null) {
            result = result.add("\nfrom ");
        }

        if (join != null) {
            result = result.add(join);
        }

        Code sql = join(where, "\nand ", emitted);
        if (sql != null) {
            result = result.add("\nwhere ");
            result = result.add(sql);
        }

        List orders = getOrders();
        if (!orders.isEmpty()) {
            result = result.add("\norder by ");
        }
        for (Iterator it = orders.iterator(); it.hasNext(); ) {
            Code key = (Code) it.next();
            result = result.add(key);
            if (it.hasNext()) {
                result = result.add(", ");
            }
        }

        if (range) {
            // XXX: nested offsets and limits are ignored
            if (m_offset != null) {
                result = result.add("\noffset ");
                result = result.add(m_offset.emit(m_generator));
            }

            if (m_limit != null) {
                result = result.add("\nlimit ");
                result = result.add(m_limit.emit(m_generator));
            }
        }

        if (select && (join != null || m_values.size() > 1)) {
            result = result.add(")");
        }

        return result;
    }

    private Code join(List exprs, String sep, Set emitted) {
        List conditions = new ArrayList();
        for (Iterator it = exprs.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            Code sql = e.emit(m_generator);
            if (!sql.isTrue() && !emitted.contains(sql)) {
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
            Code order = m_order.emit(m_generator);
            if (!m_asc) {
                order = order.add(" desc");
            }
            result.add(order);
        }
        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            child.addOrders(result);
        }
    }

    private Code render(List conditions, Set emitted) {
        return render(conditions, new HashSet(), emitted, new boolean[1]);
    }

    private Code render(List conditions, Set defined, Set emitted,
                        boolean[] outer) {
        Code result = new Code();

        Set defs = new HashSet();

        boolean first = true;
        if (m_table != null && m_duplicate == null) {
            first = false;
            result = result.add(m_table).add(" ").add(alias());
            defs.add(this);
        } else if (m_tableExpr != null && m_duplicate == null) {
            first = false;
            result = result.add(m_tableExpr.emit(m_generator)).add(" ")
                .add(alias());
            defs.add(this);
        }

        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            List conds = new ArrayList();
            boolean[] isOuter = { false };
            Code join = child.render(conds, defs, emitted, isOuter);
            boolean prop = false;
            for (Iterator iter = conds.iterator(); iter.hasNext(); ) {
                Expression e = (Expression) iter.next();
                Code c = e.emit(m_generator);
                if (c.isTrue() || emitted.contains(c)) {
                    iter.remove();
                    continue;
                }
                Set used = frames(e);
                used.removeAll(defs);
                if (!used.isEmpty()) {
                    prop = true;
                    break;
                }
            }
            if (join == null || prop) {
                if (!conds.isEmpty()) {
                    if (isOuter[0] && !outer[0]) {
                        outer[0] = true;
                    } else if (!isOuter[0] && outer[0]) {
                        throw new IllegalStateException
                            ("can't merge inner and outer conditions:" +
                             "\nconds =  " + conds +
                             "\nconditions = " + conditions +
                             "\nroot = " + getRoot() +
                             "\nthis = " + this +
                             "\nchild = " + child);
                    }
                }
                conditions.addAll(conds);
                conds.clear();
            }
            if (join == null) { continue; }
            Set nemitted = new HashSet();
            nemitted.addAll(emitted);
            Code on = join(conds, " and ", nemitted);
            if (!first) {
                result = result.add("\n");
                if (isOuter[0] && on != null) {
                    result = result.add("left ");
                } else if (on == null) {
                    result = result.add("cross ");
                }
                result = result.add("join ");
            }
            result = result.add(join);
            if (on != null) {
                if (first) {
                    if (isOuter[0]) {
                        throw new IllegalStateException
                            ("Propogating outer join conditions:" +
                             "\nroot: " + getRoot() +
                             "\nchild: " + child +
                             "\nthis: " + this +
                             "\nconds: " + conds);
                    }
                    conditions.addAll(conds);
                } else {
                    result = result.add(" on ");
                    result = result.add(on);
                    emitted.addAll(nemitted);
                }
            }
            first = false;
        }

        if (m_condition != null) {
            conditions.add(m_condition);
        }

        if (m_outer) { outer[0] = true; }

        defined.addAll(defs);

        if (first) {
            return null;
        } else {
            return result;
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

    void mergeOuter() {
        if (!m_outer) { return; }
        List equals = getEquals();
        if (equals != null) {
            List from = new ArrayList();
            List to = new ArrayList();
            m_generator.split(this, equals, from, to);
            if (isConnected(to, from)) {
                QFrame target = ((QValue) to.get(0)).getFrame();
                if (target.getRoot().equals(getRoot())) {
                    // At this point barring the possibility of from
                    // being a nullable unique key we know merging is
                    // ok, so we're going to move this frame to be a
                    // child of the to frame so that we can later
                    // merge its equiset with its new parent.

                    // XXX: consider moving the frame directly to
                    // its final destination in hoist rather than
                    // moving it in two steps
                    m_parent.m_children.remove(this);
                    target.addChild(this);

                    // XXX: the isNullable(from) test shouldn't be
                    // disabled (nor should the one down below in
                    // equifill), but it breaks QuerySuite in a number of
                    // cases that I don't have time to figure out right
                    // now.
                    if (!isNullable(to) && (true || !isNullable(from))) {
                        m_outer = false;
                    }
                }
            }
        }
    }

    void equifill() {
        if (m_equiset == null) { m_equiset = new EquiSet(m_generator); }
        if (m_nonnull == null) { m_nonnull = new HashSet(); }

        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (child.m_outer) {
                List equals = child.getEquals();
                if (equals != null) {
                    List from = new ArrayList();
                    List to = new ArrayList();
                    m_generator.split(child, equals, from, to);
                    if (isConnected(to, from) && (true || !isNullable(from))) {
                        child.m_equiset = m_equiset;
                    }
                }
                child.equifill();
            } else {
                child.m_equiset = m_equiset;
                child.m_nonnull = m_nonnull;
                child.equifill();
            }
        }

        if (m_condition != null) {
            Set nn  = m_generator.getNonNull(m_condition);
            for (Iterator it = nn.iterator(); it.hasNext(); ) {
                QValue qv = (QValue) it.next();
                m_nonnull.add(qv);
            }
        }

        if (m_columns != null) {
            for (Iterator it = m_columns.values().iterator(); it.hasNext(); ) {
                QValue qv = (QValue) it.next();
                if (!isNullable(Collections.singletonList(qv))) {
                    m_nonnull.add(qv);
                }
            }
        }
    }

    private boolean m_equated = false;

    boolean innerize(Set collapse) {
        boolean modified = false;

        if (!m_outer && m_parent != null && m_equiset != m_parent.m_equiset) {
            if (m_parent.m_equiset.addAll(m_equiset)) {
                collapse.add(m_parent.m_equiset);
            }
            m_equiset = m_parent.m_equiset;
            modified = true;
        }

        if (!m_outer && m_parent != null && m_nonnull != m_parent.m_nonnull) {
            m_parent.m_nonnull.addAll(m_nonnull);
            m_nonnull = m_parent.m_nonnull;
            modified = true;
        }

        if (m_condition != null) {
            if (!m_equated) {
                m_generator.equate(m_equiset, m_condition);
                collapse.add(m_equiset);
                m_equated = true;
                modified = true;
            }
        }

        if (!m_outer) {
            modified |= innerizeChildren(this);
        }

        if (m_outer) {
            List equals = getEquals();
            if (equals != null) {
                List from = new ArrayList();
                List to = new ArrayList();
                m_generator.split(this, equals, from, to);

                // XXX: compound keys
                if (to.size() == 1) {
                    QValue target = (QValue) to.get(0);
                    Set vals = m_parent.m_equiset.get(target);
                    if (vals != null) {
                        QValue key = (QValue) from.get(0);
                        String table = key.getTable();
                        String column = key.getColumn();
                        if (table != null && column != null) {
                            for (Iterator it = vals.iterator();
                                 it.hasNext(); ) {
                                QValue qv = (QValue) it.next();
                                if (table.equals(qv.getTable())
                                    && column.equals(qv.getColumn())
                                    && m_parent.nn(qv)) {
                                    m_outer = false;
                                    modified = true;
                                    // Our join condition is
                                    // equivalent to an inner join
                                    // condition
                                }
                            }
                        }
                    }
                }
            }
        }

        return modified;
    }

    private List getEquals() {
        List conditions = getConditions();
        List equals = new ArrayList();
        for (int i = 0; i < conditions.size(); i++) {
            Expression c = (Expression) conditions.get(i);
            if (!m_generator.isSufficient(c)) {
                return null;
            }
            equals.addAll(m_generator.getEqualities(c));
        }
        return equals;
    }

    private boolean nn(QValue qv) {
        Set s = m_equiset.get(qv);
        for (Iterator it = m_nonnull.iterator(); it.hasNext(); ) {
            QValue nn = (QValue) it.next();
            if (nn.equals(qv) || s != null && s == m_equiset.get(nn)) {
                return true;
            }
        }
        if (m_parent == null) { return false; }
        return m_parent.nn(qv);
    }

    private boolean isNullable(List qvalues) {
        Column[] cols = columns(qvalues);
        if (cols == null) { return true; }
        return isNullable(cols);
    }

    private boolean isNullable(Column[] cols) {
        for (int i = 0; i < cols.length; i++) {
            if (cols[i].isNullable()) {
                return true;
            }
        }
        return false;
    }

    private boolean isConnected(List from, List to) {
        Column[] fcols = columns(from);
        if (fcols == null) { return false; }
        Column[] tcols = columns(to);
        if (tcols == null) { return false; }
        return isConnected(fcols, tcols);
    }

    private Column[] columns(List qvalues) {
        if (qvalues.isEmpty()) { return null; }
        Column[] result = new Column[qvalues.size()];
        for (int i = 0; i < result.length; i++) {
            QValue v = (QValue) qvalues.get(i);
            Table t = m_generator.getRoot().getTable(v.getTable());
            if (t == null) { return null; }
            Column c = t.getColumn(v.getColumn());
            if (c == null) { return null; }
            result[i] = c;
        }
        return result;
    }

    private boolean isConnected(Column[] from, Column[] to) {
        if (Arrays.equals(from, to)) { return true; }
        ForeignKey fk = from[0].getTable().getForeignKey(from);
        if (fk == null) { return false; }
        UniqueKey uk = to[0].getTable().getUniqueKey(to);
        if (uk == null) { return false; }
        return isConnected(fk, uk);
    }

    private boolean isConnected(ForeignKey from, UniqueKey to) {
        UniqueKey uk = from.getUniqueKey();
        if (uk.equals(to)) { return true; }
        ForeignKey fk = uk.getTable().getForeignKey(uk.getColumns());
        if (fk == null) { return false; }
        else { return isConnected(fk, to); }
    }

    boolean innerizeChildren(QFrame ancestor) {
        boolean modified = false;
        if (m_outer && containsAnyNN(ancestor)) {
            m_outer = false;
            modified = true;
        }
        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (child.innerizeChildren(ancestor)) {
                modified = true;
            }
        }
        return modified;
    }

    private boolean containsAnyNN(QFrame ancestor) {
        if (m_columns != null) {
            for (Iterator it = m_columns.values().iterator(); it.hasNext(); ) {
                QValue qv = (QValue) it.next();
                if (ancestor.nn(qv)) { return true; }
            }
        }
        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (child.containsAnyNN(ancestor)) {
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
        if (m_parent == null || m_equiset != m_parent.m_equiset) {
            List framesets = m_equiset.getFrameSets();
            if (framesets == null) {
                m_equiset.collapse();
                framesets = m_equiset.getFrameSets();
            }
            QFrame[] frames = new QFrame[framesets.size()];
            shrink(frames, framesets);
        }
    }

    private void shrink(QFrame[] frames, List framesets) {
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

        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
            QFrame child = (QFrame) it.next();
            if (child.m_equiset == m_equiset) {
                child.shrink(frames, framesets);
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
        if (m_nonnull != null && !m_nonnull.isEmpty()) {
            result.append("\n");
            indent(result, depth);
            result.append(" nn ");
            if (m_parent != null && m_nonnull == m_parent.m_nonnull) {
                result.append("--> (parent)");
            } else {
                result.append(m_nonnull);
            }
        }
        if (m_equiset != null && !m_equiset.getSets().isEmpty()) {
            result.append("\n");
            indent(result, depth);
            result.append(" eq ");
            if (m_parent != null && m_equiset == m_parent.m_equiset) {
                result.append("--> (parent)");
            } else {
                result.append(m_equiset);
            }
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
