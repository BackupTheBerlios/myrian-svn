package com.redhat.persistence.oql;

/**
 * BinaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/02/06 $
 **/

public abstract class BinaryCondition extends Condition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/BinaryCondition.java#4 $ by $Author: rhs $, $DateTime: 2004/02/06 15:43:04 $";

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

    Code.Frame frame(Code code) {
        code.setFrame(m_left, m_left.frame(code));
        code.setFrame(m_right, m_right.frame(code));
        return null;
    }

    void emit(Code code) {
        code.append("(");
        m_left.emit(code);
        code.append(")");
        code.append(" " + getOperator() + " ");
        code.append("(");
        m_right.emit(code);
        code.append(")");
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
