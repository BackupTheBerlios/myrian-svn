package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import java.util.*;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.spi.JDOImplHelper;
import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.StateManager;

import org.apache.log4j.Logger;

class StateManagerImpl extends AbstractStateManager {

    private final static Logger s_log =
        Logger.getLogger(StateManagerImpl.class);

    private PersistenceManagerImpl m_pmi;
    private PropertyMap m_pmap;
    private String m_prefix;
    private Map m_components = new HashMap();

    StateManagerImpl(PersistenceManagerImpl pmi, PropertyMap pmap,
                     String prefix) {
        if (pmi == null) { throw new NullPointerException("pmi"); }
        if (pmap == null) { throw new NullPointerException("pmap"); }
        if (prefix == null) { throw new NullPointerException("prefix"); }
        m_pmi = pmi;
        m_pmap = pmap;
        m_prefix = prefix;
    }

    StateManagerImpl(PersistenceManagerImpl pmi, PropertyMap pmap) {
        this(pmi, pmap, "");
    }

    private final Session ssn() {
        return m_pmi.getSession();
    }

    PropertyMap getPropertyMap() {
        return m_pmap;
    }

    String getPrefix() {
        return m_prefix;
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
        return m_pmap.getObjectType();

        //        return ssn().getRoot().getObjectType(pc.getClass().getName());
    }

    private String name(PersistenceCapable pc, int field) {
        return m_prefix + C.numberToName(pc.getClass(), field);
    }

    private Property prop(PersistenceCapable pc, int field) {
        ObjectType type = type(pc);

        String name = name(pc, field);

        Property prop = type.getProperty(name);

        if (prop == null) {
            throw new IllegalStateException("no " + name + " in " +  type);
        }

        return prop;
    }

    private boolean isComponent(PersistenceCapable pc, int field) {
        return C.isComponent(type(pc), name(pc, field));
    }

    /**
     * Return the value for the field.
     */
    public Object getObjectField(PersistenceCapable pc, int field,
                                 Object currentValue) {
        if (isComponent(pc, field)) {
            String name = name(pc, field);
            if (m_components.containsKey(name)) {
                return m_components.get(name);
            } else {
                // XXX: what to do about currentValue
                Class klass = (Class) C.getAllTypes(pc.getClass()).get(field);
                if (klass.equals(List.class)) {
                    klass = CRPList.class;
                } else if (klass.equals(Map.class)) {
                    klass = CRPMap.class;
                }
                Object obj = m_pmi.newPC(m_pmap, klass, name + "$");
                m_components.put(name, obj);
                return obj;
            }
        } else {
            Property prop = prop(pc, field);

            if (prop.isKeyProperty()) {
                return getPropertyMap().get(prop);
            } else if (prop.isCollection()) {
                return new CRPSet(ssn(), ssn().retrieve(m_pmap), prop);
            } else {
                return ssn().get(ssn().retrieve(m_pmap), prop);
            }
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

    private void fill(Map values, StateManagerImpl smi, Class cls,
                      PersistenceCapable pc) {
        ObjectType type = m_pmap.getObjectType();
        List props = C.getAllFields(cls);
        List types = C.getAllTypes(cls);
        for (int i = 0; i < props.size(); i++) {
            String propName = smi.getPrefix() + ((String) props.get(i));
            Object obj = smi.provideField(pc, i);
            if (obj == null) { continue; }
            Class klass = (Class) types.get(i);
            if (C.isComponent(type, propName)) {
                if (obj instanceof PersistenceCapable) {
                    PersistenceCapable comp = (PersistenceCapable) obj;
                    StateManagerImpl csmi = m_pmi.newSM
                        (comp, m_pmap, propName + "$");
                    fill(values, csmi, klass, comp);
                } else {
                    List l = C.componentProperties(type, propName);
                    for (Iterator it = l.iterator(); it.hasNext(); ) {
                        Property prop = (Property) it.next();
                        values.put(prop, obj);
                    }
                }
            } else {
                Property prop = type.getProperty(propName);
                if (!prop.isKeyProperty()) {
                    values.put(prop, obj);
                }
            }
        }
    }

    private void handleComponent(PersistenceCapable pc, String name,
                                 Class klass) {
        Map m = new HashMap();
        StateManagerImpl csmi = m_pmi.newSM(pc, m_pmap, name + "$");
        fill(m, csmi, klass, pc);
        for (Iterator it = m.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            Property prop = (Property) me.getKey();
            Object value = me.getValue();
            if (value == null) { continue; }
            if (prop.isCollection()) {
                throw new IllegalStateException
                    ("collection properties on a component don't work");
            }
            if (value instanceof PersistenceCapable) {
                m_pmi.makePersistent
                    ((PersistenceCapable) value, prop.getType());
            }
            ssn().set(pc, prop, value);
        }
    }

    /**
     * Mark the field as modified by the user.
     */
    public void setObjectField(PersistenceCapable pc, int field,
                               Object currentValue, Object newValue) {
        if (isComponent(pc, field)) {
            String name = name(pc, field);

            // XXX: what to do about currentValue
            Class klass = (Class) C.getAllTypes(pc.getClass()).get(field);
            if (klass.equals(List.class) || klass.equals(Map.class)) {
                boolean isMap = klass.equals(Map.class);

                List props = C.componentProperties
                    (m_pmap.getObjectType(), name);

                if (props.size() > 1) { throw new Error("not implemented"); }

                Property prop = (Property) props.get(0);
                ssn().clear(pc, prop);
                if (newValue != null) {

                    Object obj = getObjectField(pc, field);
                    if (isMap) {
                        ((Map) obj).putAll((Map) newValue);
                    } else {
                        ((List) obj).addAll((List) newValue);
                    }
                }
            } else {
                if (newValue != null) {
                    if (newValue instanceof PersistenceCapable) {
                        handleComponent
                            ((PersistenceCapable) newValue, name, klass);
                    } else {
                        throw new IllegalStateException
                            ("new value " + newValue + " of class " +
                             newValue.getClass()
                             + " is not persistence capable");
                    }
                }
            }
        } else {
            Property prop = prop(pc, field);
            if (prop.isCollection()) {
                Collection c = (Collection) newValue;
                ssn().clear(pc, prop);
                if (c != null) {
                    for (Iterator it = c.iterator(); it.hasNext(); ) {
                        Object value = it.next();
                        if (value instanceof PersistenceCapable) {
                            m_pmi.makePersistent(value);
                        }
                        ssn().add(pc, prop, value);
                    }
                }
            } else {
                if (newValue instanceof PersistenceCapable) {
                    m_pmi.makePersistent(newValue);
                }
                ssn().set(pc, prop, newValue);
            }
        }
    }
}
