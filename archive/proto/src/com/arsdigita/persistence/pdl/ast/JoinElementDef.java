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

import com.arsdigita.persistence.metadata.Column;
import com.arsdigita.persistence.metadata.JoinElement;

/**
 * Defines a mapping between two particular columns, used by JoinPathDef
 * to support MDSQL.
 *
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 */

public class JoinElementDef extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/pdl/ast/JoinElementDef.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    private ColumnDef m_from;
    // the "start" column

    private ColumnDef m_to;
    // the "goal" column

    // so we can tell the Column where to add itself
    public static final int FROM = 0;
    public static final int TO   = 1;

    /**
     * Create a new JoinElementDef to specify part of a path to an object.
     * @pre from != null && to != null
     */
    public JoinElementDef(ColumnDef from, ColumnDef to) {
        m_from = from;
        m_to = to;
    }

    /**
     * Get the path element name.
     *
     * @return the column name (and table if applicable)
     */
    public String getName() {
        return "join " + m_from.toString() + " to " + m_to.toString();
    }

    /**
     * Generates a JoinElement that represents a path element.
     */
    JoinElement generateLogicalModel() {
        Column from;
        Column to;

        from = m_from.generateLogicalModel();
        to = m_to.generateLogicalModel();

        return new JoinElement(from, to);
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
