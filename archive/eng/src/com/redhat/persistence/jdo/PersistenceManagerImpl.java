package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.oql.Expression;
import java.util.*;
import javax.jdo.*;
import javax.jdo.spi.PersistenceCapable;

import org.apache.log4j.Logger;

public class PersistenceManagerImpl implements PersistenceManager {
    private static final Logger s_log =
        Logger.getLogger(PersistenceManagerImpl.class);

    static final String ATTR_NAME = PersistenceManagerImpl.class.getName();

    private Session m_ssn;
    private Transaction m_txn = new TransactionImpl(this);
    private Object m_userObject = null;

    public PersistenceManagerImpl(Session ssn) {
        m_ssn = ssn;
        m_ssn.setAttribute(ATTR_NAME, this);
    }

    // XXX: temporary hack to make PandoraTest compile.  Will revert to
    // package-scoped at first opportunity.
    public final Session getSession() {
        return m_ssn;
    }

    /**
     * Close this PersistenceManager so that no further requests may be made
     * on it.
     */
    public void close() {
        s_log.debug("close(): m_ssn = null");
        m_ssn = null;
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
    public void deletePersistent(Object pc) {
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
        Class cls = pc.getClass();
        Root root = m_ssn.getRoot();
        ObjectType type = root.getObjectType(cls.getName());
        return C.pmap(pc, type);
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
        throw new Error("not implemented");
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

    /**
     * Make the transient instance persistent in this PersistenceManager.
     */
    public void makePersistent(Object obj) {
        if (!(obj instanceof PersistenceCapable)) {
            throw new IllegalArgumentException(obj.getClass().getName());
        }
        PersistenceCapable pc = (PersistenceCapable) obj;

        Class cls = pc.getClass();
        Root root = m_ssn.getRoot();
        ObjectType type = root.getObjectType(cls.getName());
        if (type == null) {
            throw new IllegalStateException("no such type " + cls.getName());
        }
        PropertyMap pmap = C.pmap(pc, type);

        Object current = m_ssn.retrieve(pmap);
        if (current == null) {
            Map values = new HashMap();
            Map keys = new HashMap();
            for (Iterator it = type.getProperties().iterator();
                 it.hasNext(); ) {
                Property prop = (Property) it.next();
                Object value = C.javaGet(pc, prop);
                if (!prop.isKeyProperty()) {
                    values.put(prop, value);
                }
            }

            StateManagerImpl smi = new StateManagerImpl(this);
            pc.jdoReplaceStateManager(smi);

            smi.cacheKeyProperties(pc, pmap);

            m_ssn.create(pc);

            for (Iterator it = values.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry me = (Map.Entry) it.next();
                Property prop = (Property) me.getKey();
                Object value = me.getValue();

                if (value != null) {
                    if (prop.isCollection()) {
                        Collection c = (Collection) value;
                        for (Iterator vals = c.iterator(); vals.hasNext(); ) {
                            Object val = vals.next();
                            makePersistent(val);
                            m_ssn.add(obj, prop, val);
                        }
                    } else {
                        if (value instanceof PersistenceCapable) {
                            makePersistent(value);
                        }
                        m_ssn.set(obj, prop, value);
                    }
                }
            }
        }
    }

    /**
     * Make a Collection of instances persistent.
     */
    public void makePersistentAll(Collection pcs) {
        for (Iterator it = pcs.iterator(); it.hasNext(); ) {
            makePersistent(it.next());
        }
    }

    /**
     * Make an array of instances persistent.
     */
    public void makePersistentAll(Object[] pcs) {
        for (int i = 0; i < pcs.length; i++) {
            makePersistent(pcs[i]);
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
        // XXX: language parameter is ignored
        return new Query() {

            private boolean m_ignoreCache = false;

            public void closeAll() {
                throw new Error("not implemented");
            }
            public void close(Object result) {
                throw new Error("not implemented");
            }
            public PersistenceManager getPersistenceManager() {
                return PersistenceManagerImpl.this;
            }
            public Object executeWithMap(Map parameters) {
                // XXX: need to use parameters
                final Expression expr =
                    Expression.valueOf((String) query, parameters);
                final ObjectType type = expr.getType(m_ssn.getRoot());
                return new CRPCollection(m_ssn) {
                    ObjectType type() {
                        return type;
                    }
                    Expression expression() {
                        return expr;
                    }
                    public boolean add(Object o) {
                        throw new UnsupportedOperationException();
                    }
                    public boolean remove(Object o) {
                        throw new UnsupportedOperationException();
                    }
                    public void clear() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
            public Object executeWithArray(Object[] parameters) {
                Map m = new HashMap();
                for (int i = 0; i < parameters.length; i++) {
                    m.put("$" + (i+1), parameters[i]);
                }
                return executeWithMap(m);
            }
            public Object execute() {
                return executeWithArray(new Object[0]);
            }
            public Object execute(Object p1) {
                return executeWithArray(new Object[] {p1});
            }
            public Object execute(Object p1, Object p2) {
                return executeWithArray(new Object[] {p1, p2});
            }
            public Object execute(Object p1, Object p2, Object p3) {
                return executeWithArray(new Object[] {p1, p2, p3});
            }
            public void compile() {
                throw new Error("not implemented");
            }
            public boolean getIgnoreCache() {
                return m_ignoreCache;
            }
            public void setIgnoreCache(boolean value) {
                m_ignoreCache = value;
            }
            public void setOrdering(String ordering) {
                throw new Error("not implemented");
            }
            public void declareVariables(String variables) {
                throw new Error("not implemented");
            }
            public void declareParameters(String parameters) {
                throw new Error("not implemented");
            }
            public void declareImports(String imports) {
                throw new Error("not implemented");
            }
            public void setFilter(String filter) {
                throw new Error("not implemented");
            }
            public void setCandidates(Collection pcs) {
                throw new Error("not implemented");
            }
            public void setCandidates(Extent pcs) {
                throw new Error("not implemented");
            }
            public void setClass(Class pcs) {
                throw new Error("not implemented");
            }
        };
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
}
