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
 * @version $Revision: #4 $ $Date: 2004/08/26 $
 **/

public class Literal extends Expression {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Literal.java#4 $ by $Author: ashah $, $DateTime: 2004/08/26 14:07:44 $";

    private Object m_value;

    public Literal(Object value) {
        m_value = value;
    }

    private ObjectMap map(Generator gen) {
        return map(gen, m_value);
    }

    private static ObjectMap map(Generator gen, Object value) {
        if (value == null || value instanceof Collection) {
            return null;
        } else {
            Adapter ad = gen.getRoot().getAdapter(value.getClass());
            ObjectType type = ad.getObjectType(value);
            Session ssn = gen.getSession();
            if (ssn.hasObjectMap(value)) {
                return ssn.getObjectMap(value);
            } else if (type.isPrimitive()) {
                return new ObjectMap(type);
            } else {
                return null;
            }
        }
    }

    private Object value(ObjectMap map) {
        if (m_value instanceof Collection) {
            return m_value;
        } else if (map == null) {
            return null;
        } else {
            return m_value;
        }
    }

    void frame(Generator gen) {
        ObjectMap map = map(gen);
        QFrame frame = gen.frame(this, map);
        List result = new ArrayList();
        Object key = gen.level > 0 ? null : getBindKey(gen);
        Object value = value(map);
        convert(value, result, gen, key);
        if (result.size() == 0) {
            throw new IllegalStateException
                ("unable to convert value: " + value);
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
        ObjectMap map = map(gen);
        if (map != null && !map.isPrimitive()) { gen.hash(map); }
        Object value = value(map);
        List values = new ArrayList();
        convert(value, values, gen, getBindKey(gen));
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

    static void convert(Object value, List result, Generator gen, Object key) {
        convert(value, result, gen, key, 0);
    }

    static int convert(Object value, List result, Generator gen, Object key,
                       int bindcount) {
        if (value == null) {
            result.add(Code.NULL);
        } else if (value instanceof Collection) {
            Collection c = (Collection) value;
            Code sql = new Code("(");
            for (Iterator it = c.iterator(); it.hasNext(); ) {
                List single = new ArrayList();
                bindcount = convert(it.next(), single, gen, key, bindcount);
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
            ObjectMap map = map(gen, value);

            if (map.isCompound()) {
                if (map.isNested()) {
                    Object container = gen.getSession().getContainer(value);
                    bindcount =
                        convert(container, result, gen, key, bindcount);
                }
                bindcount = convert(map, value, result, gen, key, bindcount);
            } else {
                Adapter ad = gen.getRoot().getAdapter(value.getClass());
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

    private static int convert(ObjectMap map, Object value, List result,
                               Generator gen, Object key, int bindcount) {
        Session ssn = gen.getSession();
        Collection props = map.getKeyProperties();
        for (Iterator it = props.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            Object obj = ssn.get(value, prop);
            bindcount = convert(obj, result, gen, key, bindcount);
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
