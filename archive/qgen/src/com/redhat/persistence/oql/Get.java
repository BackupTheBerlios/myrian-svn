package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Get
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/01/23 $
 **/

public class Get extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Get.java#4 $ by $Author: rhs $, $DateTime: 2004/01/23 15:34:30 $";

    private Expression m_expr;
    private String m_name;

    Get(Expression expr, String name) {
        m_expr = expr;
        m_name = name;
    }

    void graph(Pane pane) {
        final Pane expr = pane.frame.graph(m_expr);
        pane.type = new GetTypeNode(expr.type, m_name);
        pane.variables = expr.variables;
        pane.injection = new PropertyNode() {
            { add(expr.type); add(expr.keys); }
            void updateProperties() {
                Property prop = expr.type.type.getProperty(m_name);
                if (expr.keys.contains(Collections.singleton(prop))) {
                    properties.add(prop);
                }
            }
        };
        pane.constrained = expr.constrained;
        pane.keys = new GetKeyNode(expr.keys, expr.type, m_name);
    }

    Code.Frame frame(Code code) {
        Code.Frame expr = m_expr.frame(code);
        Property prop = expr.type.getProperty(m_name);
        Code.Frame frame = code.frame(prop.getType());
        // XXX: hack for package lookup
        if (code.isPackage(m_expr) && prop.getType().getRoot() == null) {
            code.addPackage(this);
        } else {
            frame.alias();
            code.setFrame(m_expr, expr);
            code.setFrame(this, frame);
        }
        return frame;
    }

    void emit(Code code) {
        // XXX: hack for package lookup
        if (code.isPackage(this)) { return; }

        Code.Frame expr = code.getFrame(m_expr);
        Property prop = expr.type.getProperty(m_name);
        Code.Frame frame = code.getFrame(this);
        code.append("(select ");
        String[] columns = expr.getColumns(prop);
        if (columns == null) {
            // XXX: hack for package lookup
            if (code.isPackage(m_expr)) {
                code.alias(prop.getType(), frame.getColumns());
            } else {
                code.alias(prop, frame.getColumns());
            }
        } else {
            code.alias(columns, frame.getColumns());
        }
        code.append(" from ");
        if (columns == null) {
            // XXX: hack for package lookup
            if (code.isPackage(m_expr)) {
                code.table(prop.getType());
                code.append(")");
                return;
            }
            code.table(prop);
            code.append(" join ");
        }
        m_expr.emit(code);
        code.append(" e");
        if (columns == null) {
            code.append(" on ");
            code.condition(prop, expr.getColumns());
        }
        code.append(")");
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
