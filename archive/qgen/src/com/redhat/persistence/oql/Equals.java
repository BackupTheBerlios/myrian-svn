package com.redhat.persistence.oql;

/**
 * Equals
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/16 $
 **/

public class Equals extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Equals.java#2 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

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
                    constrain(m_right, m_left);
                }
                if (correlated(right.variables)) {
                    constrain(m_left, m_right);
                }
            }

            private boolean correlated(VariableNode nd) {
                return nd.lower != null && nd.lower.intValue() > 0;
            }
        };
    }

    String getOperator() {
        return "==";
    }

}
