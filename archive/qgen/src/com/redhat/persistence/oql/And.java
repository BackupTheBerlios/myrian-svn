package com.redhat.persistence.oql;

/**
 * And
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/02/21 $
 **/

public class And extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/And.java#4 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    public And(Expression left, Expression right) {
        super(left, right);
    }

    String getOperator() {
        return "and";
    }

    void frame(Generator gen) {
        super.frame(gen);
        gen.unionEqualities(this, m_left, m_right);
    }

    void graph(Pane pane) {
        super.graph(pane);
        Pane left = pane.frame.getPane(m_left);
        Pane right = pane.frame.getPane(m_right);
        pane.constrained =
            new UnionConstraintNode(left.constrained, right.constrained);
    }

}
