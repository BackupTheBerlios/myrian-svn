package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Filter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/01/29 $
 **/

public class Filter extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Filter.java#6 $ by $Author: rhs $, $DateTime: 2004/01/29 12:50:13 $";

    private Expression m_expr;
    private Expression m_condition;

    Filter(Expression expr, Expression condition) {
        m_expr = expr;
        m_condition = condition;
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
        Code.Frame frame = m_expr.frame(code);
        code.push(frame);
        try {
            m_condition.frame(code);
        } finally {
            code.pop();
        }
        return frame;
    }

    void emit(Code code) {
        code.append("(select * from ");
        m_expr.emit(code);
        code.append(" f where ");
        m_condition.emit(code);
        code.append(")");
    }

    public String toString() {
        return "filter(" + m_expr + ", " + m_condition + ")";
    }

    String summary() { return "filter"; }

}
