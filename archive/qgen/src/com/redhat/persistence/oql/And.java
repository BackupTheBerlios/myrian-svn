package com.redhat.persistence.oql;

/**
 * And
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public class And extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/And.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    And(Expression left, Expression right) {
        super(left, right);
    }

    String getOperator() {
        return "and";
    }

    void count(Environment env, Frame f) {
        super.count(env, f);
        Frame left = env.getFrame(m_left);
        Frame right = env.getFrame(m_right);
        f.getConstrained().addAll(left.getConstrained());
        f.getConstrained().addAll(right.getConstrained());
    }

}
