package com.redhat.persistence.oql;

/**
 * UnaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/03/28 $
 **/

public abstract class UnaryCondition extends Condition {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/UnaryCondition.java#2 $ by $Author: rhs $, $DateTime: 2004/03/28 22:52:45 $";

    Expression m_operand;

    UnaryCondition(Expression operand) {
        m_operand = operand;
    }

    void frame(Generator gen) {
        m_operand.frame(gen);
        gen.addUses(this, gen.getUses(m_operand));
    }

    void hash(Generator gen) {
        m_operand.hash(gen);
        gen.hash(getClass());
    }

}
