package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;
import java.sql.*;

/**
 * StaticOperation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/03/31 $
 **/

class StaticOperation extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/StaticOperation.java#4 $ by $Author: rhs $, $DateTime: 2003/03/31 10:58:30 $";

    private SQLBlock m_sql;

    public StaticOperation(SQLBlock sql, Environment env) {
	this(sql, env, true);
    }

    public StaticOperation(SQLBlock sql, Environment env, boolean initialize) {
        super(env);
        m_sql = sql;
        for (Iterator it = sql.getSQL().getBindings().iterator();
	     it.hasNext(); ) {
	    Path p = (Path) it.next();
            addParameter(p);
	    if (initialize) {
		if (!env.contains(p)) {
		    env.set(p, null);
		}
	    }
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
