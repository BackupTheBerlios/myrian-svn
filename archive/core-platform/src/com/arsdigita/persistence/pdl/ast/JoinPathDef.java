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

import java.util.List;
import java.util.Iterator;

/**
 * Defines a JoinPath, representing a path from one object type or table
 * to another.  Used for Metadata-driven SQL.
 *
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @version $Revision: #4 $ $Date: 2002/11/01 $
 */

public class JoinPathDef extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/JoinPathDef.java#4 $ by $Author: vadim $, $DateTime: 2002/11/01 09:30:48 $";

    private List m_path;

    /**
     * Create a new JoinPathDef to specify the path to an object.
     *
     * @param target the object at the end of the path
     * @param path a List of JoinElementDefs specifying the path to target
     * @pre name != null && path != null
     */
    public JoinPathDef(List path) {
        m_path = path;
    }

    /**
     * Get the column name.
     *
     * @return the column name (and table if applicable)
     */
    public String getName() {
        StringBuffer sb = new StringBuffer();
        Iterator it = m_path.iterator();

        while (it.hasNext()) {
            sb.append(((JoinElementDef)it.next()).toString()).append(" ");
        }

        return sb.toString();
    }

    /**
     * Generates a JoinPath that represents this path.
     */
    com.arsdigita.persistence.metadata.JoinPath generateLogicalModel() {
        com.arsdigita.persistence.metadata.JoinPath jp =
            new com.arsdigita.persistence.metadata.JoinPath();

        Iterator elementDefs = m_path.iterator();

        while (elementDefs.hasNext()) {
            JoinElementDef elementDef = (JoinElementDef)elementDefs.next();

            jp.addJoinElement(elementDef.generateLogicalModel());
        }

        return jp;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return getName();
    }

}
