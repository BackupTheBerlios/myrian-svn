package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.common.*;

/**
 * StaticJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

class StaticJoin extends Join {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/StaticJoin.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    private StaticOperation m_op;
    private Path m_alias;

    public StaticJoin(StaticOperation op, Path alias) {
        m_op = op;
        m_alias = alias;
    }

    public StaticOperation getStaticOperation() {
        return m_op;
    }

    public Path getAlias() {
        return m_alias;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}