package com.redhat.persistence.oql;

/**
 * Size
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/02/24 $
 **/

public class Size extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Size.java#6 $ by $Author: rhs $, $DateTime: 2004/02/24 10:13:24 $";

    private Expression m_query;

    public Size(Expression query) {
        m_query = query;
    }

    void frame(Generator gen) {
        m_query.frame(gen);
        // make sure we're being called on something with a QFrame
        gen.getFrame(m_query);
        gen.addUses(this, gen.getUses(m_query));
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
