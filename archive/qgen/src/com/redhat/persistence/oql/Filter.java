package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Filter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/01/19 $
 **/

public class Filter extends Query {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Filter.java#3 $ by $Author: rhs $, $DateTime: 2004/01/19 14:43:24 $";

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
        pane.keys = new FilterKeyNode(expr.keys, cond.constrained);
    }

    public String toString() {
        return "filter(" + m_expr + ", " + m_condition + ")";
    }

    String summary() { return "filter"; }

}
