package com.redhat.persistence.oql;

/**
 * UnaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/02/24 $
 **/

public abstract class UnaryCondition extends Condition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/UnaryCondition.java#6 $ by $Author: rhs $, $DateTime: 2004/02/24 10:13:24 $";

    Expression m_operand;

    UnaryCondition(Expression operand) {
        m_operand = operand;
    }

    void frame(Generator gen) {
        m_operand.frame(gen);
        gen.addUses(this, gen.getUses(m_operand));
    }

}
