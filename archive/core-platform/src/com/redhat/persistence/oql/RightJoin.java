package com.redhat.persistence.oql;

/**
 * RightJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/03/23 $
 **/

public class RightJoin extends LeftJoin {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/RightJoin.java#2 $ by $Author: dennis $, $DateTime: 2004/03/23 03:39:40 $";

    public RightJoin(Expression left, Expression right, Expression condition) {
        super(right, left, condition);
    }

}
