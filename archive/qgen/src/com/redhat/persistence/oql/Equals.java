package com.redhat.persistence.oql;

import java.util.*;

/**
 * Equals
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #10 $ $Date: 2004/02/24 $
 **/

public class Equals extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Equals.java#10 $ by $Author: rhs $, $DateTime: 2004/02/24 21:30:52 $";

    public Equals(Expression left, Expression right) {
        super(left, right);
    }

    void frame(Generator gen) {
        super.frame(gen);
        QFrame left = gen.getFrame(m_left);
        QFrame right = gen.getFrame(m_right);
        if (left == null || right == null) { return; }
        List lvals = left.getValues();
        List rvals = right.getValues();
        if (lvals.size() != rvals.size()) {
            throw new IllegalStateException
                ("cardinality mismatch\nleft: " + lvals +
                 "\nrvals: " + rvals +
                 "\nleft: " + m_left +
                 "\nright: " + m_right);
        }
        for (int i = 0; i < lvals.size(); i++) {
            gen.addEquality
                (this, (QValue) lvals.get(i), (QValue) rvals.get(i));
        }
    }

    String emit(Generator gen) {
        String left = m_left.emit(gen);
        String right = m_right.emit(gen);
        if ("null".equals(left)) {
            return right + " is " + left;
        } else if ("null".equals(right)) {
            return left + " is " + right;
        } else {
            return left + " = " + right;
        }
    }

    String getOperator() {
        return "==";
    }

}
