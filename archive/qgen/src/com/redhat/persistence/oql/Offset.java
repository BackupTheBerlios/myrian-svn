package com.redhat.persistence.oql;

/**
 * Offset
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/02/21 $
 **/

public class Offset extends Range {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Offset.java#4 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    public Offset(Expression query, Expression offset) {
        super(query, offset);
    }

    void frame(Generator gen) {
        super.frame(gen);
        QFrame frame = gen.getFrame(this);
        frame.setOffset(m_operand);
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
