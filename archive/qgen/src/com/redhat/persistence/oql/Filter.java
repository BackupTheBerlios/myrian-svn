package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Filter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public class Filter extends Query {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Filter.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    private Expression m_expr;
    private Expression m_condition;

    Filter(Expression expr, Expression condition) {
        m_expr = expr;
        m_condition = condition;
    }

    public String toSQL() {
        return m_expr.toSQL() + " where " + m_condition.toSQL();
    }

    void add(Environment env, Frame parent) {
        env.add(m_expr, parent);
        env.add(m_condition, env.getFrame(this));
    }

    void type(Environment env, Frame f) {
        Frame frame = env.getFrame(m_expr);
        if (frame.getType() == null) { return; }
        f.setType(frame.getType());
    }

    void count(Environment env, Frame f) {
        Frame frame = env.getFrame(m_expr);
        Frame condition = env.getFrame(m_condition);

        f.setCorrelationMax
            (Math.max(frame.getCorrelationMax(),
                      condition.getCorrelationMax() - 1));
        f.setCorrelationMin
            (Math.min(frame.getCorrelationMin(),
                      condition.getCorrelationMin()));

        f.addAllKeys(frame.getKeys());

        Set constrained = new HashSet();
        Set exprs = condition.getConstrained();
        for (Iterator it = exprs.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            constrained.addAll(env.getFrame(e).getInjection());
        }

        for (Iterator it = new ArrayList(f.getKeys()).iterator();
             it.hasNext(); ) {
            List key = (List) it.next();
            List newKey = new ArrayList(key);
            newKey.removeAll(constrained);
            if (newKey.isEmpty()) {
                f.setCollection(false);
            } else {
                f.addKey(newKey);
            }
        }
    }

    public String toString() {
        return "filter(" + m_expr + ", " + m_condition + ")";
    }

}
