package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;
import java.util.*;

/**
 * ObjectType
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class ObjectType extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/metadata/ObjectType.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    private Model m_model;
    private String m_name;
    private Class m_class;
    private ObjectType m_super;
    private Mist m_properties = new Mist(this);
    private ArrayList m_immediates = new ArrayList();

    public ObjectType(Model model, String name, ObjectType supertype) {
        m_model = model;
        m_name = name;
        m_super = supertype;
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
        return m_model.getQualifiedName() + "." + m_name;
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
