package com.redhat.persistence.oql;

/**
 * Limit
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/02/06 $
 **/

public class Limit extends Range {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Limit.java#2 $ by $Author: rhs $, $DateTime: 2004/02/06 15:43:04 $";

    public Limit(Expression query, Expression limit) {
        super(query, limit);
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
