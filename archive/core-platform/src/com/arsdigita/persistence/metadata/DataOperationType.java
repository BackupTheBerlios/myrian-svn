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
 * The OperationType class is used to describe
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

public class DataOperationType extends ModelElement {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/DataOperationType.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    /**
     * The name of this operation.
     **/
    private String m_name;

    /**
     * The event that executes this operation.
     **/
    private Event m_event;


    /**
     * Constructs a new DataOperation type with the given name, executed by
     * the given event.
     *
     * @param name The name of the DataOperationType.
     * @param event The Event used to execute this DataOperationType.
     *
     * @exception IllegalArgumentException If name is null or empty.
     * @exception IllegalArgumentException If event is null.
     **/

    public DataOperationType(String name, Event event) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException
                ("name must be non null and non empty");
        }

        if (event == null) {
            throw new IllegalArgumentException("event must be non null");
        }

        m_name = name;
        m_event = event;
    }


    /**
     * Returns the name of this DataOperationType.
     *
     * @return The name of this DataOperationType.
     **/

    public String getName() {
        return m_name;
    }


    /**
     * Returns the qualified name of this DataOperationType. The qualified
     * name consists of the model name followed by a "." followed by the name
     * of this DataOperationType.
     *
     * @return The qualified name of this DataOperationType.
     **/

    public String getQualifiedName() {
        Model m = getModel();
        if (m == null) {
            return getName();
        } else {
            return m.getName() + "." + getName();
        }
    }


    /**
     * Gets the event used to execute this DataOperationType.
     *
     * @return The event used to execute this DataOperationType.
     **/

    public Event getEvent() {
        return m_event;
    }

    void outputPDL(PrintStream out) {
        out.print("data operation " + m_name + " {");

        for (Iterator it = m_event.getOperations(); it.hasNext(); ) {
            Operation op = (Operation) it.next();
            out.println();
            op.outputPDL(out);
            out.println();
        }

        out.print("}");
    }

}
