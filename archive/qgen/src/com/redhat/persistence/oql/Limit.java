package com.redhat.persistence.oql;

/**
 * Limit
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/02/21 $
 **/

public class Limit extends Range {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Limit.java#3 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    public Limit(Expression query, Expression limit) {
        super(query, limit);
    }

    void frame(Generator gen) {
        super.frame(gen);
        QFrame frame = gen.getFrame(this);
        frame.setLimit(m_operand);
        QFrame query = gen.getFrame(m_query);
        frame.setOffset(query.getOffset());
    }

    String getRangeType() {
        return "limit";
    }

    public String toString() {
        return "limit(" + m_query + ", " + m_operand + ")";
    }

    public String summary() {
        return "limit";
    }

}
