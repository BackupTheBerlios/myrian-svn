package com.redhat.persistence.oql;

/**
 * And
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/03/23 $
 **/

public class And extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/And.java#2 $ by $Author: dennis $, $DateTime: 2004/03/23 03:39:40 $";

    public And(Expression left, Expression right) {
        super(left, right);
    }

    String getOperator() {
        return "and";
    }

    void frame(Generator gen) {
        gen.addBoolean(m_left);
        gen.addBoolean(m_right);
        super.frame(gen);
        gen.addEqualities(this, gen.getEqualities(m_left));
        gen.addEqualities(this, gen.getEqualities(m_right));
        gen.addNulls(this, gen.getNull(m_left));
        gen.addNulls(this, gen.getNull(m_right));
        gen.addNonNulls(this, gen.getNonNull(m_left));
        gen.addNonNulls(this, gen.getNonNull(m_right));
        if (gen.isSufficient(m_left) && gen.isSufficient(m_right)) {
            gen.addSufficient(this);
        }
    }

    Code emit(Generator gen) {
        Code left = m_left.emit(gen);
        Code right = m_right.emit(gen);
        if (left.isTrue()) {
            return right;
        } else if (right.isTrue()) {
            return left;
        } else {
            return emit(left, "and", right);
        }
    }

}
