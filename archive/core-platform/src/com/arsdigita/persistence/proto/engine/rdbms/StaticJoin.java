package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

/**
 * StaticJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

class StaticJoin extends Join {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/engine/rdbms/StaticJoin.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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
