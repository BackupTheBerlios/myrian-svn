package com.redhat.persistence.oql;

import java.util.*;

/**
 * Or
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/02/28 $
 **/

public class Or extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Or.java#5 $ by $Author: rhs $, $DateTime: 2004/02/28 08:30:26 $";

    public Or(Expression left, Expression right) {
        super(left, right);
    }

    String emit(Generator gen) {
        String left = m_left.emit(gen);
        String right = m_right.emit(gen);
        if (Code.FALSE.equals(left)) {
            return right;
        } else if (Code.FALSE.equals(right)) {
            return left;
        } else if (Code.TRUE.equals(left) || Code.TRUE.equals(right)) {
            return Code.TRUE;
        } else {
            return left + " or " + right;
        }
    }

    public String getOperator() {
        return "or";
    }

}
