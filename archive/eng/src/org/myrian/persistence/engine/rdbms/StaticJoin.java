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
package org.myrian.persistence.engine.rdbms;

import org.myrian.persistence.common.Path;

/**
 * StaticJoin
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

class StaticJoin extends Join {


    private StaticOperation m_op;
    private Path m_alias;

    public StaticJoin(StaticOperation op, Path alias) {
        m_op = op;
        m_alias = alias;
    }

    public StaticOperation getStaticOperation() {
        return m_op;
    }

    public Path getAlias() {
        return m_alias;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}