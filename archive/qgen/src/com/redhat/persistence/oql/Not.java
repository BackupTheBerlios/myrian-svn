package com.redhat.persistence.oql;

/**
 * Not
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public class Not extends Condition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Not.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    private Expression m_expr;

    Not(Expression expr) {
        m_expr = expr;
    }

    public String toSQL() {
        return "not " + m_expr.toSQL();
    }

    void add(Environment env, Frame parent) {
        env.add(m_expr, parent);
    }

    void count(Environment env, Frame f) {
        Frame expr = env.getFrame(m_expr);
        f.setCorrelationMax(expr.getCorrelationMax());
        f.setCorrelationMin(expr.getCorrelationMin());
    }

}
