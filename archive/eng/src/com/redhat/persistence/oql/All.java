/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * All
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public class All extends Expression {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/All.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

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
        final ObjectType type = gen.getType(m_type);
        ObjectMap map = type.getRoot().getObjectMap(type);
        SQLBlock block = map.getRetrieveAll();
        String[] columns = Code.columns(type, null);

        if (block == null) {
            QFrame frame = gen.frame(this, type);
            frame.setTable(Code.table(map).getName());
            frame.setValues(columns);
        } else if (m_substitute || gen.isBoolean(this)) {
            Static all = new Static
                (block.getSQL(), null, false, m_bindings, m_scope);
            all.frame(gen);
            gen.setSubstitute(this, all);
        } else {
            QFrame frame = gen.frame(this, type);
            Static all = new Static
                (block.getSQL(), columns, false, m_bindings, m_scope) {
                protected ObjectType getType() { return type; }
                protected boolean hasType() { return true; }
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
