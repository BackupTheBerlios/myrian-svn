package com.redhat.persistence.oql;

/**
 * FullJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/03/11 $
 **/

public class FullJoin extends AbstractJoin {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/FullJoin.java#1 $ by $Author: vadim $, $DateTime: 2004/03/11 18:13:02 $";

    public FullJoin(Expression left, Expression right, Expression condition) {
        super(left, right, condition);
    }

    String getJoinType() {
        return "full";
    }

}
