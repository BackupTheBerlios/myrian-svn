/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
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
package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * All
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class All extends Expression {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/All.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
