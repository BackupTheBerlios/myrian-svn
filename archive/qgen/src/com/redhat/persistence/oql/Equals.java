package com.redhat.persistence.oql;

/**
 * Equals
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/02/09 $
 **/

public class Equals extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Equals.java#7 $ by $Author: ashah $, $DateTime: 2004/02/09 16:16:05 $";

    public Equals(Expression left, Expression right) {
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
            if (left.type == null) {
                code.materialize(m_right);
                code.append(" is ");
                code.materialize(m_left);
            } else if (right.type == null) {
                code.materialize(m_left);
                code.append(" is ");
                code.materialize(m_right);
            } else {
                code.materialize(m_left);
                code.append(" = ");
                code.materialize(m_right);
            }
        } else {
            code.append("exists(select * from ");
            m_left.emit(code);
            code.append(" cross join ");
            m_right.emit(code);
            code.append(" where ");
            code.equals(left.getColumns(), right.getColumns());
            code.append(")");
        }
    }

    String getOperator() {
        return "==";
    }

}
