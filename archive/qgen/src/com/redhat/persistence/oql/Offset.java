package com.redhat.persistence.oql;

/**
 * Offset
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/26 $
 **/

public class Offset extends Range {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Offset.java#1 $ by $Author: rhs $, $DateTime: 2004/01/26 12:32:44 $";

    Offset(Expression query, Expression offset) {
        super(query, offset);
    }

    void emit(Code code) {
        code.append("(select * from ");
        m_query.emit(code);
        code.append(" of offset ");
        m_operand.emit(code);
        code.append(")");
    }

    public String toString() {
        return "offset(" + m_query + ", " + m_operand + ")";
    }

    public String summary() {
        return "offset";
    }

}
