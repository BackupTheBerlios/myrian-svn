package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Define
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/01/19 $
 **/

public class Define extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Define.java#3 $ by $Author: rhs $, $DateTime: 2004/01/19 17:32:28 $";

    private Expression m_expr;
    private String m_name;

    Define(Expression expr, String name) {
        m_expr = expr;
        m_name = name;
    }

    void graph(final Pane pane) {
        final Pane expr = pane.frame.graph(m_expr);
        pane.type = new TypeNode() {
            { add(expr.type); }
            void updateType() {
                type = define(m_name, expr.type.type);
            }
        };
        pane.variables = expr.variables;
        pane.injection = expr.injection;
        pane.constrained = expr.constrained;
        pane.keys = new KeyNode() {
            { add(expr.keys); add(pane.type); }
            void updateKeys() {
                if (!expr.keys.isEmpty()) {
                    add(pane.type.type.getProperties());
                }
            }
        };
    }

    private static ObjectType define(final String name,
                                     final ObjectType type) {
        Model anon = Model.getInstance("anonymous.define");
        ObjectType result = new ObjectType
            (anon, type.getQualifiedName() + "$" + name, null) {
            public String toString() {
                return "{" + type + " " + name + ";" + "}";
            }
        };
        Property prop = new Role(name, type, false, false, false);
        result.addProperty(prop);
        if (!getKeys(type).isEmpty()) {
            addKey(result, Collections.singleton(prop));
        }
        return result;
    }

    public String toString() {
        return m_name + " = " + m_expr;
    }

    String summary() { return "define " + m_name; }

}
