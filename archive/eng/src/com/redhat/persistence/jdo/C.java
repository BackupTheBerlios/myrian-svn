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
package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.oql.*;
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
        Root root = ssn.getRoot();
        ObjectType type = root.getObjectType(klass.getName());
        return cursor(ssn, type, expr);
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
        Root root = ssn.getRoot();
        Adapter ad = root.getAdapter(obj.getClass());
        ObjectType type = ad.getObjectType(obj);
        Expression expr = new Filter
            (new Define(new All(type.getQualifiedName()), "this"),
             new Equals(new Variable("this"), new Literal(obj)));
        lock(ssn, expr);
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
