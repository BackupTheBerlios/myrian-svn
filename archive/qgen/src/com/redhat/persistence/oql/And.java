package com.redhat.persistence.oql;

/**
 * And
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/02/21 $
 **/

public class And extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/And.java#6 $ by $Author: rhs $, $DateTime: 2004/02/21 23:13:07 $";

    public And(Expression left, Expression right) {
        super(left, right);
    }

    String getOperator() {
        return "and";
    }

    void frame(Generator gen) {
        super.frame(gen);
        gen.addEqualities(this, gen.getEqualities(m_left));
        gen.addEqualities(this, gen.getEqualities(m_right));
    }

}
