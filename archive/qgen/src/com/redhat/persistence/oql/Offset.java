package com.redhat.persistence.oql;

/**
 * Offset
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/02/09 $
 **/

public class Offset extends Range {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Offset.java#3 $ by $Author: ashah $, $DateTime: 2004/02/09 16:16:05 $";

    public Offset(Expression query, Expression offset) {
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
