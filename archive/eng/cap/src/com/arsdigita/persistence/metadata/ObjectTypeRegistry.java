/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.arsdigita.persistence.metadata;

import com.arsdigita.util.Assert;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Provides a general purpose object type adapter
 * registry, which is aware of the type hierarchy
 * 
 * @author Daniel Berrange
 * @see com.arsdigita.search.MetadataProviderRegistry
 **/
public class ObjectTypeRegistry {
    
    private static final Logger s_log = Logger.getLogger(ObjectTypeRegistry.class);

    private Map m_adapters = new HashMap();
    
    /**
     * Registers an adapter for a type
     * @param type the object type name
     * @param adapter the object type adapter
     **/
    public void registerAdapter(String type,
                                Object adapter) {
        registerAdapter(MetadataRoot.getMetadataRoot().getObjectType(type),
                        adapter);
    }
    
    /**
     * Unregisters an adapter for a type
     * @param type the object type name
     * @param adapter the object type adapter
     **/
    public void unregisterAdapter(String type) {
        unregisterAdapter(MetadataRoot.getMetadataRoot().getObjectType(type));
    }

    /**
     * Registers an adapter for a type
     * @param type the object type
     * @param adapter the object type adapter
     **/
    public void registerAdapter(ObjectType type,
                                Object adapter) {
        Assert.exists(type, ObjectType.class);
        Assert.exists(type, Object.class);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Registering adapter " + adapter.getClass().getName() + 
                        " for " + type.getQualifiedName());
        }
        m_adapters.put(type, adapter);
    }
    
    /**
     * Unregisters an adapter for a type
     * @param type the object type
     * @param adapter the object type adapter
     **/
    public void unregisterAdapter(ObjectType type) {
        Assert.exists(type, ObjectType.class);
        Assert.exists(type, Object.class);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Unregistering adapter for " + 
                        type.getQualifiedName());
        }
        m_adapters.remove(type);
    }
    
    /**
     * Retrieves the adapter registered against this
     * type, or null if none is registered.
     * @param type the object type name
     * @return the adapter, or null
     */
    public Object getAdapter(String type) {
        return getAdapter(MetadataRoot.getMetadataRoot().getObjectType(type));
    }

    /**
     * Retrieves the adapter registered against this
     * type, or null if none is registered.
     * @param type the object type
     * @return the adapter, or null
     */
    public Object getAdapter(ObjectType type) {
        Assert.exists(type, ObjectType.class);
        
        Object adapter = m_adapters.get(type);
        if (s_log.isDebugEnabled()) {
            s_log.debug("Returning adapter " + (adapter == null ? "<none>" :
                                                adapter.getClass().getName()) +
                        " for " + type.getQualifiedName());
        }
        return adapter;
    }
    
    /**
     * Retrieves the best matching adapter for a
     * object type. Returns the exact match if present,
     * otherwise recurses up the object type hierarchy
     * @param type the object type name
     * @return the best adapter, or null
     */
    public Object findAdapter(String type) {
        return findAdapter(MetadataRoot.getMetadataRoot().getObjectType(type));
    }

    /**
     * Retrieves the best matching adapter for a
     * object type. Returns the exact match if present,
     * otherwise recurses up the object type hierarchy
     * @param type the object type
     * @return the best adapter, or null
     */
    public Object findAdapter(ObjectType type) {
        Assert.exists(type, ObjectType.class);
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("Finding adapter for " + type.getQualifiedName());
        }
        
        Object adapter = getAdapter(type);
        if (adapter == null) {
            ObjectType parent = type.getSupertype();
            if (parent != null) {
                adapter = findAdapter(parent);
            }
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Found adapter " + (adapter == null ? "<none>" :
                                            adapter.getClass().getName()) +
                        " for " + type.getQualifiedName());
        }
        return adapter;
    }

}
