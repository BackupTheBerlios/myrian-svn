package com.redhat.persistence.jdo;

import java.util.Collection;
import java.util.Iterator;
import javax.jdo.*;

/**
 * Entry point for vendor extensions
 */
public class Extensions {

    public static final String OQL = "com.redhat.persistence.OQL";

    public static void addPath(Query q, String path) {
        if (q instanceof JDOQuery) {
            ((JDOQuery) q).addPath(path);
        }
    }

    /**
     * Create a new ExtendedQuery with no elements.
     */
    public static ExtendedQuery newQuery(PersistenceManager pm) {
        if (pm instanceof PersistenceManagerImpl) {
            return (ExtendedQuery) ((PersistenceManagerImpl) pm).newQuery();
        }

        throw new JDOUserException
            ("specified persistence manager is of class " + pm.getClass()
             + " instead of " + PersistenceManagerImpl.class);
    }

    /**
     * Create a new ExtendedQuery using elements from another Query.
     */
    public static ExtendedQuery newQuery(PersistenceManager pm,
                                         Object compiled) {
        if (pm instanceof PersistenceManagerImpl) {
            return (ExtendedQuery) ((PersistenceManagerImpl) pm).newQuery
                (compiled);
        }

        throw new JDOUserException
            ("specified persistence manager is of class " + pm.getClass()
             + " instead of " + PersistenceManagerImpl.class);
    }

    /**
     * Create a new ExtendedQuery using the specified language.
     */
    public static ExtendedQuery newQuery(PersistenceManager pm,
                                         String language, Object query) {
        if (pm instanceof PersistenceManagerImpl) {
            return (ExtendedQuery) ((PersistenceManagerImpl) pm).newQuery
                (language, query);
        }

        throw new JDOUserException
            ("specified persistence manager is of class " + pm.getClass()
             + " instead of " + PersistenceManagerImpl.class);
    }

    /**
     * Equivalent to calling Extensions#close(Iterator) for every iterator
     * returned by the specified collection.
     */
    public static void close(Collection c) {
        if (c instanceof Closeable) {
            ((Closeable) c).close();
        }
    }

    /**
     * Analogous to JDOQuery#close(Object), but applying only to the provided
     * Iterator. This method ca be used with Any Iterator obtained through the
     * JDO implementation including one that is the result of iterating over a
     * Collection-valued field of a persistent objects.
     */
    public static void close(Iterator it) {
        if (it instanceof Closeable) {
            ((Closeable) it).close();
        }
    }
}
