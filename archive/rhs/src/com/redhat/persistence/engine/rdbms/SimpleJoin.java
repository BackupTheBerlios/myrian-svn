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
import com.redhat.persistence.metadata.Table;

/**
 * SimpleJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/11/09 $
 **/

class SimpleJoin extends Join {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/engine/rdbms/SimpleJoin.java#1 $ by $Author: rhs $, $DateTime: 2003/11/09 14:41:17 $";

    private Table m_table;
    private Path m_alias;

    public SimpleJoin(Table table, Path alias) {
        m_table = table;
        m_alias = alias;
    }

    public Table getTable() {
        return m_table;
    }

    public Path getAlias() {
        return m_alias;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
