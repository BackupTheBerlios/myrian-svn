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

import java.util.Iterator;
import java.io.PrintStream;

/**
 * The Association class is used to link together the properties of two object
 * types. When such a link is made there can be data stored along with each
 * link.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/05/21 $
 **/

public class Association extends ModelElement {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/Association.java#2 $ by $Author: rhs $, $DateTime: 2002/05/21 20:57:49 $";


    /**
     * The properties that are being linked together.
     **/
    private Property[] m_roles = new Property[2];

    /**
     * The type describing any data stored along with the link. This is null
     * if there is no data stored with the link.
     **/
    private ObjectType m_linkType = new ObjectType("<link>");


    /**
     * Constructs an Association for the two specified properties with the
     * given link type.
     *
     * @param roleOne The first end of the association.
     * @param roleTwo The second end of the association.
     * 
     * @pre roleOne.isRole() && roleTwo.isRole()
     * @pre roleOne.getAssociation() == null && roleTwo.getAssociation() == null
     *
     * @post roleOne.getAssociation() == this && roleTwo.getAssociation() == this
     *
     * @exception IllegalArgumentException If roleOne is null.
     * @exception IllegalArgumentException If roleTwo is null.
     * @exception IllegalArgumentException If roleOne is not a role.
     * @exception IllegalArgumentException If roleTwo is not a role.
     **/

    public Association(Property roleOne, Property roleTwo) {
        if (roleOne == null) {
            throw new IllegalArgumentException(
                "The roleOne parameter must be non null."
                );
        }

        if (roleTwo == null) {
            throw new IllegalArgumentException(
                "The roleTwo parameter must be non null."
                );
        }

        if (!roleOne.isRole()) {
            throw new IllegalArgumentException(
                "The roleOne parameter must be a role."
                );
        }

        if (!roleTwo.isRole()) {
            throw new IllegalArgumentException(
                "The roleTwo parameter must be a role."
                );
        }
        if ( roleOne.getAssociation() != null ) {
            throw new IllegalArgumentException(
                "The roleOne parameter is already joined to an Association!"
                );
   
        }        
        if ( roleTwo.getAssociation() != null ) {
            throw new IllegalArgumentException(
                "The roleTwo parameter is already joined to an Association!"
                );
   
        }        
        m_roles[0] = roleOne;
        m_roles[1] = roleTwo;
        m_roles[0].setAssociation(this);
        m_roles[1].setAssociation(this);

        Property prop = new Property(
            m_roles[0].getName(), m_roles[0].getType(),
            Property.REQUIRED, m_roles[0].isComponent()
            );
        prop.setLineInfo(m_roles[0]);
        m_linkType.addProperty(prop);            
        m_linkType.addKeyProperty(m_roles[0].getName());

        prop = new Property(m_roles[1].getName(), m_roles[1].getType(),
                            Property.REQUIRED, m_roles[1].isComponent());
        prop.setLineInfo(m_roles[1]);
        m_linkType.addProperty(prop);
        m_linkType.addKeyProperty(m_roles[1].getName());

        // Make sure the link type has an empty insert event.
        m_linkType.setEvent(ObjectType.INSERT, newEvent());
        m_linkType.setEvent(ObjectType.DELETE, newEvent());
        for (Iterator it = m_linkType.getKeyProperties(); it.hasNext(); ) {
            prop = (Property) it.next();
            prop.setEvent(Property.ADD, newEvent());
            prop.setEvent(Property.REMOVE, newEvent());
        }
    }

    private final Event newEvent() {
        Event result = new Event();
        result.setLineInfo(m_roles[0]);
        return result;
    }


    /**
     * Gets the DataType to be used as a link in this Association.
     *
     * @return The DataType to be used as a link.
     **/

    public CompoundType getLinkType() {
        return m_linkType;
    }

    /**
     * Gets the associated property.
     **/

    public Property getAssociatedProperty(Property prop) {
        if (prop.equals(m_roles[0])) {
            return m_roles[1];
        } else {
            return m_roles[0];
        }
    }

    /**
     * Gets the first role property.    
     *
     * @returns the first role property
     */
    public Property getRoleOne() {
        return m_roles[0];
    }
    /**
     * Gets the second role property.    
     *
     * @returns the second role property
     */
    public Property getRoleTwo() {
        return m_roles[1];
    }

    /**
     * Outputs a serialized representation of this Association on the given
     * PrintStream.
     *
     * The following format is used:
     *
     * <pre>
     *     "association" "{"
     *         &lt;roleOne&gt; ";"
     *         &lt;roleTwo&gt; ";"
     *
     *         &lt;properties&gt; ";"
     *     "}"
     * </pre>
     **/

    public void outputPDL(PrintStream out) {
        out.println("association {");

        for (int i = 0; i < m_roles.length; i++) {
            out.print("    ");
            m_roles[i].outputPDL(out);
            out.println(";");
        }

        if (m_linkType != null) {
            out.println();

        outer:
            for (Iterator it = m_linkType.getProperties(); it.hasNext(); ) {
                Property p = (Property) it.next();
                for (int i = 0; i < m_roles.length; i++) {
                    if (p.getName().equals(m_roles[i].getName())) {
                        continue outer;
                    }
                }
                out.print("    ");
                p.outputPDL(out);
                out.println(";");
            }
        }

        m_roles[0].outputPDLEvents(out);
        m_roles[1].outputPDLEvents(out);

        out.println("}");
    }

}
