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
package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.metadata.Static;
import java.util.*;

/**
 * Define
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/30 $
 **/

public class Define extends Expression {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Define.java#5 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Expression m_expr;
    private String m_name;

    public Define(Expression expr, String name) {
        m_expr = expr;
        m_name = name;
    }

    void frame(Generator gen) {
        m_expr.frame(gen);
        QFrame expr = gen.getFrame(m_expr);
        QFrame frame = gen.frame(this, define(m_name, expr.getMap()));
        frame.addChild(expr);
        frame.setValues(expr.getValues());
        if (expr.hasMappings()) {
            for (Iterator it = expr.getMappings().entrySet().iterator();
                 it.hasNext(); ) {
                Map.Entry me = (Map.Entry) it.next();
                Path key = (Path) me.getKey();
                String value = (String) me.getValue();
                frame.addMapping(Path.add(m_name, key), value);
            }
        }
        gen.addUses(this, gen.getUses(m_expr));
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        m_expr.hash(gen);
        gen.hash(m_name);
        gen.hash(getClass());
    }

    public String toString() {
        return m_name + " = " + m_expr;
    }

    String summary() { return "define " + m_name; }

    static ObjectMap define(final String name, ObjectMap map) {
        final ObjectType type = map.getObjectType();
        Model anon = Model.getInstance("anonymous.define");
        ObjectType def = new ObjectType
            (anon, type.getQualifiedName() + "$" + name, (ObjectType) null) {
            public String toString() {
                return "{" + type + " " + name + ";" + "}";
            }
        };
        Property prop = new Role(name, type, false, false, false);
        def.addProperty(prop);
        ObjectMap result = new ObjectMap(def);
        Mapping m = new Static(Path.get(name));
        m.setMap(map, false);
        result.addMapping(m);
        result.getKeyProperties().add(prop);
        return result;
    }

}
