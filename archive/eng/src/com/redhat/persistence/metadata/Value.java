/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.metadata;

import com.redhat.persistence.common.Path;

import java.util.*;

/**
 * Value
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/05 $
 **/

public class Value extends Mapping {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/Value.java#2 $ by $Author: rhs $, $DateTime: 2004/08/05 12:04:47 $";

    private Column m_column;

    public Value(Path path, Column column) {
        super(path);
        m_column = column;
    }

    public List getColumns() {
        return Collections.singletonList(m_column);
    }

    public Table getTable() {
        return m_column.getTable();
    }

    public Column getColumn() {
        return m_column;
    }

    public void dispatch(Switch sw) {
        sw.onValue(this);
    }

}
