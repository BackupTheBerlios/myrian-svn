package com.redhat.persistence.oql;

import java.util.*;

/**
 * Or
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/02/21 $
 **/

public class Or extends BinaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Or.java#4 $ by $Author: rhs $, $DateTime: 2004/02/21 18:22:56 $";

    public Or(Expression left, Expression right) {
        super(left, right);
    }

    public String getOperator() {
        return "or";
    }

}
