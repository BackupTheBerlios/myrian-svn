package com.redhat.persistence.oql;

import java.util.*;

/**
 * Or
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/16 $
 **/

public class Or extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Or.java#2 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    Or(Expression left, Expression right) {
        super(left, right);
    }

    void graph(Pane pane) {
        super.graph(pane);
        Pane left = pane.frame.getPane(m_left);
        Pane right = pane.frame.getPane(m_right);
        pane.constrained =
            new IntersectConstraintNode(left.constrained, right.constrained);
    }

    public String getOperator() {
        return "or";
    }

}
