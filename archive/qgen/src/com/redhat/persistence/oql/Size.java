package com.redhat.persistence.oql;

/**
 * Size
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/02/21 $
 **/

public class Size extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Size.java#4 $ by $Author: rhs $, $DateTime: 2004/02/21 18:22:56 $";

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
        QFrame query = gen.getFrame(m_query);
        return "(select count(*) from " + query.emit(false) + ")";
    }

    public String toString() {
        return "size(" + m_query + ")";
    }

    String summary() {
        return "size";
    }

}
