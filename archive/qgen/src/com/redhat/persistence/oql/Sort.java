package com.redhat.persistence.oql;

/**
 * Sort
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/02/06 $
 **/

public class Sort extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Sort.java#4 $ by $Author: rhs $, $DateTime: 2004/02/06 15:43:04 $";

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
            code.setFrame(m_key, m_key.frame(code));
        } finally {
            code.pop();
        }
        code.setFrame(m_query, query);
        return frame;
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
        return "order";
    }

    public String toString() {
        return "order(" + m_query + ", " + m_key + ")";
    }

}
