package com.redhat.persistence.oql;

/**
 * UnaryCondition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/03/11 $
 **/

public abstract class UnaryCondition extends Condition {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/UnaryCondition.java#1 $ by $Author: vadim $, $DateTime: 2004/03/11 18:13:02 $";

    Expression m_operand;

    UnaryCondition(Expression operand) {
        m_operand = operand;
    }

    void frame(Generator gen) {
        m_operand.frame(gen);
        gen.addUses(this, gen.getUses(m_operand));
    }

}
