package com.redhat.persistence.oql;

/**
 * Equals
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/01/19 $
 **/

public class Equals extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Equals.java#3 $ by $Author: rhs $, $DateTime: 2004/01/19 14:43:24 $";

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

    String getOperator() {
        return "==";
    }

}
