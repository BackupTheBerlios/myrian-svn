package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Get
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public class Get extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Get.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    private Expression m_expr;
    private String m_name;

    Get(Expression expr, String name) {
        m_expr = expr;
        m_name = name;
    }

    public String toSQL() {
        return m_expr.toSQL() + "." + m_name;
    }

    void add(Environment env, Frame parent) {
        env.add(m_expr, parent);
    }

    void type(Environment env, Frame f) {
        Frame frame = env.getFrame(m_expr);
        if (frame.getType() == null) { return; }
        f.setType(frame.getType().getProperty(m_name).getType());
    }

    void count(Environment env, Frame f) {
        Frame frame = env.getFrame(m_expr);

        f.setCorrelationMax(frame.getCorrelationMax());
        f.setCorrelationMin(frame.getCorrelationMin());

        ObjectType type = frame.getType();
        Property prop = type.getProperty(m_name);
        if ((frame.isCollection() && frame.isKey(Collections.singleton(prop)))
            || (!frame.isCollection())) {
            f.addAllKeys(getKeys(prop.getType()));
        }

        if (isKey(type, Collections.singleton(prop))) {
            f.getInjection().addAll(frame.getInjection());
        }

        if (!frame.isCollection() && !prop.isCollection()) {
            f.setCollection(false);
        }
        if (!frame.isNullable() && !prop.isNullable()) {
            f.setNullable(false);
        }
    }

    public String toString() {
        return "get(" + m_expr + ", \"" + m_name + "\")";
    }

}
