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

import org.myrian.persistence.*;
import org.myrian.persistence.common.*;
import org.myrian.persistence.metadata.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Literal
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/10/04 $
 **/

public class Literal extends Expression {


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

                PropertyMap pmap = gen.getSession().getProperties(value);
                for (Iterator it = pmap.entrySet().iterator();
                     it.hasNext(); ) {
                    Map.Entry me = (Map.Entry) it.next();
                    Property prop = (Property) me.getKey();
                    Object obj = me.getValue();
                    bindcount = convert(obj, result, gen, key, bindcount);
                }
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

    public String toString() {
        if (m_value instanceof String) {
            return "\"" + m_value + "\"";
        } else {
            return "" + Session.str(m_value);
        }
    }

    String summary() {
        return "" + this;
    }

}
