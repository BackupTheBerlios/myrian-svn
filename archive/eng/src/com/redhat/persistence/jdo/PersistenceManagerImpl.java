package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.oql.Expression;
import com.redhat.persistence.profiler.rdbms.StatementProfiler;
import org.apache.commons.collections.map.AbstractReferenceMap;
import org.apache.commons.collections.map.ReferenceIdentityMap;
import java.sql.Connection;
import java.util.*;
import javax.jdo.*;
import javax.jdo.spi.*;

import org.apache.log4j.Logger;

public class PersistenceManagerImpl implements PersistenceManager, ClassInfo {
    private static final Logger s_log =
        Logger.getLogger(PersistenceManagerImpl.class);

    static final String ATTR_NAME = PersistenceManagerImpl.class.getName();

    /**
     * For new objects, a dataquery matching the name of the type with this
     * constant appended will be used to retrieve a new id value. If the data
     * query is not found, it is assumed the id properties are set and stored
     * in the PC instance.
     */
    public static final String ID_GEN = "$Gen";

    private Session m_ssn;
    private Transaction m_txn = new TransactionImpl(this);
    private Object m_userObject = null;
    private final PersistenceManagerFactoryImpl m_pmf;
    private final StatementProfiler m_prof;
    private final ClassInfo m_classInfo;

    private Map m_smiMap = new ReferenceIdentityMap
        (AbstractReferenceMap.WEAK, AbstractReferenceMap.HARD, true);

    public PersistenceManagerImpl(PersistenceManagerFactoryImpl pmf,
                                  Session ssn, StatementProfiler prof,
                                  ClassInfo classInfo) {
        m_pmf = pmf;
        m_ssn = ssn;
        m_ssn.setAttribute(ATTR_NAME, this);
        m_prof = prof;
        m_classInfo = classInfo;
    }

    StateManagerImpl getStateManager(Object pc) {
        return (StateManagerImpl) m_smiMap.get(pc);
    }

    private boolean hasStateManager(Object pc) {
        return m_smiMap.containsKey(pc);
    }

    PersistenceCapable newPC(PropertyMap pmap) {
        return newPC(pmap, pmap.getObjectType().getJavaClass(), null, "");
    }

    PersistenceCapable newPC(PropertyMap pmap, Class klass,
                             PersistenceCapable container, String prefix) {
        PersistenceCapable pc = JDOImplHelper.getInstance().newInstance
            (klass, null);
        StateManagerImpl smi = new StateManagerImpl
            (this, pmap, container == null ? pc : container, prefix);
        pc.jdoReplaceStateManager(smi);
        m_smiMap.put(pc, smi);
        return pc;
    }

    StateManagerImpl newSM(PersistenceCapable pc, PropertyMap pmap) {
        return newSM(pc, pmap, pc, "");
    }

    StateManagerImpl newSM(PersistenceCapable pc, PropertyMap pmap,
                           PersistenceCapable container, String prefix) {
        StateManagerImpl smi = new StateManagerImpl
            (this, pmap, container, prefix);
        // set pmap here for objects requiring id gen
        m_smiMap.put(pc, smi);
        pc.jdoReplaceStateManager(smi);
        return smi;
    }

    private PropertyMap pmap(PersistenceCapable pc, ObjectType type) {
        if (hasStateManager(pc)) {
            return getStateManager(pc).getPropertyMap();
        }

        PropertyMap pmap = new PropertyMap(type);
        Class cls = pc.getClass();

        Root root = m_ssn.getRoot();
        ObjectType gen = root.getObjectType(type.getQualifiedName() + ID_GEN);
        if (gen != null) {
            DataSet ds = m_ssn.getDataSet(gen);
            Cursor c = ds.getCursor();
            if (c.next()) {
                for (Iterator it = type.getKeyProperties().iterator();
                     it.hasNext(); ) {
                    Property prop = (Property) it.next();
                    Object value = c.get(prop.getName());
                    pmap.put(prop, value);
                }
            }
            c.close();
        } else {
            try {
                BaseStateManager smTemp = new BaseStateManager();
                pc.jdoReplaceStateManager(smTemp);

                List props = m_classInfo.getAllFields(cls);
                for (int i = 0; i < props.size(); i++) {
                    String propName = (String) props.get(i);
                    // XXX: right now we ignore the possibility of a
                    // component being a key
                    if (C.isComponent(type, propName)) { continue; }
                    Property prop = type.getProperty(propName);
                    if (prop == null) {
                        throw new Error("no " + propName + " in " + type);
                    }
                    if (prop.isKeyProperty()) {
                        pmap.put(prop, smTemp.provideField(pc, i));
                    }
                }
            } finally {
                pc.jdoReplaceStateManager(null);
            }
        }

        return pmap;
    }

