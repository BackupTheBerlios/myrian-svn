package com.redhat.persistence.oql;

/**
 * RightJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public class RightJoin extends AbstractJoin {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/RightJoin.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    RightJoin(Expression left, Expression right, Expression condition) {
        super(left, right, condition);
    }

    String getJoinType() {
        return "right";
    }

}