/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.redhat.persistence.metadata;

import com.redhat.persistence.common.Path;

import java.util.*;

/**
 * Value
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/09/07 $
 **/

public class Value extends Mapping {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/Value.java#4 $ by $Author: dennis $, $DateTime: 2004/09/07 10:26:15 $";

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
