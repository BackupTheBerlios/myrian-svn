/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.DataType;
import com.arsdigita.persistence.metadata.CompoundType;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;

import org.apache.log4j.Category;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Title:       DataContainer class
 * Description: This class is responsible for containing the data object
 *              state. This includes scalar attribute values and other data
 *              objects referenced through roles.
 * Copyright:    Copyright (c) 2001
 * Company:      ArsDigita
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */

public class DataContainer {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataContainer.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private static final Category s_log =
         Category.getInstance(DataContainer.class.getName());

    // The type of the data contained in this data container.
    private CompoundType m_type;

    // Contains the values as retrieved from the persistence mechanism.
    private Map m_back = new HashMap();
    // Contains the values that have been set in memory.
    private Map m_front = new HashMap();

    // Contains associations.
    private Map m_assns = new HashMap();

    /**
     * Constructs a new and empty data container.
     **/

    public DataContainer() {
    }

    public DataContainer(CompoundType type) {
        m_type = type;
    }

    public CompoundType getType() {
        return m_type;
    }

    void setType(CompoundType type) {
        m_type = type;
    }

    /**
     * Moves all front values to back values and syncs all the associations.
     **/

    void sync() {
        for (Iterator it = m_front.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
	    checkKey((String) me.getKey(), me.getValue());
            m_back.put(me.getKey(), me.getValue());
        }
        m_front.clear();
    }

    /**
     * Gets the values stored in the db.
     **/

    Map getBack() {
        return m_back;
    }

    /**
     * Gets the in memory values.
     **/

    Map getFront() {
        return m_front;
    }

    /**
     * Clears all values stored by this data container.
     **/

    public void clear() {
        m_back.clear();
        m_front.clear();
    }

    private void checkKey(String name, Object value) {
	if (m_type != null && m_type instanceof ObjectType) {
	    ObjectType type = (ObjectType) m_type;
	    if (type.isKeyProperty(name) && value == null) {
		throw new PersistenceException
		    ("Cannot set key property \"" + name + "\" to null.");
	    }
	}
    }

    /**
     * Initializes a property. If a property with the same name has
     * already been initialized, throws an exception.  If the value
     * is a DataAssociation then it is stored as an Association.
     * Otherwise, it is stored as an attribute.  If there is
     * any chance that the value may be null, you should use
     * {@link #initProperty(String name, Object value, PropertyType propertyType)}
     *
     * @param name The name of the property.
     * @param value The initial value of the property.
     **/
    public void initProperty(String name, Object value) {
	checkKey(name, value);

        if (value instanceof DataAssociationImpl) {
            m_assns.put(name, value);
        } else {
            // The following if screws up lazy loading by causing any locally
            // modified properties to be overwritten when we go to the db to
            // fetch the property that hasn't been loaded yet.
            //if (m_front.containsKey(name)) {
            //m_front.remove(name);
            //}
            m_back.put(name, value);
        }
    }

    public void clearProperty(String name) {
        m_back.remove(name);
        m_front.remove(name);
        m_assns.remove(name);
    }

    /**
     * Sets a property.
     *
     * @param propertyName The name of the property.
     * @param value The value of the property.
     **/

    public void set(String propertyName, Object value) {
	checkKey(propertyName, value);

        // if this changes so that we may put null values into m_assns
        // then we need to change isPropertyModified as well
        if (value instanceof DataAssociationImpl) {
            m_assns.put(propertyName, value);
        } else {
            m_front.put(propertyName, value);
        }
    }

    /**
     * Retrieves a property.
     *
     * @param propertyName The name of the property.
     *
     * @return The value of the property.
     **/

    public Object get(String propertyName) {
        if (m_assns.containsKey(propertyName)) {
            return m_assns.get(propertyName);
        } else {
            Object result;
            if (m_front.containsKey(propertyName)) {
                result = m_front.get(propertyName);
            } else {
                result = m_back.get(propertyName);
            }
            return result;
        }
    }

    /**
     * Queries for the existence of a property.
     *
     * @param name The name of the property.
     *
     * @return True if the data container has the specified property, false
     *         otherwise.
     **/

    public boolean hasProperty(String name) {
        return m_assns.containsKey(name) ||
            m_back.containsKey(name) ||
            m_front.containsKey(name);
    }

    /**
     * Returns a map of of the properties contained by this Data Container.
     * The map is keyed by the property name. Modifications of the map do not
     * effect the state of this Data Container.
     *
     * @return A map of the properties in this object.
     **/

    public Map getProperties() {
        Map result = new HashMap();
        result.putAll(m_back);
        result.putAll(m_front);
        result.putAll(m_assns);
        return result;
    }

    /**
     * Returns true if the DataContainer has been modified.
     *
     * @return True if modified, false otherwise.
     **/

    public boolean isModified() {
        if (m_front.size() > 0) {
            return true;
        }

        for (Iterator it = m_assns.values().iterator(); it.hasNext(); ) {
            DataAssociation da = (DataAssociation) it.next();
            if (da != null && da.isModified()) {
                return true;
            }
        }

        return false;
    }


    /**
     * @param propertyName The name of the property to check
     * @return true if the property name is the name of an association.  
     * false otherwise.
     */
    public boolean isAssociation(String propertyName) {
        return m_assns.containsKey(propertyName);
    }

 
    /**
     * Returns true if the property specified by <i>name</i> has been
     * modified.
     *
     * @param name The name of the property.
     *
     * @return True if the property has been modified, false otherwise.
     **/

