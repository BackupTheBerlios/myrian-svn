package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Filter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #9 $ $Date: 2004/02/21 $
 **/

public class Filter extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Filter.java#9 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    private Expression m_expr;
    private Expression m_condition;

    public Filter(Expression expr, Expression condition) {
        m_expr = expr;
        m_condition = condition;
    }

    void frame(Generator gen) {
        m_expr.frame(gen);
        QFrame expr = gen.getFrame(m_expr);
        QFrame frame = gen.frame(this, expr.getType());
        frame.addChild(expr);
        frame.setValues(expr.getValues());
        gen.push(frame);
        try {
            m_condition.frame(gen);
            frame.setCondition(m_condition);
        } finally {
            gen.pop();
        }
    }

    String emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void graph(Pane pane) {
        Pane expr = pane.frame.graph(m_expr);
        Frame frame = new Frame(pane.frame, expr.type);
        Pane cond = frame.graph(m_condition);
        pane.type = expr.type;
        pane.variables = new UnionVariableNode
            (expr.variables, new ExternalVariableNode(cond.variables));
        pane.injection = expr.injection;
        pane.keys = new FilterKeyNode(expr.keys, cond.constrained);
    }

    Code.Frame frame(Code code) {
        Code.Frame expr = m_expr.frame(code);
        Code.Frame frame = code.frame(expr.type);
        frame.setColumns(expr.getColumns());
        code.push(frame);
        try {
            m_condition.frame(code);
        } finally {
            code.pop();
        }
        code.setFrame(this, frame);
        return frame;
    }

    void opt(Code code) {
        m_expr.opt(code);
        m_condition.opt(code);
        Code.Frame frame = code.getFrame(this);
        Code.Frame expr = code.getFrame(m_expr);
        Code.Frame cond = code.getFrame(m_condition);
        frame.suckAll(expr);
        frame.suckConstrained(cond);
    }

    void emit(Code code) {
        Code.Frame frame = code.getFrame(this);
        String join = frame.join();
        if (join != null) {
            code.append(join);
            code.append(" cross join ");
        }
        m_expr.emit(code);
        code.append(" join (select 3) " + code.var("d") + " on ");
        m_condition.emit(code);
    }

    public String toString() {
        return "filter(" + m_expr + ", " + m_condition + ")";
    }

    String summary() { return "filter"; }

}
