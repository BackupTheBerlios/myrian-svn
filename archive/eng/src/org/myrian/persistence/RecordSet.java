/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
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
package org.myrian.persistence;

import org.myrian.persistence.common.CompoundKey;
import org.myrian.persistence.common.Path;
import org.myrian.persistence.metadata.Adapter;
import org.myrian.persistence.metadata.Mapping;
import org.myrian.persistence.metadata.ObjectMap;
import org.myrian.persistence.metadata.ObjectType;
import org.myrian.persistence.metadata.Property;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * RecordSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

public abstract class RecordSet {


    private static final Logger LOG = Logger.getLogger(RecordSet.class);

    private Signature m_signature;

    protected RecordSet(Signature signature) {
        m_signature = signature;
    }

    protected Signature getSignature() {
        return m_signature;
    }

    boolean isFetched(Path path) {
        return m_signature.isFetched(path);
    }

    public abstract ObjectMap getObjectMap();

    public abstract boolean next();

    public abstract Object get(Path p);

    public abstract void close();

    private ObjectMap getObjectMap(Path path) {
        ObjectMap result;
        if (path == null) {
            result = getObjectMap();
        } else {
            result = getObjectMap().getMapping(path).getMap();
        }
        if (result == null) {
            throw new IllegalStateException
                ("no map for path: " + path);
        }
        return result;
    }

    private Object key(Path path) {
        ObjectMap map = getObjectMap(path);
        ObjectType type = map.getObjectType();

        Object key;

        if (map.isPrimitive()) {
            return get(path);
        } else if (map.isNested()) {
            key = path == null ?
                key(map.getContainer().getPath()) : key(path.getParent());
            if (key == null) {
                return null;
            } else {
                key = new CompoundKey
                    (key, map.getContaining().getPath().getName());
            }
        } else {
            key = type.getBasetype();
        }

        List props = map.getKeyProperties();
        for (int i = 0; i < props.size(); i++) {
            Property p = (Property) props.get(i);
            Object subKey = key(Path.add(path, p.getName()));
            if (subKey == null) { return null; }
            key = new CompoundKey(key, subKey);
        }

        return key;
    }

    private Object get(Session ssn, Path path) {
        ObjectMap map = getObjectMap(path);
        ObjectType type = map.getObjectType();
        Collection props = type.getImmediateProperties();
        if (map.isPrimitive()) {
            return get(path);
        } else if (!map.isNested() && !type.isKeyed()
                   && m_signature.isSource(path)) {
            PropertyMap pmap = new PropertyMap(type);
            for (Iterator it = props.iterator(); it.hasNext(); ) {
                Property p = (Property) it.next();
                pmap.put(p, get(ssn, Path.add(path, p.getName())));
            }
            return pmap;
        } else {
            return reify(ssn, path);
        }
    }

    private Object reify(Session ssn, Path path) {
        Object key = key(path);
        if (key == null) { return null; }
        ObjectData odata = ssn.getObjectDataByKey(key);
        if (odata == null) {
            ObjectMap om = getObjectMap(path);
            ObjectType type = om.getObjectType();
            PropertyMap pmap = new PropertyMap(type);
            Collection props = om.getKeyProperties();
            for (Iterator it = props.iterator(); it.hasNext(); ) {
                Property p = (Property) it.next();
                pmap.put(p, get(ssn, Path.add(path, p.getName())));
            }
            Adapter ad = ssn.getRoot().getAdapter(type);
            if (LOG.isDebugEnabled()) {
                LOG.debug("resurecting: " + pmap);
            }
            Object obj = ad.getObject(type, pmap, ssn);
            odata = new ObjectData(ssn, obj, ObjectData.NUBILE);
            ssn.setSessionKey(obj, key);
            odata.setObjectMap(getObjectMap(path));
        } else if (!odata.isLoaded()) {
            odata.setState(ObjectData.NUBILE);
        }
        return odata.getObject();
    }

    private Object load(Session ssn, Map values, Path path, Map loaded) {
        if (loaded.containsKey(path)) { return loaded.get(path); }
        Object value = get(ssn, path);
        loaded.put(path, value);
        if (LOG.isDebugEnabled()) {
            LOG.debug("loading " + path + " = " + ssn.str(value));
        }
        ObjectMap map = getObjectMap(path);
        if (m_signature.isSource(path)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("loading " + path + " as cursor value (source)");
            }
            if (map.isNested() && map.isCompound()) {
                Object container =
                    load(ssn, values, map.getContainer().getPath(), loaded);
                Property prop = map.getContaining().getProperty();
                if (!prop.isCollection()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug
                            ("loading " + path + " into session (source)");
                    }
                    ssn.load(container, prop, value);
                }
                ObjectData odata = ssn.getObjectData(value);
                if (odata != null) {
                    odata.setContainer(container);
                }
            }
            values.put(path, value);
        } else {
            Path parent = path.getParent();
            Object container = load(ssn, values, parent, loaded);
            Mapping mapping = getObjectMap().getMapping(path);
            Property prop = mapping.getProperty();
            if (prop != null && (prop.isCollection()
                                 || (!mapping.getObjectMap().isNested()
                                     && !prop.getContainer().isKeyed()
                                     && m_signature.isSource(parent)))) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("loading " + path + " as cursor value");
                }
                values.put(path, value);
            } else if (prop != null) {
                if (container != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("loading " + path + " into session");
                    }
                    ssn.load(container, prop, value);
                    if (map.isNested() && map.isCompound()) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("setting container of " + path + " to " +
                                      ssn.str(container));
                        }
                        ObjectData odata = ssn.getObjectData(value);
                        odata.setContainer(container);
                    }
                } else if (value != null) {
                    throw new IllegalStateException
                        ("value at " + path + " has a null container: " +
                         ssn.str(value));
                }
            }
        }
        return value;
    }

    Map load(Session ssn) {
        Collection paths = m_signature.getPaths();

        Map loaded = new HashMap();
        Map values = new HashMap();

        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            Path path = (Path) it.next();
            load(ssn, values, path, loaded);
        }

        return values;
    }

}
