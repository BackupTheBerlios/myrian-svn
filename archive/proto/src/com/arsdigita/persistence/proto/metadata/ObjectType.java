package com.arsdigita.persistence.proto.metadata;

import java.util.*;

/**
 * ObjectType
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class ObjectType {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/ObjectType.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    private Root m_root = null;
    private Model m_model;
    private String m_name;
    private ObjectType m_super;
    private ArrayList m_properties = new ArrayList();
    private HashMap m_propertyMap = new HashMap();

    public ObjectType(Model model, String name, ObjectType supertype) {
        m_model = model;
        m_name = name;
        m_super = supertype;
    }

    void setRoot(Root root) {
        m_root = root;
    }

    public Root getRoot() {
        return m_root;
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
        return m_propertyMap.containsKey(name);
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
        if (prop == null) {
            throw new IllegalArgumentException
                ("Cannot add a null property.");
        }
        if (hasProperty(prop.getName())) {
            throw new IllegalStateException
                ("Already have a property named " + prop.getName());
        }
        if (prop.getContainer() != null) {
            throw new IllegalArgumentException
                ("Property already belongs to an object type: " + prop);
        }

        m_properties.add(prop);
        m_propertyMap.put(prop.getName(), prop);
        prop.setContainer(this);
    }

    public Collection getDeclaredProperties() {
        return m_properties;
    }

    public Property getDeclaredProperty(String name) {
        return (Property) m_propertyMap.get(name);
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

}
