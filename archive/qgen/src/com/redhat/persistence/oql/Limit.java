package com.redhat.persistence.oql;

/**
 * Limit
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/26 $
 **/

public class Limit extends Range {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Limit.java#1 $ by $Author: rhs $, $DateTime: 2004/01/26 12:32:44 $";

    public Limit(Expression query, Expression limit) {
        super(query, limit);
    }

    void emit(Code code) {
        code.append("(select * from ");
        m_query.emit(code);
        code.append(" lim limit ");
        m_operand.emit(code);
        code.append(")");
    }

    public String toString() {
        return "limit(" + m_query + ", " + m_operand + ")";
    }

    public String summary() {
        return "limit";
    }

}
