package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Get
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/02/06 $
 **/

public class Get extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Get.java#7 $ by $Author: rhs $, $DateTime: 2004/02/06 15:43:04 $";

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
        if (prop == null) {
            throw new IllegalStateException
                ("no such property: " + m_name + " in " + expr.type);
        }
        Code.Frame frame;
        if (code.isQualias(prop)) {
            frame = code.frame(this, expr, prop);
        } else {
            frame = code.frame(prop.getType());
            String[] columns = expr.getColumns(prop);
            if (columns == null) {
                code.setAlias(this, frame.alias(prop));
            } else {
                frame.setColumns(columns);
            }
        }
        code.setFrame(m_expr, expr);
        code.setFrame(this, frame);
        return frame;
    }

    void emit(Code code) {
        Code.Frame expr = code.getFrame(m_expr);
        Property prop = expr.type.getProperty(m_name);
        Code.Frame frame = code.getFrame(this);
        String alias = code.getAlias(this);

        if (code.isQualias(prop)) {
            m_expr.emit(code);
            code.append(" cross join ");
            code.emit(this);
            return;
        }

        String[] columns = expr.getColumns(prop);
        if (columns == null) {
            code.table(prop);
            code.append(" ");
            code.append(alias);
            code.append(" join ");
        }
        m_expr.emit(code);
        if (columns == null) {
            code.append(" on ");
            code.condition(prop, alias, expr.getColumns());
        }
    }

    public String toString() {
        if (m_expr instanceof Condition) {
            return "(" + m_expr + ")." + m_name;
        } else {
            return m_expr + "." + m_name;
        }
    }

    String summary() { return "get " + m_name; }

}
