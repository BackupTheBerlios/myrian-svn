package com.redhat.persistence.oql;

/**
 * Sort
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/02/21 $
 **/

public class Sort extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Sort.java#5 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    public static class Order {
        private Order() {}
    }

    public static final Order ASCENDING = new Order();
    public static final Order DESCENDING = new Order();

    private Expression m_query;
    private Expression m_key;
    private Order m_order;

    public Sort(Expression query, Expression key, Order order) {
        m_query = query;
        m_key = key;
        m_order = order;
    }

    public Sort(Expression query, Expression key) {
        this(query, key, ASCENDING);
    }

    void frame(Generator gen) {
        m_query.frame(gen);
        QFrame query = gen.getFrame(m_query);
        QFrame frame = gen.frame(this, query.getType());
        frame.addChild(query);
        frame.setValues(query.getValues());
        frame.setOrder(m_key, m_order == ASCENDING);
        gen.push(frame);
        try {
            m_key.frame(gen);
        } finally {
            gen.pop();
        }
    }

    String emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void graph(Pane pane) {
        Pane query = pane.frame.graph(m_query);
        Frame frame = new Frame(pane.frame, query.type);
        Pane key = frame.graph(m_key);
        pane.type = query.type;
        pane.variables = new UnionVariableNode(query.variables, key.variables);
        pane.injection = query.injection;
        pane.constrained = query.constrained;
        pane.keys = query.keys;
    }

    Code.Frame frame(Code code) {
        Code.Frame query = m_query.frame(code);
        Code.Frame frame = code.frame(query.type);
        code.setAlias(this, frame.alias(query.getColumns().length));
        code.push(query);
        try {
            m_key.frame(code);
        } finally {
            code.pop();
        }
        code.setFrame(this, frame);
        return frame;
    }

    void opt(Code code) {
        m_query.opt(code);
        m_key.opt(code);
        Code.Frame frame = code.getFrame(this);
        Code.Frame query = code.getFrame(m_query);
        Code.Frame key = code.getFrame(m_key);
        frame.suckAll(query);
        frame.suckConstrained(key);
    }

    void emit(Code code) {
        Code.Frame query = code.getFrame(m_query);
        code.append("(select ");
        code.alias(query.getColumns());
        code.append(" from ");
        m_query.emit(code);
        code.append(" order by ");
        code.materialize(m_key);
        if (m_order == ASCENDING) {
            code.append(" asc");
        } else if (m_order == DESCENDING) {
            code.append(" desc");
        }
        code.append(") " + code.getAlias(this));
    }

    String summary() {
        return "sort";
    }

    public String toString() {
        return "sort(" + m_query + ", " + m_key + ")";
    }

}
