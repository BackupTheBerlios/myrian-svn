package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import java.util.*;
import javax.jdo.PersistenceManager;
import javax.jdo.spi.JDOImplHelper;
import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.StateManager;

import org.apache.log4j.Logger;

class StateManagerImpl extends AbstractStateManager {

    private final static Logger s_log =
        Logger.getLogger(StateManagerImpl.class);

    private PersistenceManagerImpl m_pmi = null;
    private PropertyMap m_pmap = null;

    StateManagerImpl(PersistenceManagerImpl pmi, PropertyMap pmap) {
        if (pmi == null) { throw new NullPointerException("pmi"); }
        if (pmap == null) { throw new NullPointerException("pmap"); }
        m_pmi = pmi;
        m_pmap = pmap;
    }

    private final Session ssn() {
        return m_pmi.getSession();
    }

    PropertyMap getPropertyMap() {
        return m_pmap;
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

        String name = C.numberToName(pc.getClass(), field);

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
        Property prop = prop(pc, field);
        Class type = (Class) C.getAllTypes(pc.getClass()).get(field);

        if (prop.isKeyProperty()) {
            return getPropertyMap().get(prop);
        } else if (type.equals(Collection.class)) {
            return new CRPSet(ssn(), pc, prop);
        } else if (type.equals(List.class)) {
            return new CRPList(ssn(), pc, prop);
        } else {
            return ssn().get(pc, prop);
        }
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
        return false;
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
     * Replace the current value of jdoStateManager.
     */
    public StateManager replacingStateManager(PersistenceCapable pc,
                                              StateManager sm) {
        return sm;
    }

    /**
     * Mark the field as modified by the user.
     */
    public void setObjectField(PersistenceCapable pc, int field,
                               Object currentValue, Object newValue) {
        ssn().set(pc, prop(pc, field), newValue);
    }
}
