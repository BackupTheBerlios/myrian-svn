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
import com.redhat.persistence.metadata.Column;
import com.redhat.persistence.metadata.Table;
import java.util.Collection;
import java.util.HashMap;

/**
 * DML
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

abstract class DML extends Operation {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/engine/rdbms/DML.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
