/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
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
package org.myrian.persistence.oql;

import org.myrian.persistence.common.*;
import org.myrian.persistence.metadata.*;
import java.util.*;

/**
 * All
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/10/01 $
 **/

public class All extends Expression {

    public final static String versionId = "$Id: //eng/persistence/dev/src/org/myrian/persistence/oql/All.java#1 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

    private String m_type;
    private Map m_bindings;
    private Expression m_scope;
    private boolean m_substitute;

    public All(String type) {
        this(type, Collections.EMPTY_MAP, null, false);
    }

    All(String type, Map bindings, Expression scope, boolean substitute) {
        m_type = type;
        m_bindings = bindings;
        m_scope = scope == null ? this : scope;
        m_substitute = substitute;
    }

    String getType() {
        return m_type;
    }

    void frame(Generator gen) {
        ObjectType type = gen.getType(m_type);
        final ObjectMap map = type.getRoot().getObjectMap(type);
        if (map == null) {
            throw new IllegalStateException
                ("no map for type: " + type.getQualifiedName());
        }
        SQLBlock block = map.getRetrieveAll();
        String[] columns = Code.columns(map, null);

        if (block == null) {
            QFrame frame = gen.frame(this, map);
            frame.setTable(Code.table(map));
            frame.setValues(columns);
        } else if (m_substitute || gen.isBoolean(this)) {
            Static all = new Static
                (block.getSQL(), null, false, m_bindings, m_scope);
            all.frame(gen);
            gen.setSubstitute(this, all);
        } else {
            QFrame frame = gen.frame(this, map);
            Static all = new Static
                (block.getSQL(), columns, false, m_bindings, m_scope) {
                protected ObjectMap getMap() { return map; }
                protected boolean hasMap() { return true; }
            };
            all.frame(gen);
            QFrame child = gen.getFrame(all);
            frame.addChild(child);
            frame.setValues(child.getValues());
            for (Iterator it = block.getPaths().iterator(); it.hasNext(); ) {
                Path p = (Path) it.next();
                frame.addMapping(p, block.getMapping(p).getPath());
            }
        }
    }

    Code emit(Generator gen) {
        Expression sub = gen.getSubstitute(this);
        if (sub != null) {
            return sub.emit(gen);
        }
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        ObjectType type = gen.getType(m_type);
        gen.hash(type);
        gen.hash(getClass());
    }

    public String toString() {
        return "all(" + m_type + ")";
    }

    String summary() {
        return "all: " + m_type;
    }

}
