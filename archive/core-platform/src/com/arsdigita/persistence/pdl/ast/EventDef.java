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

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.Event;
import com.arsdigita.persistence.metadata.Operation;

import com.arsdigita.persistence.Utilities;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Defines a metadata event, including potentially its bind variable mappings.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/07/18 $
 */

public class EventDef extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/EventDef.java#3 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    private static final Map s_objectTypes = new HashMap();
    private static final Map s_roleTypes = new HashMap();

    static {
        s_objectTypes.put("retrieve", new Integer(ObjectType.RETRIEVE));
        s_objectTypes.put("all", new Integer(ObjectType.RETRIEVE_ALL));
        s_objectTypes.put("attributes",
                          new Integer(ObjectType.RETRIEVE_ATTRIBUTES));
        s_objectTypes.put("insert", new Integer(ObjectType.INSERT));
        s_objectTypes.put("update", new Integer(ObjectType.UPDATE));
        s_objectTypes.put("delete", new Integer(ObjectType.DELETE));

        s_roleTypes.put("retrieve", new Integer(Property.RETRIEVE));
        s_roleTypes.put("add", new Integer(Property.ADD));
        s_roleTypes.put("remove", new Integer(Property.REMOVE));
        s_roleTypes.put("clear", new Integer(Property.CLEAR));
    }

    private String m_type;
    private String m_name;
    private List m_blocks = new ArrayList();

    public EventDef(String type, String name) {
        m_type = type;
        m_name = name;
    }

    public EventDef(int type, String name) {
        m_name = name;

        Iterator iter = s_objectTypes.entrySet().iterator();
        Integer objType = new Integer(type);

        while (iter.hasNext()) {
            Map.Entry me = (Map.Entry)iter.next();

            if (objType.equals(me.getValue())) {
                m_type = (String)me.getKey();
                break;
            }
        }
    }

    /**
     * Get the name of the event
     *  
     * @return the name of the event
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns the type of event this is
     *
     * @return the type of event this is
     */
    public String getType() {
        return m_type;
    }

    /**
     * Add a SQLBlockDef to this event
     *
     * @param b the SQLBlockDef to add
     */
    public void add(SQLBlockDef b) {
        m_blocks.add(b);
        super.add(b);
    }

    /**
     * Gets the SQLBlockDefs associated with this event
     *
     * @return the SQLBlockDefs associated with this event
     */
    public Iterator getBlocks() {
        return m_blocks.iterator();
    }

    /**
     * Adds a call to "super"
     */
    public void addSuper() {
        m_blocks.add(null);
    }

    /**
     * Checks the validity of this object and all of its SQLBlocks
     */
    void validate() {
        validate(m_blocks);
    }

    void validateMappings(ObjectType type) {
        for (int i = 0; i < m_blocks.size(); i++) {
            SQLBlockDef b = (SQLBlockDef) m_blocks.get(i);
            if (b != null) {
                b.validateMappings(type);
            }
        }
    }

    /**
     * Returns the Event type code of this event
     *
     * @return the int value for the passed in string
     */
    public int getTypeCode() {
        Integer type = (Integer)s_objectTypes.get(m_type);

        if (type == null) {
            type = (Integer)s_roleTypes.get(m_type);

            if (type == null) {
                error(m_type + " is not a valid event type");
            }
        }

        return type.intValue();
    }


    /**
     * Generates a metadata Event.
     */
    Event generateEvent() {
        Event event = new Event();
        initLineInfo(event);

        generateEvent(event);

        return event;
    }

    /**
     * Generates the operations for a metadata Event
     */
    void generateEvent(Event event) {
        ObjectDef od = getObjectDef();
        Iterator blocks = m_blocks.iterator();

        while (blocks.hasNext()) {
            SQLBlockDef b = (SQLBlockDef)blocks.next();

            if (b == null) {
                if (od == null) {
                    error("Can't use super in association events.");
                }

                ObjectType st = od.getObjectType().getSupertype();
                if (st == null) {
                    error("Can't user super if object type doesn't " +
                          "have a superclass.");
                }

                Event superEvent = st.getEvent(getTypeCode());

                if (superEvent == null) {
                    error("Couldn't find super event for " +
                          od.getName());
                }

                for (Iterator it = superEvent.getOperations();
                     it.hasNext(); ) {
                    event.addOperation((Operation) it.next());
                }
            } else {
                event.addOperation(b.generateOperation());
            }
        }
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append("    " + m_type);

        if (m_name != null) {
            result.append(" " + m_name);
        }

        result.append(" {");

        for (int i = 0; i < m_blocks.size(); i++) {
            result.append(Utilities.LINE_BREAK + m_blocks.get(i) + 
                          Utilities.LINE_BREAK);
        }

        result.append("    }");

        return result.toString();
    }

}
