/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
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
