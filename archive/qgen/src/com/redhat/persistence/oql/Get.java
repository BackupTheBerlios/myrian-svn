package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Get
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/16 $
 **/

public class Get extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Get.java#2 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private Expression m_expr;
    private String m_name;

    Get(Expression expr, String name) {
        m_expr = expr;
        m_name = name;
    }

    void graph(Pane pane) {
        Pane expr = pane.frame.graph(m_expr);
        pane.type = new GetTypeNode(expr.type, m_name);
        pane.variables = expr.variables;
        pane.constrained = expr.constrained;
        pane.keys = new GetKeyNode(expr.keys, expr.type, m_name);
    }

    public String toString() {
        if (m_expr instanceof Variable ||
            m_expr instanceof Get ||
            m_expr instanceof Query) {
            return m_expr + "." + m_name;
        } else {
            return "(" + m_expr + ")." + m_name;
        }
    }

    String summary() { return "get " + m_name; }

}
