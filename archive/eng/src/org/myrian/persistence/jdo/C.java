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
package org.myrian.persistence.jdo;

import org.myrian.persistence.*;
import org.myrian.persistence.metadata.*;
import org.myrian.persistence.oql.Expression;
import org.myrian.persistence.oql.*;
import java.lang.reflect.*;
import java.util.*;
import javax.jdo.spi.*;

import org.apache.log4j.Logger;

class C {
    private final static Logger s_log = Logger.getLogger(C.class);


    // In Java 5.0, this family of concat methods can be replaced with a single
    // method that uses varargs.
    public static String concat(Object obj1, Object obj2) {
        return cat(obj1, obj2).toString();
    }

    public static String concat(Object obj1, Object obj2, Object obj3) {
        return cat(obj1, obj2).append(obj3).toString();
    }

    public static String concat(Object obj1, Object obj2, Object obj3,
                                Object obj4) {

        return cat(obj1, obj2)
            .append(String.valueOf(obj3))
            .append(String.valueOf(obj4))
            .toString();
    }

    public static String concat(Object obj1, Object obj2, Object obj3,
                                Object obj4, Object obj5) {

        return cat(obj1, obj2)
            .append(String.valueOf(obj3))
            .append(String.valueOf(obj4))
            .append(String.valueOf(obj5))
            .toString();
    }

    public static String concat(Object obj1, Object obj2, Object obj3,
                                Object obj4, Object obj5, Object obj6) {

        return cat(obj1, obj2)
            .append(String.valueOf(obj3))
            .append(String.valueOf(obj4))
            .append(String.valueOf(obj5))
            .append(String.valueOf(obj6))
            .toString();
    }

    private static StringBuffer cat(Object obj1, Object obj2) {
        return new StringBuffer()
            .append(String.valueOf(obj1))
            .append(String.valueOf(obj2));
    }

    public static String componentPropertyField(Property prop) {
        return prop.getName().substring(0, prop.getName().indexOf('$'));
    }

    public static boolean isComponentProperty(Property prop) {
        // at least one character before '$'
        return prop.getName().indexOf('$') > 0;
    }

    public static boolean isComponent(ObjectType type, String name) {
        if (type.hasProperty(name)) {
            return false;
        }
        for (Iterator it = type.getProperties().iterator(); it.hasNext(); ) {
            Property p = (Property) it.next();
            // XXX: is it really enough for p to merely start with name?
            if (p.getName().startsWith(name)) {
                return true;
            }
        }
        return false;
    }

    public static List componentProperties(ObjectType type, String name) {
        if (type.hasProperty(name)) {
            throw new IllegalArgumentException
                (name + " is a property of " + type.getQualifiedName());
        }
        List l = new ArrayList();
        for (Iterator it = type.getProperties().iterator(); it.hasNext(); ) {
            Property p = (Property) it.next();
            if (p.getName().startsWith(name)) {
                l.add(p);
            }
        }
        return l;
    }

    public static Cursor cursor(Session ssn, ObjectType type, Expression expr) {
        DataSet ds = new DataSet(ssn, new Signature(type), expr);
        return ds.getCursor();
    }

    public static Cursor cursor(Session ssn, Class klass, Expression expr) {
        ObjectType type = type(ssn, klass);
        return cursor(ssn, type, expr);
    }

    public static ObjectType type(Session ssn, Class cls) {
        return ssn.getRoot().getObjectType(cls.getName());
    }

    public static Cursor all(Session ssn, Class klass) {
        return cursor(ssn, klass, new All(klass.getName()));
    }

    public static void lock(Session ssn, Expression expr) {
        DataSet ds = new DataSet
            (ssn, new Signature() {
                public Query makeQuery(Session ssn, Expression e) {
                    return new Query(e, true);
                }
            }, expr);
        Cursor c = ds.getCursor();
        try { c.next(); }
        finally { c.close(); }
    }

    public static void lock(Session ssn, Object obj) {
        if (ssn.hasObjectMap(obj)) {
            ObjectMap map = ssn.getObjectMap(obj);
            ObjectType type = map.getObjectType();
            Expression expr = new Filter
                (new Define(new All(type.getQualifiedName()), "this"),
                 new Equals(new Variable("this"), new Literal(obj)));
            lock(ssn, expr);
        }
    }

    // used for application identity
    private static class PMapWriter implements
        PersistenceCapable.ObjectIdFieldConsumer {

        private PropertyMap m_pmap;
        private String[] m_props;

        PMapWriter(PersistenceCapable pc, PropertyMap pmap) {
            m_pmap = pmap;
            m_props = JDOImplHelper.getInstance().getFieldNames(pc.getClass());
        }

        public void storeBooleanField(int fieldNumber, boolean value) {
            throw new Error("not implemented");
        }
        public void storeByteField(int fieldNumber, byte value) {
            throw new Error("not implemented");
        }
        public void storeCharField(int fieldNumber, char value) {
            throw new Error("not implemented");
        }
        public void storeDoubleField(int fieldNumber, double value) {
            throw new Error("not implemented");
        }
        public void storeFloatField(int fieldNumber, float value) {
            throw new Error("not implemented");
        }
        public void storeIntField(int fieldNumber, int value) {
            throw new Error("not implemented");
        }
        public void storeLongField(int fieldNumber, long value) {
            throw new Error("not implemented");
        }
        public void storeObjectField(int fieldNumber, Object value) {
            m_pmap.put(m_pmap.getObjectType().getProperty
                       (m_props[fieldNumber]), value);
        }
        public void storeShortField(int fieldNumber, short value) {
            throw new Error("not implemented");
        }
        public void storeStringField(int fieldNumber, String value) {
            storeObjectField(fieldNumber, value);
        }
    }
}
