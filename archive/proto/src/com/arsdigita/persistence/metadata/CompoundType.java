/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence.metadata;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

/**
 * The CompoundType class represents types that are built up from SimpleTypes
 * and other CompoundTypes. A CompoundType has a set of properties. Each
 * property contained in a CompoundType has an associated DataType.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/09 $
 */

abstract public class CompoundType extends DataType {

    public static final int RETRIEVE = 0;
    public static final int INSERT = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    public static final int RETRIEVE_ALL = 4;
    public static final int RETRIEVE_ATTRIBUTES = 5;
    public final static int NUM_EVENT_TYPES = 6;

    private static boolean s_firstRowsDefault = true;

    protected static final String[] s_eventTypeText = {
        "retrieve",
        "insert",
        "update",
        "delete",
        "retrieve all",
        "retrieve attributes"
    };

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/metadata/CompoundType.java#2 $ by $Author: rhs $, $DateTime: 2002/12/09 12:29:30 $";

    /**
     * This Map contains all the properties this CompoundType has. It is keyed
     * by the property name.
     **/
    private Map m_properties = new HashMap();

    /**
     * The Events for performing operations on this CompoundType.
     **/
    Event[] m_events = new Event[NUM_EVENT_TYPES];

    /**
     * Constructs a new and empty CompoundType with the given name. In order
     * to do anything useful with the type you must add any properties it may
     * have.
     *
     * @param name The name of this compound type.
     *
     * @see CompoundType#addProperty(Property property)
     **/

    public CompoundType(String name) {
        super(name);

        initOption("FIRST_ROWS",
                   s_firstRowsDefault ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Set the default value of the FIRST_ROWS option for new CompoundType
     * objects.
     */
    public static void setFirstRowsDefault(boolean firstRows) {
        s_firstRowsDefault = firstRows;
    }

    /**
     * Adds a property to this CompoundType. The property may be fetched using
     * getProperty and passing in the property name.
     *
     * @param property The property to add.
     **/

    public void addProperty(Property property) {
        property.setContainer(this);
        m_properties.put(property.getName(), property);
    }


    /**
     *  This removes the property from this CompoundType.  This modifies
     *  the basic values of the CompoundType so it should be used with
     *  caution.  Calling this for statically defined types will cause errors.
     *
     *  @param property This is the property to be removed
     */
    public void removeProperty(Property property) {
        m_properties.remove(property.getName());
    }


    /**
     * Gets a property that this CompoundType contains. Returns null if no
     * such property exists.
     *
     * @param name The name of the property.
     *
     * @return The property with name <i>name</i>, or null if no such property
     *         exists.
     **/

    public Property getProperty(String name) {
        Object result = m_properties.get(name);
        if (result == null) {
            result = caseInsensativeGet(m_properties, name);
        }
        return (Property) result;
    }


    /**
     * Returns true if and only if this CompoundType has a property with the
     * given name.
     *
     * @param name The name of the property for which to check existence.
     *
     * @return True if this CompoundType has a property with the given name.
     *         False otherwise.
     **/

    public boolean hasProperty(String name) {
        return m_properties.containsKey(name);
    }


    /**
     * Returns an iterator containing all the Properties this CompoundType
     * contains.
     *
     * @return An iterator containing all the Properties this CompoundType
     *         contains.
     *
     * @see Property
     **/

    public Iterator getProperties() {
        return m_properties.values().iterator();
    }


    /**
     * This method will always return true. It is the implementation of the
     * abstract method that appears in DataType.
     *
     * @return true
     **/

    public boolean isCompound() {
        return true;
    }


    /**
     * Sets this CompoundTypes event of the type identified by <i>type</i> to
     * <i>event</i>.
     *
     * @param type The integer type code identifying the even type.
     * @param event The event.
     *
     * @pre type >= RETRIEVE && type < NUM_EVENT_TYPES
     **/
    public void setEvent(int type, Event event) {
        m_events[type] = event;
    }


    /**
     * Returns the Event identified by the given integer type code.
     *
     * @param type The integer type code identifying which Event to return.
     *
     * @pre type >= RETRIEVE && type < NUM_EVENT_TYPES
     **/
    public Event getEvent(int type) {
        return m_events[type];
    }

    /**
     * Returns the Event name identified by the given integer type code.
     *
     * @param type The integer type code identifying which Event to return.
     *
     * @pre type >= RETRIEVE && type < NUM_EVENT_TYPES
     **/

    public static final String getEventName(int type) {
        return s_eventTypeText[type];
    }

    public static final int getEventCode(String name) {
        for (int i = 0; i < s_eventTypeText.length; i++) {
            if (s_eventTypeText[i].equals(name)) {
                return i;
            }
        }

        throw new IllegalArgumentException(name + ": not a valid event");
    }

}
