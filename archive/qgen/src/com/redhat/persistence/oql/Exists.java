package com.redhat.persistence.oql;

/**
 * Exists
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public class Exists extends Condition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Exists.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    private Expression m_query;

    Exists(Expression query) {
        m_query = query;
    }

    public String toSQL() {
        return "exists (" + m_query.toSQL() + ")";
    }

    void add(Environment env, Frame parent) {
        env.add(m_query, parent);
    }

    void count(Environment env, Frame f) {
        f.setCorrelationMax(env.getFrame(m_query).getCorrelationMax());
        f.setCorrelationMin(env.getFrame(m_query).getCorrelationMin());
    }

    public String toString() {
        return "exists(" + m_query + ")";
    }

}
