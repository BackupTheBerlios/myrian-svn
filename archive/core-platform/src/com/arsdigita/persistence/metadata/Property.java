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

package com.arsdigita.persistence.metadata;

import java.io.PrintStream;
import java.util.*;

/**
 * The Property class represents one property of a CompoundType. Each property
 * is a "field" in the CompoundType. Every Property has an associated
 * DataType. This allows CompoundTypes to be constructed from multiple
 * SimpleTypes and CompoundTypes. In addition to having an associated
 * DataType, each property has an associated multiplicity. There are currently
 * three possible values for the multiplicity of a Property, NULLABLE,
 * REQUIRED, and COLLECTION.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2002/08/07 $
 **/

public class Property extends Element {

    /**
     * These are the integer type codes for the different event types a
     * Property may have.
     **/

    public final static int RETRIEVE = 0;
    public final static int ADD = 1;
    public final static int REMOVE = 2;
    public final static int CLEAR = 3;
    public final static int NUM_EVENT_TYPES = 4;

    private final static String[] s_eventTypeText = {
        "retrieve",
        "add",
        "remove",
        "clear"
    };

    /**
     * These are the integer type codes for the multiplicity of a Property.
     **/

    /**
     * The NULLABLE multiplicity is for single valued properties that can be
     * set to null.
     **/
    public final static int NULLABLE = 0;

    /**
     * The REQUIRED multiplicity is for single valued properties that cannot
     * be set to null.
     **/
    public final static int REQUIRED = 1;

    /**
     * The COLLECTION multiplicity is for multi valued properties.
     **/
    public final static int COLLECTION = 2;

    /**
     * This is for the outputPDL method to use to display the multiplicity of the
     * Property.
     **/
    private final static String[] s_multiplicityText = {
        "",
        "[1..1]",
        "[0..n]"
    };

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/Property.java#5 $ by $Author: rhs $, $DateTime: 2002/08/07 15:23:06 $";

    /**
     * The container type of the property.
     **/

    private CompoundType m_container;

    /**
     * The name of the Property.
     **/
    private String m_name;

    /**
     * The DataType of the Property.
     **/
    private DataType m_type;

    /**
     * The multiplicity of the Property.
     **/
    private int m_multiplicity;

    /**
     * Indicates whether or not the Property is part of a composition relation
     * or an association. All SimpleTypes are components.
     **/
    private boolean m_isComponent;

    /**
     * The Column used to store this Property. This may be null if the
     * Property isn't actually stored in a Column.
     **/
    private Column m_column = null;
 
    /**
     * The JoinPath used to retrieve this Property.  This may be null if the
     * Property is not a role reference.
     */
    private JoinPath m_joinPath = null;

    /**
     * If this Property plays a role in an Association then this field is
     * set to the association.
     **/
    private Association m_assn = null;

    /**
     * 
     **/
    private Event[] m_events = new Event[NUM_EVENT_TYPES];


    /**
     * Constructs a new Property with the given name and DataType. By default
     * the multiplicity is set to NULLABLE and isComponent defaults to true if
     * <i>type</i> is simple, false otherwise.
     *
     * @param name The name of the property.
     * @param type The DataType of the property.
     **/

    public Property(String name, DataType type) {
        this(name, type, NULLABLE);
    }

    /**
     * Constructs a new Property with the given name, DataType, and
     * multiplicity. IsComponent defaults to true if <i>type</i> is simple,
     * false otherwise.
     *
     * @param name The name of the property.
     * @param type The DataType of the property.
     * @param multiplicity The multiplicity of the property.
     **/

    public Property(String name, DataType type, int multiplicity) {
        // If it's a simple type it's a component, otherwise we default to
        // association.
        this(name, type, multiplicity, type.isSimple());
    }

    /**
     * Constructs a new Property with the given name, DataType, multiplicity,
     * and compositeness.
     *
     * @param name The name of the property.
     * @param type The DataType of the property.
     * @param multiplicity The multiplicity of the property.
     * @param isComponent Indiciates if the property is a component or not.
     *
     * @exception IllegalArgumentException If name is empty or null.
     * @exception IllegalArgumentException If type is null.
     * @exception IllegalArgumentException if multiplicity is not one of the
     *            integer type codes for multiplicity.
     **/

