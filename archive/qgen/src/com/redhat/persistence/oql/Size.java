package com.redhat.persistence.oql;

/**
 * Size
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/02/23 $
 **/

public class Size extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Size.java#5 $ by $Author: ashah $, $DateTime: 2004/02/23 11:51:21 $";

    private Expression m_query;

    public Size(Expression query) {
        m_query = query;
    }

    void frame(Generator gen) {
        m_query.frame(gen);
        // make sure we're being called on something with a QFrame
        gen.getFrame(m_query);
    }

    String emit(Generator gen) {
        return "select count(*) from " + m_query.emit(gen) + " c__";
    }

    public String toString() {
        return "size(" + m_query + ")";
    }

    String summary() {
        return "size";
    }

}
