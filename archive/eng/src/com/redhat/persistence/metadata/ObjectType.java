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
package com.redhat.persistence.metadata;

import com.redhat.persistence.common.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * ObjectType
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/10/01 $
 **/

public class ObjectType extends Element {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/ObjectType.java#7 $ by $Author: rhs $, $DateTime: 2004/10/01 15:07:31 $";

    private final Model m_model;
    private final String m_name;
    private final List m_parameters;
    private final String m_qualifiedName;
    private Class m_class;
    private final TypeReference m_super;
    private final Mist m_properties = new Mist(this);
    private final ArrayList m_immediates = new ArrayList();
    private final ArrayList m_roles = new ArrayList();
    private List m_allProps = null;
    private List m_allRoles = null;
    private List m_allImmediates = null;

    public ObjectType(Model model, String name, ObjectType supertype) {
        this(model, name, null, supertype);
    }

    public ObjectType(Model model, String name, List parameters,
                      ObjectType supertype) {
        this(model, name, parameters, supertype == null ?
             null : TypeReference.get(supertype));
    }

    public ObjectType(Model model, String name, List parameters,
                      TypeReference supertype) {
        m_model = model;
        m_name = name;
        m_parameters = parameters;
        m_super = supertype;

        if (m_model == null) {
            m_qualifiedName = m_name;
        } else {
            m_qualifiedName = m_model.getQualifiedName() + "." + m_name;
        }
    }

    public Root getRoot() {
        return (Root) getParent();
    }

    public Model getModel() {
        return m_model;
    }

    public String getName() {
        return m_name;
    }

    public List getParameters() {
        return m_parameters;
    }

    public void setJavaClass(Class klass) {
	m_class = klass;
    }

    public Class getJavaClass() {
        if (m_class == null && m_super != null) {
            return getSupertype().getJavaClass();
        } else {
            return m_class;
        }
    }

    public String getQualifiedName() {
        return m_qualifiedName;
    }

    public TypeReference getSupertypeReference() {
        return m_super;
    }

    public ObjectType getSupertype() {
        if (m_super == null) {
            return null;
        } else {
            return m_super.get();
        }
    }

    public boolean hasDeclaredProperty(String name) {
        return m_properties.containsKey(name);
    }

    public boolean hasProperty(String name) {
        if (hasDeclaredProperty(name)) {
            return true;
        } else if (m_super != null) {
            return getSupertype().hasProperty(name);
        } else {
            return false;
        }
    }

    public boolean isKeyProperty(String name) {
	return isKeyProperty(getProperty(name));
    }

    public boolean isKeyProperty(Property prop) {
	return getKeyProperties().contains(prop);
    }

    public void addProperty(Property prop) {
        m_properties.add(prop);
        if (prop instanceof Role) {
            m_roles.add(prop);
        }
    }

    public List getDeclaredProperties() {
        return m_properties;
    }

    public Property getDeclaredProperty(String name) {
        return (Property) m_properties.get(name);
    }

    public List getProperties() {
        if (m_allProps == null) {
            if (m_super == null) {
                m_allProps = Collections.unmodifiableList(m_properties);
            } else {
                m_allProps = Collections.unmodifiableList
                    (new UnionList
                     (m_properties, getSupertype().getProperties()));
            }
        }
        return m_allProps;
    }

    public Property getProperty(String name) {
        if (hasDeclaredProperty(name)) {
            return getDeclaredProperty(name);
        } else if (m_super != null) {
            return getSupertype().getProperty(name);
        } else {
            return null;
        }
    }

    public Property getProperty(Path path) {
        if (path.getParent() == null) {
            return getProperty(path.getName());
        } else {
            Property prop = getProperty(path.getParent());
            if (prop == null) {
                return null;
            } else {
                ObjectType type = prop.getType();
                return type.getProperty(path.getName());
            }
        }
    }

    public ObjectType getType(Path path) {
        if (path == null) {
            return this;
        } else {
            Property prop = getProperty(path);
            if (prop == null) {
                return null;
            } else {
                return prop.getType();
            }
        }
    }

    public boolean exists(Path path) {
        return getProperty(path) != null;
    }

    public boolean isKey(Path path) {
        Property prop = getProperty(path);
	if (getRoot() == null) { return false; }
        ObjectMap map = getRoot().getObjectMap(prop.getContainer());
        return map.getKeyProperties().contains(prop);
    }

    public boolean isImmediate(Property prop) {
        return getImmediateProperties().contains(prop);
    }

    public boolean isImmediate(Path path) {
        Property prop = getProperty(path);
        return prop.getContainer().isImmediate(prop);
    }

    public List getKeyProperties() {
	if (getRoot() == null) { return Collections.EMPTY_LIST; }
        ObjectMap map = getRoot().getObjectMap(this);
        if (map == null) { return Collections.EMPTY_LIST; }
        return map.getKeyProperties();
    }

    public List getImmediateProperties() {
        if (m_allImmediates == null) {
            List keys = getKeyProperties();
            if (keys.size() > 0) {
                m_allImmediates = m_super == null
                    ? Collections.unmodifiableList(new UnionList
                                                   (keys, m_immediates))
                    : getSupertype().getImmediateProperties();
            }
        }

        // for nonkeyed types, return all properties
        return (m_allImmediates == null) ? getProperties() : m_allImmediates;
    }

    public void addImmediateProperty(Property prop) {
        if (prop.getContainer() != this) {
            throw new IllegalArgumentException
                ("property doesn't belong to this type: " + prop);
        }
        if (m_super != null) {
            throw new IllegalArgumentException
                ("derived object types cannot have immediate properties");
        }

        if (!m_immediates.contains(prop)) {
            m_immediates.add(prop);
        }
    }

    public boolean isKeyed() {
	if (getRoot() == null) { return false; }
        ObjectMap map = getRoot().getObjectMap(this);
        if (map == null) { return false; }
        if (map.getKeyProperties().size() == 0) { return false;}
        return true;
    }

    public boolean hasKey() {
	return isKeyed();
    }

    public boolean isCompound() {
        return getProperties().size() != 0;
    }

    public boolean isPrimitive() {
        return !isCompound();
    }

    public List getRoles() {
        if (m_allRoles == null) {
            if (m_super == null) {
                m_allRoles = Collections.unmodifiableList(m_roles);
            } else {
                m_allRoles = Collections.unmodifiableList
                    (new UnionList(m_roles, getSupertype().getRoles()));
            }
        }
        return m_allRoles;
    }

    public ObjectType getBasetype() {
        if (m_super == null) {
            return this;
        } else {
            return getSupertype().getBasetype();
        }
    }

    public boolean isSubtypeOf(String name) {
	return isSubtypeOf(getRoot().getObjectType(name));
    }

    public boolean isSubtypeOf(ObjectType type) {
        if (this.equals(type)) {
            return true;
        } else if (m_super != null) {
            return getSupertype().isSubtypeOf(type);
        } else {
            return false;
        }
    }

    Object getElementKey() {
        return getQualifiedName();
    }

    public String toString() {
        return getQualifiedName();
    }

}
