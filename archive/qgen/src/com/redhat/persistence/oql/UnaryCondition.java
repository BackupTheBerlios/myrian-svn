package com.redhat.persistence.oql;

/**
 * UnaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/02/06 $
 **/

public abstract class UnaryCondition extends Condition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/UnaryCondition.java#3 $ by $Author: rhs $, $DateTime: 2004/02/06 15:43:04 $";

    Expression m_operand;

    UnaryCondition(Expression operand) {
        m_operand = operand;
    }

    void graph(Pane pane) {
        Pane op = pane.frame.graph(m_operand);
        pane.variables = op.variables;
        pane.constrained = new ConstraintNode() {
            void updateConstraints() {}
        };
    }

    Code.Frame frame(Code code) {
        code.setFrame(m_operand, m_operand.frame(code));
        return null;
    }

}
