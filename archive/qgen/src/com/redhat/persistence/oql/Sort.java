package com.redhat.persistence.oql;

/**
 * Sort
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/02/21 $
 **/

public class Sort extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Sort.java#6 $ by $Author: rhs $, $DateTime: 2004/02/21 18:22:56 $";

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

    String summary() {
        return "sort";
    }

    public String toString() {
        return "sort(" + m_query + ", " + m_key + ")";
    }

}
