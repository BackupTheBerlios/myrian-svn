package com.redhat.persistence.oql;

/**
 * UnaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/02/21 $
 **/

public abstract class UnaryCondition extends Condition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/UnaryCondition.java#4 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    Expression m_operand;

    UnaryCondition(Expression operand) {
        m_operand = operand;
    }

    void frame(Generator gen) {
        m_operand.frame(gen);
    }

    void graph(Pane pane) {
        Pane op = pane.frame.graph(m_operand);
        pane.variables = op.variables;
        pane.constrained = new ConstraintNode() {
            void updateConstraints() {}
        };
    }

    Code.Frame frame(Code code) {
        Code.Frame frame = code.frame(null);
        m_operand.frame(code);
        code.setFrame(this, frame);
        return frame;
    }

    void opt(Code code) {
        m_operand.opt(code);
        Code.Frame frame = code.getFrame(this);
        Code.Frame op = code.getFrame(m_operand);
        frame.suckConstrained(op);
    }

}
