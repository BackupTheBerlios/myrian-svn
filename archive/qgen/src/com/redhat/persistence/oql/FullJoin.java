package com.redhat.persistence.oql;

/**
 * FullJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

public class FullJoin extends AbstractJoin {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/FullJoin.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    FullJoin(Expression left, Expression right, Expression condition) {
        super(left, right, condition);
    }

    String getJoinType() {
        return "full";
    }

}
