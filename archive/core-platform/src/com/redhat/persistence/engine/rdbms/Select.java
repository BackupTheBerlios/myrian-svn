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
 * @version $Revision: #6 $ $Date: 2004/03/30 $
 **/

class Select extends Operation {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/Select.java#6 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    private Query m_query;
    private Signature m_sig ;

    public Select(RDBMSEngine engine, Signature sig, Expression expr) {
        this(engine, sig.makeQuery(expr));
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
