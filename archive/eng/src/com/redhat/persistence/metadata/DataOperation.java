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
package com.redhat.persistence.metadata;

import com.redhat.persistence.common.Path;

/**
 * DataOperation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class DataOperation extends Element {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/DataOperation.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Path m_name;
    private SQLBlock m_sql;

    public DataOperation(Path name, SQLBlock sql) {
        m_name = name;
        m_sql = sql;
    }

    public Path getName() {
        return m_name;
    }

    public SQLBlock getSQL() {
        return m_sql;
    }

    Object getElementKey() {
        return m_name;
    }

    public String toString() {
        return "data operation " + getName() + " {\n" + getSQL() + " }\n";
    }

}
