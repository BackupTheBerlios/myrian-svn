package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

/**
 * SimpleJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/02/14 $
 **/

class SimpleJoin extends Join {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/SimpleJoin.java#3 $ by $Author: rhs $, $DateTime: 2003/02/14 16:46:06 $";

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

    public String toString() {
        return m_table.getName() + " as " + m_alias;
    }

}
