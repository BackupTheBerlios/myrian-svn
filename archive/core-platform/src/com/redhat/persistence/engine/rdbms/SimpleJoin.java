package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;

/**
 * SimpleJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

class SimpleJoin extends Join {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/SimpleJoin.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

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
