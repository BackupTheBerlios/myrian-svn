package com.redhat.persistence.oql;

/**
 * CrossJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/03/23 $
 **/

public class CrossJoin extends AbstractJoin {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/CrossJoin.java#2 $ by $Author: dennis $, $DateTime: 2004/03/23 03:39:40 $";

    CrossJoin(Expression left, Expression right) {
        super(left, right);
    }

    String getJoinType() {
        return "cross";
    }

}
