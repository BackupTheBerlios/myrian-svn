package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.metadata.*;

/**
 * StaticOperation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/19 $
 **/

class StaticOperation extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/StaticOperation.java#1 $ by $Author: rhs $, $DateTime: 2003/02/19 22:58:51 $";

    private SQLBlock m_sql;

    public StaticOperation(SQLBlock sql) {
        m_sql = sql;
    }

    public SQLBlock getSQLBlock() {
        return m_sql;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