    public Property(String name, DataType type, int multiplicity,
                    boolean isComponent) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException(
                "The property name must be non null and non empty."
                );
        }
        m_name = name;

        if (type == null) {
            throw new IllegalArgumentException(
                "The property type must be non null."
                );
        }
        m_type = type;

        if (multiplicity < 0 || multiplicity >= s_multiplicityText.length) {
            throw new IllegalArgumentException(
                "The multiplicity must be one of the integer type codes " +
                "defined in the Property class."
                );
        }
        m_multiplicity = multiplicity;

        m_isComponent = isComponent;
    }

    void setContainer(CompoundType container) {
        m_container = container;
    }

    /**
     * Returns the container of this property.
     **/

    public CompoundType getContainer() {
        return m_container;
    }

    /**
     * Returns the name of this Property.
     *
     * @return The name of this Property.
     **/

    public String getName() {
        return m_name;
    }


    /**
     * Returns the type of this Property.
     *
     * @return The type of this Property.
     **/

    public DataType getType() {
        return m_type;
    }


    /**
     * Returns true if this Property is an attribute, i.e. its DataType is
     * simple.
     *
     * @return True if this Property is an attribute, false otherwise.
     **/

    public boolean isAttribute() {
        return m_type.isSimple();
    }


    /**
     * Returns true if this Property is a role, i.e. it's DataType is
     * compound.
     *
     * @return True if this Property is a role, false otherwise.
     **/

    public boolean isRole() {
        return m_type.isCompound();
    }


    /**
     * Returns the integer type code for the multiplicity of this property.
     *
     * @return An integer that is always one of the type codes defined in this
     *         class.
     **/

    public int getMultiplicity() {
        return m_multiplicity;
    }


    /**
     * Returns true if the multiplicity of this Property is COLLECTION.
     *
     * @return True if the property is a COLLECTION.
     **/

    public boolean isCollection() {
        return m_multiplicity == COLLECTION;
    }

    /**
     * Returns true if the multiplicity of this Property is NULLABLE.
     *
     * @return True if the multiplicity of this Property is NULLABLE.
     **/

    public boolean isNullable() {
        return m_multiplicity == NULLABLE;
    }


    /**
     * Returns true if the multiplicity of this Property is REQUIRED.
     *
     * @return True if the multiplicity of this Property is REQUIRED.
     **/

    public boolean isRequired() {
        return m_multiplicity == REQUIRED;
    }


    /**
     * Returns true if this property is a component.
     *
     * @return true if this property is a component.
     **/

    public boolean isComponent() {
        return m_isComponent;
    }


    /**
     * Sets the Column used to store this Property.
     *
     * @param column The column.
     **/

    public void setColumn(Column column) {
        m_column = column;
    }


    /**
     * Returns the Column used to store this Property.
     *
     * @return the Column used to store this Property.
     */
    public Column getColumn() {
        return m_column;
    }


    /**
     * Sets the JoinPath used to retrieve this Property.
     * 
     * @param joinPath the JoinPath
     */
    public void setJoinPath(JoinPath joinPath) {
        m_joinPath = joinPath;
    }


    /**
     * Returns the JoinPath used to retrieve this Property.
     * 
     * @return the JoinPath used to retrieve this Property.
     */
    public JoinPath getJoinPath() {
        return m_joinPath;
    }

 
    /**
     * This method is used when a Property is made part of an association.
     *
     * @param assn The association that this Property is part of.
     **/

    void setAssociation(Association assn) {
        m_assn = assn;
    }


    /**
     * Returns the associon that this property plays a role in, or null if
     * this property doesn't play a role in an association.
     *
     * @return An association in which this property is a role, or null.
     **/

    public Association getAssociation() {
        return m_assn;
    }


    /**
     * Returns the type of the link object used by this association, or null
     * if there is none.
     *
     * @return The type of the link object used by this association, or null.
     **/

    public CompoundType getLinkType() {
        if (m_assn == null) {
            return null;
        } else {
            return m_assn.getLinkType();
        }
    }


    /**
     * Returns the associated property or null if there is no associated
     * property.
     **/

    public Property getAssociatedProperty() {
        if (m_assn == null) {
            return null;
        } else {
            return m_assn.getAssociatedProperty(this);
        }
    }


    /**
     * Returns true if this property refers to the composite of the object
     * type containing this property.
     **/

    public boolean isComposite() {
        Property other = getAssociatedProperty();
        if (other == null) {
            return false;
        } else {
            return other.isComponent();
        }
    }


    /**
     * Sets the Event of the given type.
     *
     * @param type The integer type code for the event type.
     * @param event The event.
     **/

    public void setEvent(int type, Event event) {
        m_events[type] = event;
    }

    /**
     * Gets the Event of the given type.
     *
     * @param type The integer type code for the event type.
     *
     * @return The specified event.
     **/

    public Event getEvent(int type) {
        return m_events[type];
    }

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

    /**
     * Returns the java class for the object that will be returned when
     * DataObject.get() is called on this Property.
     *
     * @return A java Class object.
     **/

    public Class getJavaClass() {
        if (isRole()) {
            if (isCollection()) {
                return com.arsdigita.persistence.DataAssociation.class;
            } else {
                return com.arsdigita.persistence.DataObject.class;
            }
        } else {
            return ((SimpleType) m_type).getJavaClass();
        }
    }

    /**
     * Outputs a serialized representation of this Properties events.
     *
     * @param out The PrintStream to use for output.
     **/

    void outputPDLEvents(PrintStream out) {
        for (int i = 0; i < NUM_EVENT_TYPES; i++) {
            Event event = getEvent(i);
            if (event == null) {
                continue;
            }

            out.println();
            out.print("    " + s_eventTypeText[i] + " " + getName() + " ");
            event.outputPDL(out);
            out.println();
        }
    }


    /**
     * Outputs a serialized representation of this Property.
     *
     * The following format is used:
     *
     * <pre>
     *     ["component"] &lt;name&gt; [ "[1..1]" | "[0..n]" ] &lt;type&gt; [ "=" &lt;column&gt; ]
     * </pre>
     *
     * @param out The PrintStream to use for output.
     **/

    void outputPDL(PrintStream out) {
        String start = "";

        if (isComponent() && isRole()) {
            start = start + "component ";
        }

        start = start + m_type.getQualifiedName() +
            s_multiplicityText[m_multiplicity];
        out.print(start);

        int padd = 22 - start.length();
        if (padd < 1) {
            padd = 1;
        }
        for (int i = 0; i < padd; i++) {
            out.print(" ");
        }

        out.print(m_name);

        if (m_column != null) {
            padd = 32 - start.length() - padd - m_name.length();
            if (padd < 1) {
                padd = 1;
            }
            for (int i = 0; i < padd; i++) {
                out.print(" ");
            }
            out.print("= ");
            m_column.outputPDL(out);
        } else if (m_joinPath != null) {
            padd = 32 - start.length() - padd - m_name.length();
            if (padd < 1) {
                padd = 1;
            }
            for (int i = 0; i < padd; i++) {
                out.print(" ");
            }
            out.print("= ");
            m_joinPath.outputPDL(out);
        }
    }

    public boolean isKeyProperty() {
        if (m_container instanceof ObjectType) {
            ObjectType ot = (ObjectType) m_container;
            return ot.isKeyProperty(m_name);
        } else {
            return false;
        }
    }

    void setNullability() {
        if (m_column != null) {
            if (isKeyProperty()) {
                m_column.setNullable(false);
            } else {
                m_column.setNullable(isNullable());
            }
        }

        if (m_joinPath != null) {
            List path = m_joinPath.getPath();
            switch (path.size()) {
            case 1:
                JoinElement je = m_joinPath.getJoinElement(0);
                if (isCollection()) {
                    je.getTo().setNullable(true);
                } else {
                    je.getFrom().setNullable(isNullable());
                }
                break;
            case 2:
                JoinElement one = m_joinPath.getJoinElement(0);
                JoinElement two = m_joinPath.getJoinElement(1);
                one.getTo().setNullable(false);
                two.getFrom().setNullable(false);
                break;
            default:
                m_joinPath.error("Don't know how to deal with length " +
                                 path.size() + " join paths.");
                break;
            }
        }
    }

    void generateForeignKeys() {
        if (m_joinPath != null) {
            boolean cascade = false;
            switch (m_joinPath.getPath().size()) {
            case 1:
                if (m_joinPath.getJoinElement(0).getFrom().isUniqueKey()) {
                    cascade = true;
                } else {
                    cascade = false;
                }
                break;
            case 2:
                cascade = true;
                break;
            default:
                m_joinPath.error("Don't know how to deal with length " +
                                 m_joinPath.getPath().size() + " join paths.");
                break;
            }

            m_joinPath.generateForeignKeys(cascade);
        }
    }

}
