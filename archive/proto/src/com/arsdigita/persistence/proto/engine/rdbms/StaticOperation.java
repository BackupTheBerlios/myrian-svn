package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.sql.*;

/**
 * StaticOperation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/02/26 $
 **/

class StaticOperation extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/StaticOperation.java#2 $ by $Author: rhs $, $DateTime: 2003/02/26 12:01:31 $";

    private SQLBlock m_sql;

    public StaticOperation(SQLBlock sql, Environment env) {
        super(env);
        m_sql = sql;
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
