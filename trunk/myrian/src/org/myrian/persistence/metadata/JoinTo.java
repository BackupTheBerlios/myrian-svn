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
package org.myrian.persistence.metadata;

import org.myrian.persistence.common.Path;

import java.util.*;

/**
 * JoinTo
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

public class JoinTo extends Mapping {


    private ForeignKey m_key;

    public JoinTo(Path path, ForeignKey key) {
        super(path);
        m_key = key;
    }

    public List getColumns() {
        return Arrays.asList(m_key.getColumns());
    }

    public Table getTable() {
        return m_key.getTable();
    }

    public ForeignKey getKey() {
        return m_key;
    }

    public void dispatch(Switch sw) {
        sw.onJoinTo(this);
    }

}
