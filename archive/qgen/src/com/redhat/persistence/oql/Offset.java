package com.redhat.persistence.oql;

/**
 * Offset
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/02/06 $
 **/

public class Offset extends Range {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Offset.java#2 $ by $Author: rhs $, $DateTime: 2004/02/06 15:43:04 $";

    Offset(Expression query, Expression offset) {
        super(query, offset);
    }

    String getRangeType() {
        return "offset";
    }

    public String toString() {
        return "offset(" + m_query + ", " + m_operand + ")";
    }

    public String summary() {
        return "offset";
    }

}
