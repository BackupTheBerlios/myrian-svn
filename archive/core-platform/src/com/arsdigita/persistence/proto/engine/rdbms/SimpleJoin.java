package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

/**
 * SimpleJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

class SimpleJoin extends Join {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/engine/rdbms/SimpleJoin.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    private Table m_table;
    private Path m_alias;

    public SimpleJoin(Table table, Path alias) {
        m_table = table;
        m_alias = alias;
    }

    public Table getTable() {
        return m_table;
    }

    public Path getAlias() {
        return m_alias;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
