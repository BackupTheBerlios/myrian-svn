package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * AbstractJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/16 $
 **/

public abstract class AbstractJoin extends Query {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/AbstractJoin.java#2 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private Expression m_left;
    private Expression m_right;
    private Expression m_condition;

    AbstractJoin(Expression left, Expression right, Expression condition) {
        m_left = left;
        m_right = right;
        m_condition = condition;
    }

    void graph(Pane pane) {
        Pane left = pane.frame.graph(m_left);
        Pane right = pane.frame.graph(m_right);
        pane.type = new JoinTypeNode(left.type, right.type);
        Frame frame = new Frame(pane.frame, pane.type);
        Pane cond = m_condition == null ? null : frame.graph(m_condition);
        pane.variables =
            new UnionVariableNode(left.variables, right.variables);
        pane.keys = new JoinKeyNode(pane.type, left.keys, right.keys);
        if (cond != null) {
            pane.variables = new UnionVariableNode
                (pane.variables, new ExternalVariableNode(cond.variables));
            pane.keys = new FilterKeyNode(pane.keys, frame, cond.constrained);
        }
    }

    public String toString() {
        return getJoinType() + "(" + m_left + ", " + m_right +
            (m_condition == null ? "" : ", " + m_condition) + ")";
    }

    abstract String getJoinType();

    String summary() { return getJoinType(); }

}
