package com.redhat.persistence.oql;

/**
 * Range
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/02/24 $
 **/

public abstract class Range extends Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Range.java#7 $ by $Author: rhs $, $DateTime: 2004/02/24 10:13:24 $";

    Expression m_query;
    Expression m_operand;

    public Range(Expression query, Expression operand) {
        m_query = query;
        m_operand = operand;
    }

    void frame(Generator gen) {
        m_query.frame(gen);
        QFrame query = gen.getFrame(m_query);
        QFrame frame = gen.frame(this, query.getType());
        frame.addChild(query);
        frame.setValues(query.getValues());
        m_operand.frame(gen);
        gen.addUses(this, gen.getUses(m_query));
        gen.addUses(this, gen.getUses(m_operand));
    }

    String emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    abstract String getRangeType();

}