    final Session getSession() {
        return m_ssn;
    }

    /**
     * Close this PersistenceManager so that no further requests may be made
     * on it.
     */
    public void close() {
        s_log.debug("close(): m_ssn = null");
        m_ssn = null;
        m_smiMap.clear();
    }

    void commit() {
        for (Iterator smis=m_smiMap.values().iterator(); smis.hasNext(); ) {
            StateManagerImpl smi = (StateManagerImpl) smis.next();
            smi.getState().commit(false);
        }
    }

    /**
     * Return the Transaction instance associated with a PersistenceManager.
     */
    public Transaction currentTransaction() {
        return m_txn;
    }

    /**
     * Delete the persistent instance from the data store.
     */
    public void deletePersistent(Object obj) {
        PersistenceCapable pc = checkAndCast(obj);
        requireActiveTxn();

        StateManagerImpl smi = getStateManager(pc);
        if (smi == null) {
            throw new JDOFatalInternalException
                ("implementation error: null smi for pc=" + pc);
        }
        smi.getState().deletePersistent();
        m_ssn.delete(pc);
    }

    public void deletePersistentAll(Collection pcs) {
        for (Iterator it = pcs.iterator(); it.hasNext(); ) {
            deletePersistent(it.next());
        }
    }

    public void deletePersistentAll(Object[] pcs) {
        for (int i = 0; i < pcs.length; i++) {
            deletePersistent(pcs[i]);
        }
    }

    /**
     * Mark an instance as no longer needed in the cache.
     */
    public void evict(Object pc) {
        throw new Error("not implemented");
    }

    public void evictAll(Object[] pcs) {
        throw new Error("not implemented");
    }

    public void evictAll(Collection pcs) {
        throw new Error("not implemented");
    }

    /**
     * Mark all persistent-nontransactional instances as no longer needed in
     * the cache.
     */
    public void evictAll() {
        throw new Error("not implemented");
    }


    /**
     * The PersistenceManager manages a collection of instances in the data
     * store based on the class of the instances.
     */
    public Extent getExtent(Class persistenceCapableClass,
                            boolean subclasses) {
        throw new Error("not implemented");
    }

    /**
     * Get the ignoreCache setting for queries.
     */
    public boolean getIgnoreCache() {
        return false;
    }


    /**
     * Get the current Multithreaded flag for this PersistenceManager.
     */
    public boolean getMultithreaded() {
        return false;
    }

    /**
     * This method locates a persistent instance in the cache of instances
     * managed by this PersistenceManager.
     */
    public Object getObjectById(Object oid, boolean validate) {
        // XXX: handle validate flag
        // XXX: assuming datastore identity
        PropertyMap pmap = (PropertyMap) oid;
        return m_ssn.retrieve(pmap);
    }

    /**
     * The ObjectId returned by this method represents the JDO identity of the
     * instance.
     */
    public Object getObjectId(Object obj) {
        PersistenceCapable pc = (PersistenceCapable) obj;
        if (!hasStateManager(obj)) {
            throw new JDOUserException(obj + " has no state manager");
        }
        return getStateManager(pc).getPropertyMap();
    }

    /**
     * Return the Class that implements the JDO Identity for the specified
     * PersistenceCapable class.
     */
    public Class getObjectIdClass(Class cls) {
        throw new Error("not implemented");
    }

    /**
     * This method returns the PersistenceManagerFactory used to create this
     * PersistenceManager.
     */
    public PersistenceManagerFactory getPersistenceManagerFactory() {
        return m_pmf;
    }

    /**
     * The ObjectId returned by this method represents the JDO identity of the
     * instance.
     */
    public Object getTransactionalObjectId(Object pc) {
        throw new Error("not implemented");
    }

    /**
     * The application can manage the PersistenceManager instances more easily
     * by having an application object associated with each PersistenceManager
     * instance.
     */
    public Object getUserObject() {
        return m_userObject;
    }

    /**
     * A PersistenceManager instance can be used until it is closed.
     */
    public boolean isClosed() {
        return m_ssn == null;
    }

    /**
     * Make an instance non-transactional after commit.
     */
    public void makeNontransactional(Object pc) {
        throw new JDOUnsupportedOptionException("nontransactional");
    }

    public void makeNontransactionalAll(Collection pcs) {
        throw new JDOUnsupportedOptionException("nontransactional");
    }

