package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Define
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #9 $ $Date: 2004/02/21 $
 **/

public class Define extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Define.java#9 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    private Expression m_expr;
    private String m_name;

    public Define(Expression expr, String name) {
        m_expr = expr;
        m_name = name;
    }

    void frame(Generator gen) {
        m_expr.frame(gen);
        QFrame expr = gen.getFrame(m_expr);
        QFrame frame = gen.frame(this, define(m_name, expr.getType()));
        frame.addChild(expr);
        frame.setValues(expr.getValues());
    }

    String emit(Generator gen) {
        return gen.getFrame(this).emit();
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

    Code.Frame frame(Code code) {
        Code.Frame expr = m_expr.frame(code);
        Code.Frame frame = code.frame(define(m_name, expr.type));
        frame.setColumns(expr.getColumns());
        code.setFrame(this, frame);
        return frame;
    }

    void opt(Code code) {
        m_expr.opt(code);
        Code.Frame expr = code.getFrame(m_expr);
        Code.Frame frame = code.getFrame(this);
        frame.suckAll(expr);
    }

    void emit(Code code) {
        m_expr.emit(code);
    }

    static ObjectType define(final String name, final ObjectType type) {
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
