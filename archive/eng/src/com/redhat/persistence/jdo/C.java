package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import java.lang.reflect.*;
import java.util.*;
import javax.jdo.spi.*;

class C {

    public static Property prop(PersistenceCapable pc, int field) {
        ObjectType type = type(pc);

        String name =
            JDOImplHelper.getInstance().getFieldNames(pc.getClass())[field];

        Property prop = type.getProperty(name);

        if (prop == null) {
            throw new IllegalStateException("no " + name + " in " +  type);
        }

        return prop;
    }

    public static ObjectType type(PersistenceCapable pc) {
        PersistenceManagerImpl pmi = (PersistenceManagerImpl)
            pc.jdoGetPersistenceManager();
        if (pmi == null) { throw new IllegalStateException("pmi==null"); }

        Class cls = pc.getClass();
        Root root = pmi.getSession().getRoot();
        ObjectType type = root.getObjectType(cls.getName());

        return type;
    }

    public static StateManager getStateManager(PersistenceCapable pc) {
        Class cls = JDOImplHelper.getInstance().
            getPersistenceCapableSuperclass(pc.getClass());
        try {
            Field f = cls.getDeclaredField("jdoStateManager");
            return (StateManager) f.get(pc);
        } catch (NoSuchFieldException nsfe) {
            throw new IllegalStateException
                ("no jdoStateManager field in persistence capable superclass");
        } catch (IllegalAccessException iae) {
            throw new IllegalStateException
                ("jdoStateManager field is not accessible");
        }
    }

    public static Object javaGet(PersistenceCapable pc, Property prop) {
        Class cls = pc.getClass();
        String propName = prop.getName();
        Method m;
        try {
            m = cls.getMethod
                ("get" + propName.substring(0, 1).toUpperCase() +
                 propName.substring(1, propName.length()), new Class[] {});
        } catch (NoSuchMethodException nsme) {
            return null;
        }

        try {
            Object result = m.invoke(pc, new Object[] { });
            return result;
        } catch (IllegalAccessException iae) {
            throw new IllegalStateException
                ("could not access getter for " + propName);
        } catch (InvocationTargetException ite) {
            throw new Error(ite);
        }
    }

    public static PropertyMap pmap(PersistenceCapable pc, ObjectType type) {
        PropertyMap pmap = new PropertyMap(type);
        for (Iterator it = type.getKeyProperties().iterator();
             it.hasNext(); ) {
            Property prop = (Property) it.next();
            pmap.put(prop, C.javaGet(pc, prop));
        }

        return pmap;
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
