package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import java.util.*;
import javax.jdo.PersistenceManager;
import javax.jdo.spi.JDOImplHelper;
import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.StateManager;

import org.apache.log4j.Logger;

class StateManagerImpl implements StateManager {
    private final static Logger s_log =
        Logger.getLogger(StateManagerImpl.class);

    private PersistenceManagerImpl m_pmi = null;

    // XXX temporary storage for replaceField/replacingField
    private Object m_tmpValue = null;

    StateManagerImpl(PersistenceManagerImpl pmi) {
        if (pmi == null) { throw new NullPointerException("pmi"); }

        m_pmi = pmi;
    }

    private final Session ssn() {
        return m_pmi.getSession();
    }


    /*
     * XXX: This mapping needs to be computed at startup and cached.
     **/
    private static List getAllFields(Class pcClass) {
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
    private static List getAllTypes(Class pcClass) {
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

    private static String numberToName(Class pcClass, int field) {
        return (String) getAllFields(pcClass).get(field);
    }

    /**
     * Returns the first occurrence of the specified field in the most derived
     * class.
     **/
    private static int nameToNumber(Class pcClass, String fieldName) {
        return getAllFields(pcClass).lastIndexOf(fieldName);
    }

    void cacheKeyProperties(PersistenceCapable pc, PropertyMap pmap) {
        List props = getAllFields(pc.getClass());

        for (Iterator it = pmap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            Property prop = (Property) me.getKey();
            int index = props.indexOf(prop.getName());
            Object value = me.getValue();
            // XXX: should null values be allowed?
            if (value != null) {
                try {
                    m_tmpValue = value;
                    pc.jdoReplaceField(index);
                } finally {
                    m_tmpValue = null;
                }
            }
        }
    }

    /**
     * Return the object representing the JDO identity of the calling
     * instance.
     */
    public Object getObjectId(PersistenceCapable pc) {
        Object id = JDOImplHelper.getInstance().
            newObjectIdInstance(pc.getClass());
        pc.jdoCopyKeyFieldsToObjectId(id);
        return id;
    }

    /**
     * Return the PersistenceManager that owns this instance.
     */
    public PersistenceManager getPersistenceManager(PersistenceCapable pc) {
        return m_pmi;
    }

    private ObjectType type(PersistenceCapable pc) {
        if (pc == null) { throw new NullPointerException("pc"); }

        return ssn().getRoot().getObjectType(pc.getClass().getName());
    }

    private Property prop(PersistenceCapable pc, int field) {
        ObjectType type = type(pc);

        String name = numberToName(pc.getClass(), field);

        Property prop = type.getProperty(name);

        if (prop == null) {
            throw new IllegalStateException("no " + name + " in " +  type);
        }

        return prop;
    }

    /**
     * Return the value for the field.
     */
    public Object getObjectField(PersistenceCapable pc, int field,
                                 Object currentValue) {

        Class klass = (Class) getAllTypes(pc.getClass()).get(field);

        if (klass.equals(Collection.class)) {
            return new CRPSet(ssn(), pc, prop(pc, field));
        } else {
            return ssn().get(pc, prop(pc, field));
        }
    }

    public boolean getBooleanField(PersistenceCapable pc, int field,
                                   boolean currentValue) {
        return ((Boolean) getObjectField(pc, field, null)).booleanValue();
    }

    public byte getByteField(PersistenceCapable pc, int field,
                             byte currentValue) {
        return ((Byte) getObjectField(pc, field, null)).byteValue();
    }

    public char getCharField(PersistenceCapable pc, int field,
                             char currentValue) {
        return ((Character) getObjectField(pc, field, null)).charValue();
    }

    public double getDoubleField(PersistenceCapable pc, int field,
                                 double currentValue) {
        return ((Double) getObjectField(pc, field, null)).doubleValue();
    }

    public float getFloatField(PersistenceCapable pc, int field,
                               float currentValue) {
        return ((Float) getObjectField(pc, field, null)).floatValue();
    }

    public int getIntField(PersistenceCapable pc, int field,
                           int currentValue) {
        return ((Integer) getObjectField(pc, field, null)).intValue();
    }

    public long getLongField(PersistenceCapable pc, int field,
                             long currentValue) {
        return ((Long) getObjectField(pc, field, null)).longValue();
    }

    public short getShortField(PersistenceCapable pc, int field,
                               short currentValue) {
        return ((Short) getObjectField(pc, field, null)).shortValue();
    }

    public String getStringField(PersistenceCapable pc, int field,
                                 String currentValue) {
        return (String) getObjectField(pc, field, null);
    }

    /**
     * Return the object representing the JDO identity of the calling
     * instance.
     */
    public Object getTransactionalObjectId(PersistenceCapable pc) {
        return getObjectId(pc);
    }

    /**
     * Tests whether this object has been deleted.
     */
    public boolean isDeleted(PersistenceCapable pc) {
        return ssn().isDeleted(pc);
    }

    /**
     * Tests whether this object is dirty.
     */
    public boolean isDirty(PersistenceCapable pc) {
        // XXX: semantics for new objects
        return ssn().isModified(pc);
    }

    /**
     * Return true if the field is cached in the calling instance.
     */
    public boolean isLoaded(PersistenceCapable pc, int field) {
        return prop(pc, field).isKeyProperty();
    }

    /**
     * Tests whether this object has been newly made persistent.
     */
    public boolean isNew(PersistenceCapable pc) {
        return ssn().isNew(pc);
    }

    /**
     * Tests whether this object is persistent.
     */
    public boolean isPersistent(PersistenceCapable pc) {
        throw new Error("not implemented");
    }

    /**
     * Tests whether this object is transactional.
     */
    public boolean isTransactional(PersistenceCapable pc) {
        throw new Error("not implemented");
    }

    /**
     * Mark the associated PersistenceCapable field dirty.
     */
    public void makeDirty(PersistenceCapable pc, String fieldName) {
        throw new Error("not implemented");
    }

    /**
     * Guarantee that the serializable transactional and persistent fields are
     * loaded into the instance.
     */
    public void preSerialize(PersistenceCapable pc) {
        throw new Error("not implemented");
    }

    /**
     * The value of the field requested to be provided to the StateManager
     */
    public void providedObjectField(PersistenceCapable pc, int field,
                                    Object currentValue) {
        throw new Error("not implemented");
    }

    public void providedBooleanField(PersistenceCapable pc, int field,
                                     boolean currentValue) {
        throw new Error("not implemented");
    }

    public void providedByteField(PersistenceCapable pc, int field,
                                  byte currentValue) {
        throw new Error("not implemented");
    }

    public void providedCharField(PersistenceCapable pc, int field,
                                  char currentValue) {
        throw new Error("not implemented");
    }

    public void providedDoubleField(PersistenceCapable pc, int field,
                                    double currentValue) {
        throw new Error("not implemented");
    }

    public void providedFloatField(PersistenceCapable pc, int field,
                                   float currentValue) {
        throw new Error("not implemented");
    }

    public void providedIntField(PersistenceCapable pc, int field,
                                 int currentValue) {
        throw new Error("not implemented");
    }

    public void providedLongField(PersistenceCapable pc, int field,
                                  long currentValue) {
        throw new Error("not implemented");
    }

    public void providedShortField(PersistenceCapable pc, int field,
                                   short currentValue) {
        throw new Error("not implemented");
    }

    public void providedStringField(PersistenceCapable pc, int field,
                                    String currentValue) {
        throw new Error("not implemented");
    }

    /**
     * The owning StateManager uses this method to supply the value of the
     * flags to the PersistenceCapable instance.
     */
    public byte replacingFlags(PersistenceCapable pc) {
        // sec 21.9 of jdo1.0.1 spec
        return PersistenceCapable.LOAD_REQUIRED;
    }

    /**
     * The replacing value of the field in the calling instance
     */
    public Object replacingObjectField(PersistenceCapable pc, int field) {
        return m_tmpValue;
    }

    public boolean replacingBooleanField(PersistenceCapable pc, int field) {
        return ((Boolean) replacingObjectField(pc, field)).booleanValue();
    }

    public byte replacingByteField(PersistenceCapable pc, int field) {
        return ((Byte) replacingObjectField(pc, field)).byteValue();
    }

    public char replacingCharField(PersistenceCapable pc, int field) {
        return ((Character) replacingObjectField(pc, field)).charValue();
    }

    public double replacingDoubleField(PersistenceCapable pc, int field) {
        return ((Double) replacingObjectField(pc, field)).doubleValue();
    }

    public float replacingFloatField(PersistenceCapable pc, int field) {
        return ((Float) replacingObjectField(pc, field)).floatValue();
    }

    public int replacingIntField(PersistenceCapable pc, int field) {
        return ((Integer) replacingObjectField(pc, field)).intValue();
    }

    public long replacingLongField(PersistenceCapable pc, int field) {
        return ((Long) replacingObjectField(pc, field)).longValue();
    }

    public short replacingShortField(PersistenceCapable pc, int field) {
        return ((Short) replacingObjectField(pc, field)).shortValue();
    }

    public String replacingStringField(PersistenceCapable pc, int field) {
        return (String) replacingObjectField(pc, field);
    }

    /**
     * Replace the current value of jdoStateManager.
     */
    public StateManager replacingStateManager(PersistenceCapable pc,
                                              StateManager sm) {
        // XXX: what to do here?
        return sm;
    }

    /**
     * Mark the field as modified by the user.
     */
    public void setObjectField(PersistenceCapable pc, int field,
                               Object currentValue, Object newValue) {
        ssn().set(pc, prop(pc, field), newValue);
    }

    public void setBooleanField(PersistenceCapable pc, int field,
                                boolean currentValue, boolean newValue) {
        setObjectField
            (pc, field, (currentValue ? Boolean.TRUE : Boolean.FALSE),
             (newValue ? Boolean.TRUE : Boolean.FALSE));
    }

    public void setByteField(PersistenceCapable pc, int field,
                             byte currentValue, byte newValue) {
        setObjectField
            (pc, field, new Byte(currentValue), new Byte(newValue));
    }

    public void setCharField(PersistenceCapable pc, int field,
                             char currentValue, char newValue) {
        setObjectField
            (pc, field, new Character(currentValue), new Character(newValue));
    }

    public void setDoubleField(PersistenceCapable pc, int field,
                               double currentValue, double newValue) {
        setObjectField
            (pc, field, new Double(currentValue), new Double(newValue));
    }

    public void setFloatField(PersistenceCapable pc, int field,
                              float currentValue, float newValue) {
        setObjectField
            (pc, field, new Float(currentValue), new Float(newValue));
    }

    public void setIntField(PersistenceCapable pc, int field,
                            int currentValue, int newValue) {
        setObjectField
            (pc, field, new Integer(currentValue), new Integer(newValue));
    }

    public void setLongField(PersistenceCapable pc, int field,
                             long currentValue, long newValue) {
        setObjectField(pc, field, new Long(currentValue), new Long(newValue));
    }

    public void setShortField(PersistenceCapable pc, int field,
                              short currentValue, short newValue) {
        setObjectField
            (pc, field, new Short(currentValue), new Short(newValue));
    }

    public void setStringField(PersistenceCapable pc, int field,
                               String currentValue, String newValue) {
        setObjectField(pc, field, currentValue, newValue);
    }
}
