package com.redhat.persistence.oql;

/**
 * RightJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/03/20 $
 **/

public class RightJoin extends LeftJoin {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/RightJoin.java#3 $ by $Author: rhs $, $DateTime: 2004/03/20 20:50:09 $";

    public RightJoin(Expression left, Expression right, Expression condition) {
        super(right, left, condition);
    }

}
