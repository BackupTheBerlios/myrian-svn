package com.redhat.persistence.oql;

/**
 * Sort
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/23 $
 **/

public class Sort extends Query {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Sort.java#1 $ by $Author: rhs $, $DateTime: 2004/01/23 18:29:26 $";

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
        code.push(query);
        try {
            m_key.frame(code);
        } finally {
            code.pop();
        }
        return query;
    }

    void emit(Code code) {
        code.append("(select * from ");
        m_query.emit(code);
        code.append(" o order by ");
        m_key.emit(code);
        if (m_order == ASCENDING) {
            code.append(" asc");
        } else if (m_order == DESCENDING) {
            code.append(" desc");
        }
        code.append(")");
    }

    String summary() {
        return "order";
    }

    public String toString() {
        return "order(" + m_query + ", " + m_key + ")";
    }

}
