package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Define
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #10 $ $Date: 2004/02/21 $
 **/

public class Define extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Define.java#10 $ by $Author: rhs $, $DateTime: 2004/02/21 18:22:56 $";

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

    public String toString() {
        return m_name + " = " + m_expr;
    }

    String summary() { return "define " + m_name; }

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
        return result;
    }

}
