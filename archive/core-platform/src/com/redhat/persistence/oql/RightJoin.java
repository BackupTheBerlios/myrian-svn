package com.redhat.persistence.oql;

/**
 * RightJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/03/11 $
 **/

public class RightJoin extends AbstractJoin {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/RightJoin.java#1 $ by $Author: vadim $, $DateTime: 2004/03/11 18:13:02 $";

    public RightJoin(Expression left, Expression right, Expression condition) {
        super(left, right, condition);
    }

    String getJoinType() {
        return "right";
    }

}
