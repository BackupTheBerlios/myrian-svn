package com.redhat.persistence.oql;

/**
 * CrossJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/03/11 $
 **/

public class CrossJoin extends AbstractJoin {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/CrossJoin.java#1 $ by $Author: vadim $, $DateTime: 2004/03/11 18:13:02 $";

    CrossJoin(Expression left, Expression right) {
        super(left, right, null);
    }

    String getJoinType() {
        return "cross";
    }

}
