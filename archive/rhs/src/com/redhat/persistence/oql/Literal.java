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

// XXX: dependency on c.a.db.DbHelper
import com.arsdigita.db.DbHelper;
import com.redhat.persistence.*;
import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Literal
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/05/02 $
 **/

public class Literal extends Expression {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/oql/Literal.java#2 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    private Object m_value;

    public Literal(Object value) {
        m_value = value;
    }

    private ObjectType type(Generator gen) {
        if (m_value == null || m_value instanceof Collection) {
            return null;
        } else {
            Adapter ad = gen.getRoot().getAdapter(m_value.getClass());
            if (ad == null) { return null; }
            return ad.getObjectType(m_value);
        }
    }

    void frame(Generator gen) {
        QFrame frame = gen.frame(this, type(gen));
        List result = new ArrayList();
        Object key = gen.level > 0 ? null : getBindKey(gen);
        convert(m_value, result, gen.getRoot(), key);
        if (result.size() == 0) {
            throw new IllegalStateException
                ("unable to convert value: " + m_value);
        }
        List values = new ArrayList();
        for (int i = 0; i < result.size(); i++) {
            Code c = (Code) result.get(i);
            QValue v = frame.getValue(c);
            values.add(v);
            if (c.isNull()) {
                gen.addNull(this, v);
            } else {
                gen.addNonNull(this, v);
            }
        }
        frame.setValues(values);
    }

    Code emit(Generator gen) {
        return gen.getFrame(this).emit();
    }

    void hash(Generator gen) {
        List values = new ArrayList();
        convert(m_value, values, gen.getRoot(), getBindKey(gen));
        for (int i = 0; i < values.size(); i++) {
            Code c = (Code) values.get(i);
            gen.hash(c.getSQL());
            gen.bind(c);
        }
        gen.hash(getClass());
    }

    Object getBindKey(Generator gen) {
        return gen.id(this);
    }

    static void convert(Object value, List result, Root root, Object key) {
        convert(value, result, root, key, 0);
    }

    static int convert(Object value, List result, Root root, Object key,
                       int bindcount) {
        if (value == null) {
            result.add(Code.NULL);
        } else if (value instanceof Collection) {
            Collection c = (Collection) value;
            Code sql = new Code("(");
            for (Iterator it = c.iterator(); it.hasNext(); ) {
                List single = new ArrayList();
                bindcount = convert(it.next(), single, root, key, bindcount);
                if (single.size() != 1) {
                    throw new IllegalStateException
                        ("can't deal with collection of compound objects");
                }
                sql = sql.add((Code) single.get(0));
                if (it.hasNext()) {
                    sql = sql.add(",");
                } else {
                    sql = sql.add(")");
                }
            }
            result.add(sql);
        } else {
            Adapter ad = root.getAdapter(value.getClass());
            PropertyMap pmap = ad.getProperties(value);
            if (pmap.getObjectType().isCompound()) {
                bindcount = convert(pmap, result, root, key, bindcount);
            } else {
                Object k = key == null ? null :
                    new CompoundKey(key, new Integer(bindcount));
                Code.Binding b = new Code.Binding
                    (k, value, ad.defaultJDBCType());
                result.add(new Code("?", Collections.singletonList(b)));
                bindcount++;
            }
        }

        return bindcount;
    }

    private static int convert(PropertyMap pmap, List result, Root root,
                               Object key, int bindcount) {
        Collection props = Code.properties(pmap.getObjectType());
        for (Iterator it = props.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            bindcount = convert(pmap.get(prop), result, root, key, bindcount);
        }

        return bindcount;
    }

    public String toString() {
        if (m_value instanceof String) {
            return "\"" + m_value + "\"";
        } else {
            return "" + m_value;
        }
    }

    String summary() {
        return "" + this;
    }

}
