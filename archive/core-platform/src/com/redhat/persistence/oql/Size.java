package com.redhat.persistence.oql;

/**
 * Size
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/03/28 $
 **/

public class Size extends Expression {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Size.java#2 $ by $Author: rhs $, $DateTime: 2004/03/28 22:52:45 $";

    private Expression m_query;

    public Size(Expression query) {
        m_query = query;
    }

    void frame(Generator gen) {
        m_query.frame(gen);
        QFrame query = gen.getFrame(m_query);
        QFrame frame = gen.frame(this, query.getType());
        frame.addChild(query);
        frame.setLimit(query.getLimit());
        frame.setOffset(query.getOffset());
        gen.addUses(this, gen.getUses(m_query));
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        m_query.hash(gen);
        gen.hash(getClass());
    }

    public String toString() {
        return "size(" + m_query + ")";
    }

    String summary() {
        return "size";
    }

}
