/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
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
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

class StaticOperation extends Operation {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/engine/rdbms/StaticOperation.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

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