    public void makeNontransactionalAll(Object[] pcs) {
        throw new JDOUnsupportedOptionException("nontransactional");
    }

    private void requireActiveTxn() {
        if (!m_txn.isActive()) {
            throw new JDOUserException("No active transaction");
        }
    }

    private PersistenceCapable checkAndCast(Object obj) {
        if (obj == null) { throw new NullPointerException("obj"); }

        if (!(obj instanceof PersistenceCapable)) {
            throw new ClassCastException
                ("Expected " + obj.getClass().getName() + " to implement " +
                 PersistenceCapable.class.getName());
        }
        return (PersistenceCapable) obj;
    }

    /**
     * Make the transient instance persistent in this PersistenceManager.
     */
    public void makePersistent(Object obj) {
        PersistenceCapable pc = checkAndCast(obj);

        Class cls = pc.getClass();
        Root root = m_ssn.getRoot();

        // XXX: This rests on the assumption that the Java class and the
        // corresponding object type have the same name.
        ObjectType type = root.getObjectType(cls.getName());
        if (type == null) {
            throw new IllegalStateException("no type for " + cls.getName());
        }
        makePersistent(pc, type);
    }

    void makePersistent(PersistenceCapable pc, ObjectType type) {
        requireActiveTxn();
        Class cls = pc.getClass();
        if (!hasStateManager(pc)) {
            PropertyMap pmap = pmap(pc, type);
            StateManagerImpl smi = newSM(pc, pmap);

            Map values = new HashMap();

            m_ssn.create(pc);

            List props = m_classInfo.getAllFields(cls);
            for (int i = 0; i < props.size(); i++) {
                String propName = smi.getPrefix() + ((String) props.get(i));
                if (C.isComponent(type, propName)
                    || !type.hasProperty(propName)
                    || !type.getProperty(propName).isKeyProperty()) {
                    smi.setObjectField(pc, i, null, smi.provideField(pc, i));
                }
            }
        }
        StateManagerImpl smi = getStateManager(pc);
        smi.getState().makePersistent();
    }

    /**
     * Make a Collection of instances persistent.
     */
    public void makePersistentAll(Collection pcs) {
        makePersistentAll(pcs.toArray());
    }

    /**
     * Make an array of instances persistent.
     */
    public void makePersistentAll(Object[] pcs) {
        List exceptions = null;

        for (int i = 0; i < pcs.length; i++) {
            try {
                makePersistent(pcs[i]);
            } catch (Exception ex) {
                if (exceptions == null) {
                    exceptions = new LinkedList();
                }
                exceptions.add(ex);
            }
        }

        if (exceptions != null && exceptions.size() > 0) {
            Throwable[] nested = (Throwable[]) exceptions.toArray
                (new Throwable[exceptions.size()]);
            throw new JDOUserException("failed to make all objects persistent",
                                       nested);
        }
    }

    /**
     * Make an instance subject to transactional boundaries.
     */
    public void makeTransactional(Object pc) {
        throw new JDOUnsupportedOptionException
            ("nontransactional and transient-transactional");
    }

    /**
     * Make a Collection of instances subject to transactional boundaries.
     */
    public void makeTransactionalAll(Collection pcs) {
        throw new JDOUnsupportedOptionException
            ("nontransactional and transient-transactional");
    }

    /**
     * Make an array of instances subject to transactional boundaries.
     */
    public void makeTransactionalAll(Object[] pcs) {
         throw new JDOUnsupportedOptionException
            ("nontransactional and transient-transactional");
    }

    /**
     * Make an instance transient, removing it from management by this
     * PersistenceManager.
     */
    public void makeTransient(Object pc) {
        throw new Error("not implemented");
    }

    /**
     * Make a Collection of instances transient, removing them from management
     * by this PersistenceManager.
     */
    public void makeTransientAll(Collection pcs) {
        throw new Error("not implemented");
    }

    /**
     * Make an array of instances transient, removing them from management by
     * this PersistenceManager.
     */
    public void makeTransientAll(Object[] pcs) {
        throw new Error("not implemented");
    }

    /**
     * This method returns an object id instance corresponding to the Class
     * and String arguments.
     */
    public Object newObjectIdInstance(Class pcClass, String str) {
        throw new Error("not implemented");
    }

    /**
     * Create a new Query with no elements.
     */
    public Query newQuery() {
        return new JDOQuery(this);
    }

    /**
     * Create a new Query specifying the Class of the candidate instances.
     */
    public Query newQuery(Class cls) {
        Query q = newQuery();
        q.setClass(cls);
        return q;
    }

