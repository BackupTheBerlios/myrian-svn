package com.redhat.persistence.oql;

/**
 * Equals
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public class Equals extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Equals.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    Equals(Expression left, Expression right) {
        super(left, right);
    }

    void count(Environment env, Frame f) {
        super.count(env, f);
        Frame left = env.getFrame(m_left);
        Frame right = env.getFrame(m_right);

        if (left.getCorrelationMin() > 1) {
            f.getConstrained().add(m_right);
        }
        if (right.getCorrelationMin() > 1) {
            f.getConstrained().add(m_left);
        }
    }

    String getOperator() {
        return "==";
    }

}
