package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * AbstractJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/01/23 $
 **/

public abstract class AbstractJoin extends Query {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/AbstractJoin.java#4 $ by $Author: rhs $, $DateTime: 2004/01/23 15:34:30 $";

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
            pane.keys = new FilterKeyNode(pane.keys, cond.constrained);
        }
    }

    Code.Frame frame(Code code) {
        Code.Frame left = m_left.frame(code);
        Code.Frame right = m_right.frame(code);
        Code.Frame frame =
            code.frame(JoinTypeNode.join(left.type, right.type));
        for (Iterator it = frame.type.getKeyProperties().iterator();
             it.hasNext(); ) {
            Property prop = (Property) it.next();
            if (left.type.hasProperty(prop.getName())) {
                frame.setColumns(prop, left.getColumns(prop.getName()));
            } else {
                frame.setColumns(prop, right.getColumns(prop.getName()));
            }
        }

        if (m_condition != null) {
            code.push(frame);
            try {
                m_condition.frame(code);
            } finally {
                code.pop();
            }
        }
        return frame;
    }

    void emit(Code code) {
        code.append("(select * from ");
        m_left.emit(code);
        code.append(" l ");
        String type = getJoinType();
        code.append(type);
        code.append(" ");
        if (!type.equals("join")) {
            code.append("join ");
        }
        m_right.emit(code);
        code.append(" r");

        if (m_condition != null) {
            code.append(" on ");
            m_condition.emit(code);
        }
        code.append(")");
    }

    public String toString() {
        return getJoinType() + "(" + m_left + ", " + m_right +
            (m_condition == null ? "" : ", " + m_condition) + ")";
    }

    abstract String getJoinType();

    String summary() { return getJoinType(); }

}
