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
import com.redhat.persistence.metadata.Column;
import com.redhat.persistence.metadata.Table;
import java.util.Collection;
import java.util.HashMap;

/**
 * DML
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

abstract class DML extends Operation {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/engine/rdbms/DML.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    private Table m_table;
    private HashMap m_bindings = new HashMap();

    public DML(RDBMSEngine engine, Table table) {
        super(engine);
        m_table = table;
    }

    public Table getTable() {
        return m_table;
    }

    private Path getValuePath(Column column) {
        return Path.get("__" + column.getName() + "__");
    }

    public void set(Column column, Object value) {
        Path vp = getValuePath(column);
        m_bindings.put(column, vp);
        set(vp, value, column.getType());
    }

    public Path get(Column column) {
        return (Path) m_bindings.get(column);
    }

    public Collection getColumns() {
        return m_bindings.keySet();
    }

}
