package com.redhat.persistence.oql;

import java.util.*;

/**
 * Or
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/03/20 $
 **/

public class Or extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Or.java#7 $ by $Author: rhs $, $DateTime: 2004/03/20 20:50:09 $";

    public Or(Expression left, Expression right) {
        super(left, right);
    }

    void frame(Generator gen) {
        gen.addBoolean(m_left);
        gen.addBoolean(m_right);
        super.frame(gen);
    }

    Code emit(Generator gen) {
        Code left = m_left.emit(gen);
        Code right = m_right.emit(gen);
        if (left.isFalse()) {
            return right;
        } else if (right.isFalse()) {
            return left;
        } else if (left.isTrue() || right.isTrue()) {
            return Code.TRUE;
        } else {
            return emit(left, "or", right);
        }
    }

    public String getOperator() {
        return "or";
    }

}
