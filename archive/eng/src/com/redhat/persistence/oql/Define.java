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
package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Define
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/07/22 $
 **/

public class Define extends Expression {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Define.java#2 $ by $Author: richardl $, $DateTime: 2004/07/22 15:13:20 $";

    private Expression m_expr;
    private String m_name;

    public Define(Expression expr, String name) {
        m_expr = expr;
        m_name = name;
    }

    void frame(Generator gen) {
        m_expr.frame(gen);
        QFrame expr = gen.getFrame(m_expr);
        QFrame frame = gen.frame(this, define(m_name, expr.getType()));
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

    static ObjectType define(final String name, final ObjectType type) {
        Model anon = Model.getInstance("anonymous.define");
        ObjectType result = new ObjectType
            (anon, type.getQualifiedName() + "$" + name, (ObjectType) null) {
            public String toString() {
                return "{" + type + " " + name + ";" + "}";
            }
        };
        Property prop = new Role(name, type, false, false, false);
        result.addProperty(prop);
        return result;
    }

}
