package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import java.lang.reflect.*;
import java.util.*;
import javax.jdo.spi.*;

import org.apache.log4j.Logger;

class C {
    private final static Logger s_log = Logger.getLogger(C.class);

    /*
     * XXX: This mapping needs to be computed at startup and cached.
     **/
    public static List getAllFields(Class pcClass) {
        List fields = new ArrayList();
        JDOImplHelper helper = JDOImplHelper.getInstance();

        for (Class klass=pcClass;
             klass != null;
             klass = helper.getPersistenceCapableSuperclass(klass)) {

            String[] names = helper.getFieldNames(klass);
            if (names.length == 0) { continue; }
            List current = new ArrayList(names.length);
            // Yes, I know about Arrays.asList(Object[]).  It returns a list
            // that doesn't implement addAll.
            for (int ii=0; ii<names.length; ii++) {
                current.add(names[ii]);
            }
            current.addAll(fields);
            fields = current;
        }
        return fields;
    }

    /*
     * XXX: This mapping needs to be computed at startup and cached.
     * XXX: This is very similar to getAllFields.
     **/
    public static List getAllTypes(Class pcClass) {
        List types = new ArrayList();
        JDOImplHelper helper = JDOImplHelper.getInstance();

        for (Class klass=pcClass;
             klass != null;
             klass = helper.getPersistenceCapableSuperclass(klass)) {

            Class[] names = helper.getFieldTypes(klass);
            if (names.length == 0) { continue; }
            List current = new ArrayList(names.length);
            // Yes, I know about Arrays.asList(Object[]).  It returns a list
            // that doesn't implement addAll.
            for (int ii=0; ii<names.length; ii++) {
                current.add(names[ii]);
            }
            current.addAll(types);
            types = current;
        }
        return types;
    }

    public static String numberToName(Class pcClass, int field) {
        return (String) getAllFields(pcClass).get(field);
    }

    /**
     * Returns the first occurrence of the specified field in the most derived
     * class.
     **/
    public static int nameToNumber(Class pcClass, String fieldName) {
        return getAllFields(pcClass).lastIndexOf(fieldName);
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
