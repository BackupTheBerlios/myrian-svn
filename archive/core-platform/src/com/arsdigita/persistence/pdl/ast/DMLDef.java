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

import com.arsdigita.persistence.metadata.DataOperationType;
import com.arsdigita.persistence.metadata.Event;
import com.arsdigita.persistence.Utilities;
import java.util.Iterator;


/**
 * Defines a named DML statement that is not associated with an object type or 
 * association.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */
public class DMLDef extends NamedSQLDef {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/DMLDef.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    /**
     * Create a new DMLDef named "name"
     *
     * @param name named DML name
     */
    public DMLDef(String name) {
        super(name);
    }

    /**
     * Creates the metadata for this DataOperationDef
     */
    DataOperationType generateEvents() {
        Event event = new Event();

        event.addOperation(m_sql.generateOperation());
	DataOperationType result = new DataOperationType(m_name, event);

	if (m_options != null) {
	    m_options.setOptions(result);
	}

        return result;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append("dml " + m_name + " {" + Utilities.LINE_BREAK);

        for (Iterator it = m_attrs.values().iterator(); it.hasNext(); ) {
            result.append("    " + it.next() + ";" + Utilities.LINE_BREAK);
        }

        result.append(Utilities.LINE_BREAK + m_sql);

        result.append(Utilities.LINE_BREAK + "}");

        return result.toString();
    }

}
