package com.redhat.persistence.oql;

/**
 * CrossJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public class CrossJoin extends AbstractJoin {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/CrossJoin.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    CrossJoin(Expression left, Expression right) {
        super(left, right, null);
    }

    String getJoinType() {
        return "cross";
    }

}
