package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;
import java.util.*;

/**
 * ObjectType
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/01/15 $
 **/

public class ObjectType extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/ObjectType.java#5 $ by $Author: rhs $, $DateTime: 2003/01/15 16:58:00 $";

    private Model m_model;
    private String m_name;
    private ObjectType m_super;
    private Mist m_properties = new Mist(this);

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
            ObjectType type = prop.getType();
            return type.getProperty(path.getName());
        }
    }

    public ObjectType getType(Path path) {
        return getProperty(path).getType();
    }

    public boolean isKey(Path path) {
        Property prop = getProperty(path);
        ObjectMap map = getRoot().getObjectMap(prop.getContainer());
        return map.getKeyProperties().contains(prop);
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

    public boolean isSubtypeOf(ObjectType type) {
        if (this.equals(type)) {
            return true;
        } else if (m_super != null) {
            return m_super.isSubtypeOf(type);
        } else {
            return false;
        }
    }

    Object getKey() {
        return toString();
    }

    public String toString() {
        return getQualifiedName();
    }

}
