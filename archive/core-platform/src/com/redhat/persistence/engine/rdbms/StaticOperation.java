/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.SQLBlock;
import java.util.Iterator;

/**
 * StaticOperation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/03/30 $
 **/

class StaticOperation extends Operation {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/StaticOperation.java#5 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    private SQLBlock m_sql;

    public StaticOperation(RDBMSEngine engine, SQLBlock sql, Environment env) {
	this(engine, sql, env, true);
    }

    public StaticOperation(RDBMSEngine engine, SQLBlock sql, Environment env,
                           boolean initialize) {
        super(engine, env);
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
