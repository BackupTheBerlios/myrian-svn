package com.redhat.persistence.oql;

/**
 * RightJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/02/09 $
 **/

public class RightJoin extends AbstractJoin {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/RightJoin.java#2 $ by $Author: ashah $, $DateTime: 2004/02/09 16:16:05 $";

    public RightJoin(Expression left, Expression right, Expression condition) {
        super(left, right, condition);
    }

    String getJoinType() {
        return "right";
    }

}
