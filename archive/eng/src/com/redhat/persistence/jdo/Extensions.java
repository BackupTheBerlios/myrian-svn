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
package com.redhat.persistence.jdo;

import com.redhat.persistence.pdl.*;
import com.redhat.persistence.metadata.*;

import java.util.*;
import java.sql.*;
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

    /**
     * Loads schema for the given list of classes using the specified
     * connection. This will recursively load the schema for any
     * related classes.
     **/

    public static void load(List classes, Connection conn)
        throws SQLException {
        Root root = PersistenceManagerFactoryImpl.getMetadataRoot();
        Schema.load(getTables(root, classes), conn);
    }

    /**
     * Drops the schema for the given list of classes using the
     * specified connection. This will recursively drop the schema for
     * any related classes.
     **/

    public static void unload(List classes, Connection conn)
        throws SQLException {
        Root root = PersistenceManagerFactoryImpl.getMetadataRoot();
        Schema.unload(getTables(root, classes), conn);
    }

    private static List getTables(Root root, List classes) {
        Set tables = new LinkedHashSet();
        for (int i = 0; i <  classes.size(); i++) {
            Class klass = (Class) classes.get(i);
            ObjectType ot = root.getObjectType(klass.getName());
            if (ot == null) {
                throw new IllegalStateException
                    ("no object type for klass: " + klass);
            }
            ObjectMap om = root.getObjectMap(ot);
            if (om == null) {
                throw new IllegalStateException
                    ("no object map for type: " + ot.getQualifiedName());
            }
            tables.addAll(om.getRequiredTables());
        }
        return new ArrayList(tables);
    }

}
