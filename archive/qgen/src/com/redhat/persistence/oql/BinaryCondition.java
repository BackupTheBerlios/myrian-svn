package com.redhat.persistence.oql;

/**
 * BinaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/02/21 $
 **/

public abstract class BinaryCondition extends Condition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/BinaryCondition.java#5 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    Expression m_left;
    Expression m_right;

    BinaryCondition(Expression left, Expression right) {
        m_left = left;
        m_right = right;
    }

    void frame(Generator gen) {
        m_left.frame(gen);
        m_right.frame(gen);
    }

    String emit(Generator gen) {
        String op = getOperator();
        return m_left.emit(gen) + " " + op + " " + m_right.emit(gen);
    }

    void graph(Pane pane) {
        Pane left = pane.frame.graph(m_left);
        Pane right = pane.frame.graph(m_right);
        pane.variables =
            new UnionVariableNode(left.variables, right.variables);
    }

    Code.Frame frame(Code code) {
        Code.Frame frame = code.frame(null);
        m_left.frame(code);
        m_right.frame(code);
        code.setFrame(this, frame);
        return frame;
    }

    void opt(Code code) {
        m_left.opt(code);
        m_right.opt(code);

        Code.Frame frame = code.getFrame(this);
        Code.Frame left = code.getFrame(m_left);
        Code.Frame right = code.getFrame(m_right);
        frame.suckConstrained(left);
        frame.suckConstrained(right);
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
