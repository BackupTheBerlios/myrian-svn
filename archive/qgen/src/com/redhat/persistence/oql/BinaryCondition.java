package com.redhat.persistence.oql;

/**
 * BinaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public abstract class BinaryCondition extends Condition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/BinaryCondition.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    Expression m_left;
    Expression m_right;

    BinaryCondition(Expression left, Expression right) {
        m_left = left;
        m_right = right;
    }

    public String toSQL() {
        return "(" + m_left.toSQL() + ") " + getOperator() +
            " (" + m_right.toSQL() + ")";
    }

    abstract String getOperator();

    void add(Environment env, Frame parent) {
        env.add(m_left, parent);
        env.add(m_right, parent);
    }

    void count(Environment env, Frame f) {
        f.setCorrelationMax
            (Math.max(env.getFrame(m_left).getCorrelationMax(),
                      env.getFrame(m_right).getCorrelationMax()));
        f.setCorrelationMin
            (Math.min(env.getFrame(m_left).getCorrelationMin(),
                      env.getFrame(m_right).getCorrelationMin()));
    }

    private String str(Expression e) {
        if (e instanceof BinaryCondition) {
            return "(" + e + ")";
        } else {
            return e.toString();
        }
    }

    public String toString() {
        return str(m_left) + " " + getOperator() + " " + str(m_right);
    }

}
