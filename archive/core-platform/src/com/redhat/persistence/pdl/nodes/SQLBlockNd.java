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
package com.redhat.persistence.pdl.nodes;

import java.util.Collection;

/**
 * SQLBlockNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/03/30 $
 **/

public class SQLBlockNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/nodes/SQLBlockNd.java#4 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    public static final Field MAPPINGS =
        new Field(SQLBlockNd.class, "mappings", MappingNd.class, 0);
    public static final Field BINDINGS =
        new Field(SQLBlockNd.class, "bindings", BindingNd.class, 0);

    private String m_sql;

    public SQLBlockNd(String sql) {
        m_sql = sql;
    }

    public String getSQL() {
        return m_sql;
    }

    public Collection getMappings() {
        return (Collection) get(MAPPINGS);
    }

    public Collection getBindings() {
        return (Collection) get(BINDINGS);
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onSQLBlock(this);
    }

}
