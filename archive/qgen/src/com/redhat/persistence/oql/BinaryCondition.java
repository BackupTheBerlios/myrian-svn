package com.redhat.persistence.oql;

/**
 * BinaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/16 $
 **/

public abstract class BinaryCondition extends Condition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/BinaryCondition.java#2 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    Expression m_left;
    Expression m_right;

    BinaryCondition(Expression left, Expression right) {
        m_left = left;
        m_right = right;
    }

    void graph(Pane pane) {
        Pane left = pane.frame.graph(m_left);
        Pane right = pane.frame.graph(m_right);
        pane.variables =
            new UnionVariableNode(left.variables, right.variables);
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

    abstract String getOperator();

    String summary() { return getOperator(); }

}
