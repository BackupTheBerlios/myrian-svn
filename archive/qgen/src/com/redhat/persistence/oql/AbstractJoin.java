package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * AbstractJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/02/21 $
 **/

public abstract class AbstractJoin extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/AbstractJoin.java#7 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    private Expression m_left;
    private Expression m_right;
    private Expression m_condition;

    AbstractJoin(Expression left, Expression right, Expression condition) {
        m_left = left;
        m_right = right;
        m_condition = condition;
    }

    void frame(Generator gen) {
        m_left.frame(gen);
        QFrame left = gen.getFrame(m_left);
        m_right.frame(gen);
        QFrame right = gen.getFrame(m_right);
        QFrame frame = gen.frame
            (this, JoinTypeNode.join(left.getType(), right.getType()));
        frame.addChild(left);
        frame.addChild(right);
        List values = new ArrayList();
        values.addAll(left.getValues());
        values.addAll(right.getValues());
        frame.setValues(values);
        if (m_condition != null) {
            gen.push(frame);
            try {
                m_condition.frame(gen);
            } finally {
                gen.pop();
            }
            frame.setCondition(m_condition);
        }
    }

    String emit(Generator gen) {
        return gen.getFrame(this).emit();
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

        code.setFrame(this, frame);
        return frame;
    }

    void opt(Code code) {
        m_left.opt(code);
        m_right.opt(code);
        if (m_condition != null) { m_condition.opt(code); }

        Code.Frame frame = code.getFrame(this);
        Code.Frame left = code.getFrame(m_left);
        Code.Frame right = code.getFrame(m_right);
        Code.Frame cond = null;
        if (m_condition != null) {
            cond = code.getFrame(m_condition);
        }

        frame.suckAll(left);
        frame.suckAll(right);
        if (cond != null) {
            frame.suckConstrained(cond);
        }
    }

    void emit(Code code) {
        Code.Frame frame = code.getFrame(this);
        String join = frame.join();
        if (join != null) {
            code.append(join);
            code.append(" cross join ");
        }

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
