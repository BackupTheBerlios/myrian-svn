/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
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
 * @version $Revision: #5 $ $Date: 2004/03/11 $
 **/

public class ObjectType extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/metadata/ObjectType.java#5 $ by $Author: vadim $, $DateTime: 2004/03/11 18:13:56 $";

    private final Model m_model;
    private final String m_name;
    private final String m_qualifiedName;
    private Class m_class;
    private final ObjectType m_super;
    private final Mist m_properties = new Mist(this);
    private final ArrayList m_immediates = new ArrayList();

    public ObjectType(Model model, String name, ObjectType supertype) {
        m_model = model;
        m_name = name;
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

    public void setJavaClass(Class klass) {
	m_class = klass;
    }

    public Class getJavaClass() {
	return m_class;
    }

    public String getQualifiedName() {
        return m_qualifiedName;
    }

    public ObjectType getSupertype() {
        return m_super;
    }

    public boolean hasDeclaredProperty(String name) {
        return m_properties.containsKey(name);
    }

    public boolean hasProperty(String name) {
        if (hasDeclaredProperty(name)) {
            return true;
        } else if (m_super != null) {
            return m_super.hasProperty(name);
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
    }

    public Collection getDeclaredProperties() {
        return m_properties;
    }

    public Property getDeclaredProperty(String name) {
        return (Property) m_properties.get(name);
    }

    private void getProperties(Collection result) {
        if (m_super != null) {
            m_super.getProperties(result);
        }
        result.addAll(m_properties);
    }

    public Collection getProperties() {
        ArrayList result = new ArrayList();
        getProperties(result);
        return result;
    }

    public Property getProperty(String name) {
        if (hasDeclaredProperty(name)) {
            return getDeclaredProperty(name);
        } else if (m_super != null) {
            return m_super.getProperty(name);
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

    public Collection getImmediateProperties() {
	if (isKeyed()) {
            ArrayList result = new ArrayList();
	    result.addAll(getKeyProperties());
            result.addAll(getBasetype().m_immediates);
            return result;
	} else {
	    return getProperties();
	}
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

    public Collection getRoles() {
        Collection result = getProperties();
        for (Iterator it = result.iterator(); it.hasNext(); ) {
            if (!(it.next() instanceof Role)) {
                it.remove();
            }
        }
        return result;
    }

    public ObjectType getBasetype() {
        if (m_super == null) {
            return this;
        } else {
            return m_super.getBasetype();
        }
    }

    public boolean isSubtypeOf(String name) {
	return isSubtypeOf(getRoot().getObjectType(name));
    }

    public boolean isSubtypeOf(ObjectType type) {
        if (this.equals(type)) {
            return true;
        } else if (m_super != null) {
            return m_super.isSubtypeOf(type);
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