    /**
     * Create a new Query with the candidate Extent; the class is taken from
     * the Extent.
     */
    public Query newQuery(Class cls, Collection cln) {
        throw new Error("not implemented");
    }

    /**
     * Create a new Query with the Class of the candidate instances, candidate
     * Collection, and filter.
     */
    public Query newQuery(Class cls, Collection cln, String filter) {
        throw new Error("not implemented");
    }

    /**
     * Create a new Query with the Class of the candidate instances and
     * filter.
     */
    public Query newQuery(Class cls, String filter) {
        Query q = newQuery(cls);
        q.setFilter(filter);
        return q;
    }

    /**
     * Create a new Query with the Class of the candidate instances and
     * candidate Extent.
     */
    public Query newQuery(Extent cln) {
        throw new Error("not implemented");
    }

    /**
     * Create a new Query with the candidate Extent and filter; the class is
     * taken from the Extent.
     */
    public Query newQuery(Extent cln, String filter) {
        throw new Error("not implemented");
    }

    /**
     * Create a new Query using elements from another Query.
     */
    public Query newQuery(Object compiled) {
        throw new Error("not implemented");
    }

    /**
     * Create a new Query using the specified language.
     */
    public Query newQuery(String language, final Object query) {
        if (Extensions.OQL.equals(language)) {
            return new JDOQuery(this, (String) query);
        }

        throw new JDOUserException
            ("Language " + language + " is not supported");
    }

    /**
     * Refresh the state of the instance from the data store.
     */
    public void refresh(Object pc) {
        throw new Error("not implemented");
    }

    /**
     * Refresh the state of all applicable instances from the data store.
     */
    public void refreshAll() {
        throw new Error("not implemented");
    }

    /**
     * Refresh the state of a Collection of instances from the data store.
     */
    public void refreshAll(Collection pcs) {
        throw new Error("not implemented");
    }

    /**
     * Refresh the state of an array of instances from the data store.
     */
    public void refreshAll(Object[] pcs) {
        throw new Error("not implemented");
    }

    /**
     * Retrieve an instance from the store.
     */
    public void retrieve(Object pc) {
        throw new Error("not implemented");
    }

    /**
     * Retrieve instances from the store.
     */
    public void retrieveAll(Collection pcs) {
        throw new Error("not implemented");
    }

    /**
     * Retrieve instances from the store.
     */
    public void retrieveAll(Object[] pcs) {
        throw new Error("not implemented");
    }

    /**
     * Not documented at
     * http://java.sun.com/products/jdo/javadocs/javax/jdo/PersistenceManager.html
     */
    public void retrieveAll(Object[] pcs, boolean validate) {
        throw new Error("not implemented");
    }

    /**
     * Not documented at
     * http://java.sun.com/products/jdo/javadocs/javax/jdo/PersistenceManager.html
     */
    public void retrieveAll(Collection pcs, boolean validate) {
        throw new Error("not implemented");
    }

    /**
     * Set the ignoreCache parameter for queries.
     */
    public void setIgnoreCache(boolean flag) {
        throw new Error("not implemented");
    }

    /**
     * Set the Multithreaded flag for this PersistenceManager.
     */
    public void setMultithreaded(boolean flag) {
        throw new Error("not implemented");
    }

    /**
     * The application can manage the PersistenceManager instances more easily
     * by having an application object associated with each PersistenceManager
     * instance.
     */
    public void setUserObject(Object o) {
        m_userObject = o;
    }

    public void startProfiling() {
        if (m_prof == null) {
            throw new JDOUserException
                ("no profiler configured for this persistence manager");
        }
        m_prof.start();
    }

    public void stopProfiling() {
        if (m_prof == null) {
            throw new JDOUserException
                ("no profiler configured for this persistence manager");
        }
        m_prof.stop();
    }

    // Implementation of ClassInfo

    public List getAllFields(Class pcClass) {
        return m_classInfo.getAllFields(pcClass);
    }

    public List getAllTypes(Class pcClass) {
        return m_classInfo.getAllTypes(pcClass);
    }

    public String numberToName(Class pcClass, int fieldNumber) {
        return m_classInfo.numberToName(pcClass, fieldNumber);
    }

    public Class numberToType(Class pcClass, int fieldNumber) {
        return m_classInfo.numberToType(pcClass, fieldNumber);
    }

    public int nameToNumber(Class pcClass, String fieldName) {
        return m_classInfo.nameToNumber(pcClass, fieldName);
    }
}
