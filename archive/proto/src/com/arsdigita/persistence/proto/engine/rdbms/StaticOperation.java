package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;
import java.sql.*;

/**
 * StaticOperation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/03/14 $
 **/

class StaticOperation extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/StaticOperation.java#3 $ by $Author: rhs $, $DateTime: 2003/03/14 13:52:50 $";

    private SQLBlock m_sql;

    public StaticOperation(SQLBlock sql, Environment env) {
        super(env);
        m_sql = sql;
        for (Iterator it = sql.getSQL().getBindings().iterator();
             it.hasNext(); ) {
            addParameter((Path) it.next());
        }
    }

    public SQLBlock getSQLBlock() {
        return m_sql;
    }

    public int getType(Path path) {
        int type = Types.INTEGER;
        if (m_sql.hasType(path)) {
            type = m_sql.getType(path);
        }
        return type;
    }


    void write(SQLWriter w) {
        w.write(this);
    }

}
