package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;
import java.sql.*;

/**
 * StaticOperation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/05/07 $
 **/

class StaticOperation extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/StaticOperation.java#5 $ by $Author: rhs $, $DateTime: 2003/05/07 09:50:14 $";

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
        if (m_sql.hasType(path)) {
            return m_sql.getType(path);
        }
        return super.getType(path);
    }


    void write(SQLWriter w) {
        w.write(this);
    }

}
