package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * AbstractJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/02/06 $
 **/

public abstract class AbstractJoin extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/AbstractJoin.java#6 $ by $Author: rhs $, $DateTime: 2004/02/06 15:43:04 $";

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

        String[] lc = left.getColumns();
        String[] rc = right.getColumns();
        String[] columns = new String[lc.length + rc.length];
        System.arraycopy(lc, 0, columns, 0, lc.length);
        System.arraycopy(rc, 0, columns, lc.length, rc.length);
        frame.setColumns(columns);

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
        m_left.emit(code);
        code.append(" ");
        String type = getJoinType();
        code.append(type);
        code.append(" ");
        if (!type.equals("join")) {
            code.append("join ");
        }
        m_right.emit(code);

        if (m_condition != null) {
            code.append(" on ");
            m_condition.emit(code);
        }
    }

    public String toString() {
        return getJoinType() + "(" + m_left + ", " + m_right +
            (m_condition == null ? "" : ", " + m_condition) + ")";
    }

    abstract String getJoinType();

    String summary() { return getJoinType(); }

}
