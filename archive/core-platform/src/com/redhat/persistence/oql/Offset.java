package com.redhat.persistence.oql;

/**
 * Offset
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/03/11 $
 **/

public class Offset extends Range {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Offset.java#1 $ by $Author: vadim $, $DateTime: 2004/03/11 18:13:02 $";

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
