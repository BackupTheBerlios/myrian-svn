package com.redhat.persistence.oql;

import java.util.*;

/**
 * Or
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public class Or extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Or.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    Or(Expression left, Expression right) {
        super(left, right);
    }

    public String getOperator() {
        return "or";
    }

    void count(Environment env, Frame f) {
        super.count(env, f);

        Frame left = env.getFrame(m_left);
        Frame right = env.getFrame(m_right);

        Set keys = keys(left.getConstrained());
        keys.retainAll(keys(right.getConstrained()));

        add(f.getConstrained(), left.getConstrained(), keys);
        add(f.getConstrained(), right.getConstrained(), keys);
    }

    private void add(Set to, Set from, Set filter) {
        for (Iterator it = from.iterator(); it.hasNext(); ) {
            Expression e = (Expression) it.next();
            String key = e.toString();
            if (filter.contains(key)) {
                to.add(e);
            }
        }
    }

    private Set keys(Set expressions) {
        Set result = new HashSet();
        for (Iterator it = expressions.iterator(); it.hasNext(); ) {
            result.add(it.next().toString());
        }
        return result;
    }

}