    public boolean isPropertyModified(String name) {
        if ((m_type != null) && (!m_type.hasProperty(name))) {
            throw new PersistenceException(
                "Property " + name + " does not exist in " +
                m_type.getQualifiedName());
        }

        if (m_assns.containsKey(name)) {
            DataAssociationImpl da = (DataAssociationImpl)m_assns.get(name);
            if (da == null) {
                // we can return false since  there is currently no
                // way to "set" a DataAssociation to null
                return false;
            } else {
                return da.isModified();
            }
        } else {
            return m_front.containsKey(name);
        }
    }

    /**
     * Returns the associations this data container has.
     **/

    Iterator getDataAssociations() {
        return m_assns.values().iterator();
    }

    /**
     * Returns a human readable string representation of this data container.
     *
     * @return The string representation of the Data Container.
     **/

    public String toString() {
        return "<properties: " + getProperties() + 
                ", Front store data:" + m_front + 
                ", Back store data: " + m_back + 
                ", Associations: " + m_assns + ">";
    }


    static final String formatPath(String[] path) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < path.length - 1; i++) {
            result.append(path[i]);
            result.append('.');
        }
        result.append(path[path.length - 1]);
        return result.toString();
    }


    void initPath(String[] path, Object value) {
        DataContainer last = null;
        DataContainer dc = this;
        for (int i = 0; i < path.length - 1; i++) {
            Property prop = dc.getType().getProperty(path[i]);

            if (prop == null || !prop.isRole()) {
                throw new PersistenceException(
                    "Cannot assign to property in type " +
                    m_type.getQualifiedName() +
                    ": " + formatPath(path)
                    );
            }

            Object obj = dc.m_back.get(path[i]);

            if (obj == null) {
                if (dc.m_back.containsKey(path[i])) { return; }
                Session ssn = SessionManager.getSession();
                obj = GenericDataObjectFactory.createObject(
                    (ObjectType) prop.getType(), 
                    ssn,
                    false
                    );
                ((GenericDataObject)
                 obj).getDataContainer().setType((ObjectType) prop.getType());
                dc.initProperty(prop.getName(), obj);
            }

            last = dc;

            dc = ((GenericDataObject) obj).getDataContainer();
        }

        if (value == null &&
            last != null &&
            dc.m_type instanceof ObjectType &&
            ((ObjectType) dc.m_type).isKeyProperty(path[path.length - 1])) {
            last.initProperty(path[path.length - 2], null);
            return;
        }

        dc.initProperty(path[path.length - 1], value);
    }


    Object lookupValue(String[] path) {
        DataContainer dc = this;
        for (int i = 0; i < path.length - 1; i++) {
            Object obj = dc.get(path[i]);
            if (obj == null) {
                return null;
            }

            if (obj instanceof GenericDataObject) {
                dc = ((GenericDataObject) obj).getDataContainer();
            } else if (obj instanceof DataAssociationImpl) {
                dc = ((DataAssociationImpl) obj).getDataContainer();
            } else {
                throw new PersistenceException(
                    "Cannot lookup property in type " +
                    m_type.getQualifiedName() +
                    ": " + formatPath(path)
                    );
            }
        }

        return dc.get(path[path.length - 1]);
    }


    Property lookupProperty(String[] path) {
        DataType type = m_type;
        Property prop = null;
        for (int i = 0; i < path.length; i++) {
            if (type.isCompound()) {
                prop = ((CompoundType) type).getProperty(path[i]);
            } else {
                prop = null;
            }

            if (prop == null) {
                throw new PersistenceException(
                    "No such property in type " +
                    m_type.getQualifiedName() +
                    ": " + formatPath(path)
                    );
            }

            type = prop.getType();
        }

        return prop;
    }

    /**
     * Returns a deep copy of this data container. Note that this does not
     * copy any locally modified values.
     **/

    public DataContainer copy() {
        DataContainer result = new DataContainer(m_type);

        for (Iterator it = m_back.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            String key = (String) me.getKey();
            Object value = me.getValue();

            if (value instanceof GenericDataObject) {
                result.initProperty(key, ((GenericDataObject) value).copy());
            } else {
                result.initProperty(key, value);
            }
        }

        return result;
    }

    static DataContainer join(DataContainer front, DataContainer back) {
        CompoundType type = new ObjectType(
            front.m_type.getQualifiedName() + " and " +
            back.m_type.getQualifiedName()
            );
        type.setFilename(back.m_type.getFilename());
        type.setLineInfo(back.m_type.getLineNumber(),
                         back.m_type.getColumnNumber());

        CompoundType[] types = {front.m_type, back.m_type};

        for (int i = 0; i < types.length; i++) {
            for (Iterator it = types[i].getProperties(); it.hasNext(); ) {
                Property prop = (Property) it.next();
                if (!type.hasProperty(prop.getName())) {
                    type.addProperty(prop);
                }
            }
        }

        DataContainer result = new DataContainer(type);

        result.m_back.putAll(back.m_back);
        result.m_back.putAll(front.m_back);

        result.m_front.putAll(back.m_front);
        result.m_front.putAll(front.m_front);

        return result;
    }

}
