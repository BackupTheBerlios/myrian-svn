package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

/**
 * StaticJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/26 $
 **/

class StaticJoin extends Join {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/StaticJoin.java#1 $ by $Author: rhs $, $DateTime: 2003/02/26 12:01:31 $";

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
