package com.redhat.persistence.oql;

/**
 * And
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/16 $
 **/

public class And extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/And.java#2 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    And(Expression left, Expression right) {
        super(left, right);
    }

    String getOperator() {
        return "and";
    }

    void graph(Pane pane) {
        super.graph(pane);
        Pane left = pane.frame.getPane(m_left);
        Pane right = pane.frame.getPane(m_right);
        pane.constrained =
            new UnionConstraintNode(left.constrained, right.constrained);
    }

}
