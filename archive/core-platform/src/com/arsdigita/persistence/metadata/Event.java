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
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * An Event contains an ordered set of Operations.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/07/18 $
 */

public class Event extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/Event.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    /**
     * The operations this event performs.
     **/
    private List m_ops = new ArrayList();


    /**
     * Adds the specified Operation to this Event.
     *
     * @param op The operation to add.
     **/

    public void addOperation(Operation op) {
        m_ops.add(op);
    }

    /**
     * Returns an Iterator containing all the Operations that this Event has.
     *
     * @return An Iterator of Operation objects.
     *
     * @see Operation
     **/

    public Iterator getOperations() {
        return m_ops.iterator();
    }

    /**
     * Outputs a serialized representation of this Event.
     *
     * @param out The PrintStream to use for output.
     **/
    void outputPDL(PrintStream out) {
        out.print("{");

        for (int i = 0; i < m_ops.size(); i++) {
            Element el = (Element) m_ops.get(i);
            out.println();
            el.outputPDL(out);
            out.println();
        }

        out.print("    }");
    }

}
