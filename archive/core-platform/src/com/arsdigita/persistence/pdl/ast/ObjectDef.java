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

package com.arsdigita.persistence.pdl.ast;

import com.arsdigita.persistence.metadata.MDSQLGeneratorFactory;
import com.arsdigita.persistence.metadata.CompoundType;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.Event;
import com.arsdigita.persistence.metadata.Operation;
import com.arsdigita.persistence.metadata.Mapping;
import com.arsdigita.persistence.metadata.Column;

import com.arsdigita.persistence.Utilities;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Defines an ObjectType according to what is read in from a PDL file.
 * Outputs a metadata ObjectType.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2002/08/06 $
 */

public class ObjectDef extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/ObjectDef.java#5 $ by $Author: rhs $, $DateTime: 2002/08/06 16:54:58 $";

    // object name
    private String m_name;

    // the object's parent
    private Identifier m_super;

    // the properties of the object
    private Map m_props = new HashMap();

    // the events associated with the object
    private List m_events = new ArrayList();

    // the flexfields available for dynamic extensions of the object
    private List m_flexFields = new ArrayList();

    // the various join paths defined for this object type
    private List m_joinPaths = new ArrayList();

    // any properties that need to be aggressively loaded by MDSQL
    private List m_aggressives = new ArrayList();

    // the reference key for this object type
    private ColumnDef m_refKey = null;

    // the object's primary key
    private ObjectKeyDef m_key;

    // the option block associated with this object type
    private OptionBlock m_options = null;

    // if the events have been generated yet
    private boolean m_eventsGenerated = false;

    // the objecttype
    private ObjectType m_type = null;

    /**
     * Creates a new ObjectDef with a given name and superclass.
     *
     * @param name the object name
     * @param superName the identifier of the object's parent
     * @pre name != null
     */
    public ObjectDef(String name, Identifier superName) {
        m_name = name;
        m_super = superName;
        if (m_super != null) {
            super.add(m_super);
        }
    }

    /**
     * Adds an option block to this object type.
     **/

    public void add(OptionBlock options) {
        m_options = options;
    }

    /**
     * Returns the object name
     *
     * @return the object name
     */
    public String getName() {
        return m_name;
    }


    /**
     * Returns the identifier refering to this ObjectDef's supertype.
     **/

    public Identifier getSuper() {
        return m_super;
    }

    /**
     * Returns the generated version of this objectdef
     *
     * @return the generated version of this objectdef
     */
    public ObjectType getObjectType() {
        return m_type;
    }

    /**
     * Returns true if the ObjectType has already been generated
     *
     * @return true if the ObjectType has already been generated
     */
    public boolean getObjectGenerated() {
        return getObjectType() != null;
    }

    /**
     * Returns a particular EventDef associated with this ObjectDef.
     * If name is null, the default EventDef of type "type" is returned.
     * Otherwise an EventDef matching the name is returned, or if it does not
     * exist, null.
     *
     * @return a particular EventDef associated with this ObjectDef.
     */
    public EventDef getEventDef(String type, String name) {
        for (int i = 0; i < m_events.size(); i++) {
            EventDef ed = (EventDef) m_events.get(i);
            if (ed.getType().equals(type)) {
                if ((ed.getName() == null && name == null) ||
                     (ed.getName() != null && ed.getName().equals(name))) {
                    return ed;
                }
            }
        }

        return null;
    }

    /**
     * Add a PropertyDef to this ObjectDef.
     *
     * @param ad the PropertyDef to add
     */
    public void add(PropertyDef ad) {
        if (m_props.get(ad.getName()) != null) {
            error(ad.getName() + " already defined in " + getName());
        }

        if (ad.isAttribute()) {
            boolean hasColumn = (ad.getColumn() != null);
            Iterator it = m_props.values().iterator();

            while (it.hasNext()) {
                PropertyDef prop = (PropertyDef)it.next();

                if (prop.isAttribute()) {
                    if (hasColumn != (prop.getColumn() != null)) {
                        error("Not all properties are defined with columns " +
                              "in object " + m_name);
                    }

                    if (hasColumn && (prop.getColumn() != null) &&
                        ad.getColumn().getName().equals(
                            prop.getColumn().getName())) {
                        error("Duplicate column name [" +
                              ad.getColumn().getName() + "] in " + m_name);
                    }
                }
            }
        }

        m_props.put(ad.getName(), ad);
        super.add(ad);
    }

    /**
     * Add an EventDef to this ObjectDef
     *
     * @param oed the EventDef to add
     */
    public void add(EventDef oed) {
        m_events.add(oed);
        super.add(oed);
    }

    /**
     * Specify the ObjectKeyDef for this ObjectDef
     *
     * @param key the ObjectKeyDef for this ObjectDef
     */
    public void add(ObjectKeyDef key) {
        m_key = key;
        super.add(m_key);
    }

    /**
     * Add a FlexFieldDef to this ObjectDef
     *
     * @param flex the FlexFieldDef to add
     */
    public void add(FlexFieldDef flex) {
        m_flexFields.add(flex);
        super.add(flex);
    }

    /**
     * Defines a join path between two tables that compose this ObjectType
     *
     * @param jp the JoinPathDef
     */
    public void add(JoinPathDef jp) {
        m_joinPaths.add(jp);
        super.add(jp);
    }

    /**
     * Specifies the "reference key" column for this object type.  Essentially,
     * a reference key is a column that contains the main ID number of the
     * object type instance, and is used to join the object to its super
     * type when necessary.
     *
     * @param refKey ColumnDef defining the reference key column
     */
    public void add(ColumnDef refKey) {
        m_refKey = refKey;
    }

    /**
     * Specify a "path" for a property attribute, of any depth.  Any property
     * listed here will be aggressively loaded by MDSQL.
     *
     * @param path the path of the property
     */
    public void add(String[] path) {
        m_aggressives.add(path);
    }

    /**
     * Confirm that this ObjectDef is in a valid state.  This means that all
     * of the following are valid: its parent object, its properties, its
     * object key, its events, and its flexfields.
     */
    void validate() {
        validate(m_super);
        validate(m_props);
        validate(m_key);
        validate(m_events);
        validate(m_flexFields);

        // I'm going to comment this out for now in order to get incremental
        // loading working. I'll think about how to add it back later.
//          // check the supertype hierarchy to see if there's a loop
//          StringBuffer sb = new StringBuffer();
//          ObjectDef superDef = getSuperObjectDef();

//          sb.append(getName());

//          while (superDef != null) {
//              sb.append(", child of ").append(superDef.getName());
//              if (superDef.equals(this)) {
//                  error(getName() + " has a loop in its inheritance path" +
//                        Utilities.LINE_BREAK + sb.toString());
//              }
//              superDef = superDef.getSuperObjectDef();
//         }
    }

    void validateMappings() {
        for (int i = 0; i < m_events.size(); i++) {
            EventDef ed = (EventDef) m_events.get(i);
            ed.validateMappings(m_type);
        }
        // for now, this is placed here because this method
        // is called even though the validate() method is not.
        // when the validate method is once again called then
        // this line should be removed
        validate(m_props);
    }

    /**
     * Creates the metadata that this ObjectDef represents.  If the
     * ObjectType is already defined, this call aborts.  If there is a
     * super object, the super ObjectType is extracted.  Then the actual
     * ObjectType corresponding to this ObjectDef is created.
     *
     * @param model the Model that the object should belong to
     */
    ObjectType createObjectType() {

        if (m_super != null) {
            m_super.resolve();

            m_type = new ObjectType(m_name, m_super.getResolvedObjectType());
        } else {
            m_type = new ObjectType(m_name);
        }

        if (m_options != null) {
            m_options.setOptions(m_type);
        }

        for (Iterator it = m_aggressives.iterator(); it.hasNext(); ) {
            String[] aggressive = (String[])it.next();

            m_type.addAggressiveLoad(aggressive);
        }

        m_type.setFilename(getFilename());
        m_type.setLineInfo(getLineNumber(), getColumnNumber());

        return m_type;
    }

    /**
     * Returns true if the Events have been generated yet, false if not.
     *
     * @return true if the Events have been generated yet, false if not.
     */
    public boolean getEventsGenerated() {
        return m_eventsGenerated;
    }

    /**
     * Generates Metadata events for this ObjectDef.
     */
    void generateEvents() {
        m_eventsGenerated = true;

        for (int i = 0; i < m_events.size(); i++) {
            EventDef ed = (EventDef) m_events.get(i);

            if (ed.getName() == null) {
                if (m_type.getEvent(ed.getTypeCode()) == null) {
                    m_type.setEvent(ed.getTypeCode(),
                                    ed.generateEvent());
                } else {
                    ed.error(
                        "Duplicate " + ed.getType() +
                        " event definition for object type " +
                        m_type.getQualifiedName()
                        );
                }
            }
        }

        // generate MDSQL events...  later...
        for (int i = 0; i< CompoundType.NUM_EVENT_TYPES; i++) {
            boolean found = false;

            for (int j = 0; j < m_events.size(); j++) {
                EventDef tmp = (EventDef) m_events.get(j);

                if ((tmp.getName() == null) && (tmp.getTypeCode() == i)) {
                    found = true;
                    break;
                }
            }

            // check if event is already defined...
            if (found) {
                continue;
            }

            Event oe =
                MDSQLGeneratorFactory.getInstance().generateEvent(m_type, i);

            if (oe == null) {
                continue;
            }

            // now work backwards and create an EventDef
            EventDef ed = new EventDef(i, null);

            Iterator it = oe.getOperations();

            while (it.hasNext()) {
                Operation block = (Operation)it.next();

                SQLBlockDef blockDef = new SQLBlockDef(block.getSQL());

                Iterator maps = block.getMappings();

                while (maps.hasNext()) {
                    Mapping mapping = (Mapping)maps.next();

                    String[] path = mapping.getPath();
                    MapStatement map = new MappingDef(path,
                                                      mapping.getTable(),
                                                      mapping.getColumn());

                    blockDef.add(map);
                }

                ed.add(blockDef);
            }

            add(ed);
        }

        for (Iterator it = m_type.getDeclaredProperties(); it.hasNext(); ) {
            Property prop = (Property)it.next();

            if (prop.isAttribute()) {
                continue;
            }

mdsqlloop:
            for (int i = 0; i<Property.NUM_EVENT_TYPES; i++) {

                for (int j = 0; j < m_events.size(); j++) {
                    EventDef ed = (EventDef) m_events.get(j);

                    if ((ed.getName() != null) &&
                        (ed.getName().equals(prop.getName())) &&
                        (ed.getTypeCode() == i)) {
                        if (prop.getEvent(ed.getTypeCode()) == null) {
                            prop.setEvent(ed.getTypeCode(), ed.generateEvent());
                            continue mdsqlloop;
                        } else {
                            ed.error(
                                "Duplicate " + ed.getType() +
                                " event definition for property " +
                                prop.getName() + " of object type " +
                                m_type.getQualifiedName()
                                );
                        }
                    }
                }

                if (prop.getEvent(i) == null) {
                    // Use MDSQL to generate an event here
                    Event event = MDSQLGeneratorFactory
                                    .getInstance()
                                    .generateEvent(m_type, prop, i, null);
                }
            }
        }

    }


    /**
     * Generates the attributes and properties that hang off this object type.
     */
    void generateLogicalModel() {
        for (Iterator it = m_props.values().iterator(); it.hasNext(); ) {
            PropertyDef pd = (PropertyDef) it.next();
            Property prop = pd.generateLogicalModel();

            m_type.addProperty(prop);
        }

        if (m_key != null) {
            // have to do it this way since object keys may be composed of
            // several attributes
            m_key.generateKey(m_type);
        } else if ((m_super == null) && (m_props.size() > 0)) {
            error(m_name + " has no object key or super type defined.");
        }
    }

    /**
     * Generates the properties that this object definition refers to.
     **/

    void generateMappingMetadata() {
        if (m_refKey != null) {
            Property prop = (Property) m_type.getKeyProperties().next();
            m_type.setReferenceKey(
                m_refKey.generateLogicalModel(prop.getColumn().getType())
                );
        }

/*
        for (int i = 0; i < m_flexFields.size(); i++ ) {
            FlexFieldDef flex = (FlexFieldDef)m_flexFields.get(i);
            flex.generateLogicalModel(type);
        }
*/

        for (int i = 0; i < m_joinPaths.size(); i++ ) {
            JoinPathDef jp = (JoinPathDef)m_joinPaths.get(i);
            m_type.addJoinPath(jp.generateLogicalModel());
        }
    }


    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        StringBuffer result = new StringBuffer("object type " + m_name);

        if (m_super != null) {
            result.append(" extends " + m_super);
        }

        result.append(" {" + Utilities.LINE_BREAK);

        for (Iterator it = m_props.values().iterator(); it.hasNext(); ) {
            result.append("    " + it.next() + ";" + Utilities.LINE_BREAK);
        }

        for (Iterator it = m_events.iterator(); it.hasNext(); ) {
            result.append(Utilities.LINE_BREAK + it.next() +
                          Utilities.LINE_BREAK);
        }

        result.append("}");

        return result.toString();
    }

}
