package com.redhat.persistence.oql;

/**
 * Equals
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/01/23 $
 **/

public class Equals extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Equals.java#4 $ by $Author: rhs $, $DateTime: 2004/01/23 15:34:30 $";

    Equals(Expression left, Expression right) {
        super(left, right);
    }

    void graph(Pane pane) {
        super.graph(pane);
        final Pane left = pane.frame.getPane(m_left);
        final Pane right = pane.frame.getPane(m_right);
        pane.constrained = new ConstraintNode() {
            { add(left.variables); add(right.variables); }
            void updateConstraints() {
                if (correlated(left.variables)) {
                    constrain(right, left);
                }
                if (correlated(right.variables)) {
                    constrain(left, right);
                }
            }

            private boolean correlated(VariableNode nd) {
                return nd.lower != null && nd.lower.intValue() > 0;
            }
        };
    }

    void emit(Code code) {
        Code.Frame left = code.getFrame(m_left);
        Code.Frame right = code.getFrame(m_right);
        if (left.getColumns().length <= 1) {
            super.emit(code);
            return;
        }

        code.append("exists(select * from ");
        m_left.emit(code);
        code.append(" leq cross join ");
        m_right.emit(code);
        code.append(" req where ");
        code.equals(left.getColumns(), right.getColumns());
        code.append(")");
    }

    String getOperator() {
        return "==";
    }

}
