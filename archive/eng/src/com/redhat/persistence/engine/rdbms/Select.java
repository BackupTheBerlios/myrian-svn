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

import com.redhat.persistence.Signature;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Select
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

class Select extends Operation {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/engine/rdbms/Select.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Query m_query;
    private Signature m_sig ;

    public Select(RDBMSEngine engine, Signature sig, Expression expr) {
        this(engine, sig.makeQuery(engine.getSession(), expr));
        m_sig = sig;
    }

    public Select(RDBMSEngine engine, Query query) {
        super(engine, new Environment(engine, null));
        m_query = query;
    }

    public Query getQuery() {
        return m_query;
    }

    public Signature getSignature() {
        return m_sig;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
