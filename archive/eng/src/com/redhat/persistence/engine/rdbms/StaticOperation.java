/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.SQLBlock;
import java.util.Iterator;

/**
 * StaticOperation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

class StaticOperation extends Operation {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/engine/rdbms/StaticOperation.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
