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

package com.arsdigita.persistence.pdl.ast;

import com.arsdigita.persistence.Utilities;
import com.arsdigita.persistence.metadata.Association;
import com.arsdigita.persistence.metadata.MDSQLGenerator;
import com.arsdigita.persistence.metadata.MDSQLGeneratorFactory;
import com.arsdigita.persistence.metadata.CompoundType;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * This represents the "Association" block from a PDL file.  It is used to
 * represent a mapping between two object types, which can include various
 * additional properties and SQL Events.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #9 $ $Date: 2002/11/26 $
 */

public class AssociationDef extends Element {

    private static final Logger s_log = Logger.getLogger(AssociationDef.class);

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/AssociationDef.java#9 $ by $Author: vadim $, $DateTime: 2002/11/26 18:30:20 $";

    // the two PropertyDefs that define what Objects are being associated
    private PropertyDef m_one;
    private PropertyDef m_two;

    // additional PropertyDefs
    private Map m_attrs = new HashMap();

    // the SQL Events
    private List m_events = new ArrayList();

    // The option block associated with this association.
    private OptionBlock m_options = null;

    /**
     * Create a new AssociationDef that associates the two given PropertyDefs.
     *
     * @param one a PropertyDef to associated
     */
    public AssociationDef(PropertyDef one, PropertyDef two) {
        m_one = one;
        m_two = two;

        super.add(m_one);
        super.add(m_two);
    }

    /**
     * Add an additional property to the association
     *
     * @param ad the propertydef to add
     */
    public void add(PropertyDef ad) {
        m_attrs.put(ad.getName(), ad);
        super.add(ad);
    }

    /**
     * Add an Event to the association
     *
     * @param ed the EventDef to add
     */
    public void add(EventDef ed) {
        m_events.add(ed);
        super.add(ed);
    }


    /**
     * Adds the option block to this AssociationDef.
     **/

    public void add(OptionBlock options) {
        m_options = options;
    }

    void validateMappings() {
        for (int i = 0; i < m_events.size(); i++) {
            EventDef ed = (EventDef) m_events.get(i);
            // Nothing to pass in for now.
            // ed.validateMappings();
        }
    }

    /**
     * Returns a named propertydef, or null.
     *
     * @param name the name of the propertydef to return
     * @return a named propertydef, or null.
     */
    public PropertyDef getPropertyDef(String name) {
        if (name.equals(m_one.getName())) {
            return m_one;
        }

        if (name.equals(m_two.getName())) {
            return m_two;
        }

        return (PropertyDef)m_attrs.get(name);
    }

    private Association m_assn = null;

    /**
     * Create the metadata for this association beneath a certain Model
     *
     * @param model the model that owns this association
     */
    Association generateLogicalModel() {
        Property one = m_one.generateLogicalModel();
        Property two = m_two.generateLogicalModel();

        try {
            ((ObjectType) one.getType()).addProperty(two);
            ((ObjectType) two.getType()).addProperty(one);
        } catch (ClassCastException e) {
            error("Roles must be object types.");
        }

        m_assn = new Association(one, two);
        initLineInfo(m_assn);

        ObjectType link = (ObjectType) m_assn.getLinkType();
        initLineInfo(link);

        Iterator props = m_attrs.values().iterator();

        while (props.hasNext()) {
            PropertyDef propDef = (PropertyDef)props.next();

            Property prop = propDef.generateLogicalModel();
            link.addProperty(prop);
        }

        if (m_options != null) {
            m_options.setOptions(m_assn);
        }

        return m_assn;
    }

    void generateAssociationEvents() {
        MDSQLGenerator generator = MDSQLGeneratorFactory.getInstance();
        Property one = m_assn.getRoleOne();
        Property two = m_assn.getRoleTwo();
        ObjectType link = (ObjectType) m_assn.getLinkType();

        Iterator events;

        int i;

        // generate events for all the random associations
        event_loop_one:
        for (i = 0; i < Property.NUM_EVENT_TYPES; i++) {
            events = m_events.iterator();

            while (events.hasNext()) {
                EventDef ed = (EventDef)events.next();

                if ((ed.getTypeCode() == i) && ed.getName() != null &&
                    ed.getName().equals(one.getName())) {
                    one.setEvent(ed.getTypeCode(), ed.generateEvent());
                    continue event_loop_one;
                }
            }

            generator.generateEvent((ObjectType)two.getType(), one, i, link);
        }

        event_loop_two:
        for (i = 0; i < Property.NUM_EVENT_TYPES; i++) {
            events = m_events.iterator();

            while (events.hasNext()) {
                EventDef ed = (EventDef)events.next();

                if ((ed.getTypeCode() == i) && ed.getName() != null &&
                    ed.getName().equals(two.getName())) {
                    two.setEvent(ed.getTypeCode(), ed.generateEvent());
                    continue event_loop_two;
                }
            }

            generator.generateEvent((ObjectType)one.getType(), two, i, link);
        }

        events = m_events.iterator();

        // do the rest of the events now
        while (events.hasNext()) {
            EventDef ed = (EventDef)events.next();

            if (ed.getName() == null) {
                if (ed.getType().equals("add")) {
                    one.setEvent(Property.ADD, ed.generateEvent());
                    two.setEvent(Property.ADD, ed.generateEvent());
                } else if (ed.getType().equals("remove")) {
                    one.setEvent(Property.REMOVE, ed.generateEvent());
                    two.setEvent(Property.REMOVE, ed.generateEvent());
                } else if (ed.getType().equals("insert") ||
                           ed.getType().equals("delete")) {
                    ed.error(ed.getType() + " events not allowed here.");
                } else if (link.getEvent(ed.getTypeCode()) == null) {
                    link.setEvent(ed.getTypeCode(), ed.generateEvent());
                } else {
                    ed.error(
                             "Duplicate " + ed.getType() +
                             " event definition for association."
                             );
                }
            } else if (!ed.getName().equals(one.getName()) &&
                       !ed.getName().equals(two.getName())) {
                Property prop = link.getProperty(ed.getName());
                if (prop != null) {
                    if (prop.getEvent(ed.getTypeCode()) == null) {
                        prop.setEvent(ed.getTypeCode(), ed.generateEvent());
                    } else {
                        ed.error(
                                 "Duplicate " + ed.getType() +
                                 " event definition for property" +
                                 prop.getName()
                                 );
                    }
                }
            }
        }
        generator.generateAssociationEvent(link, CompoundType.UPDATE);
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append("association {" + Utilities.LINE_BREAK);
        result.append("    " + m_one);
        result.append(Utilities.LINE_BREAK + "    " + m_two);

        for (Iterator it = m_attrs.values().iterator(); it.hasNext(); ) {
            result.append(Utilities.LINE_BREAK + "    " + it.next());
        }

        for (int i = 0; i < m_events.size(); i++) {
            result.append(Utilities.LINE_BREAK + m_events.get(i) +
                          Utilities.LINE_BREAK);
        }

        result.append(Utilities.LINE_BREAK + "}");

        return result.toString();
    }

}
