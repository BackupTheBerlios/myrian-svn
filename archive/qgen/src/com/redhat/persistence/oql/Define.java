package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Define
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public class Define extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Define.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    private Expression m_expr;
    private String m_name;

    Define(Expression expr, String name) {
        m_expr = expr;
        m_name = name;
    }

    public String toSQL() {
        return m_expr.toSQL() + " as " + m_name;
    }

    void add(Environment env, Frame parent) {
        env.add(m_expr, parent);
    }

    void type(Environment env, Frame f) {
        Frame frame = env.getFrame(m_expr);
        if (frame.getType() == null) { return; }
        Model anon = Model.getInstance("anonymous.define");
        ObjectType type = new ObjectType
            (anon, frame.getType().getQualifiedName() + "$" + m_name, null);
        Property prop = new Role(m_name, frame.getType(), false, false, false);
        type.addProperty(prop);
        if (!getKeys(frame.getType()).isEmpty()) {
            addKey(type, Collections.singleton(prop));
        }
        f.setType(type);
    }

    void count(Environment env, Frame f) {
        Frame frame = env.getFrame(m_expr);
        f.setCorrelationMax(frame.getCorrelationMax());
        f.setCorrelationMin(frame.getCorrelationMin());
        f.setNullable(frame.isNullable());
        f.setCollection(frame.isCollection());
        if (frame.isSet()) {
            f.addAllKeys(getKeys(f.getType()));
        }
    }

    public String toString() {
        return "define(" + m_expr + ", \"" + m_name + "\")";
    }

}
