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

import com.arsdigita.persistence.metadata.Mapping;

/**
 * Defines a mapping between a attribute (and maybe a role) and a database
 * column.
 *
 * Should eventually be updated to use ColumnDef instead.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 */

public class MappingDef extends MapStatement {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/pdl/ast/MappingDef.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    // the role and attribute to map from
    private String[] m_path;

    // the database column to map to
    private String m_table;
    private String m_column;

    /**
     * Creates a new MappingDef for the given role/attr and database column
     * pairing.
     *
     * @param role the role to map from (null if none)
     * @param attr the attribute of the role to map from
     * @param table the table containing the column (null if implicit)
     * @param column the column to map to
     */
    public MappingDef(String[] path, String table, String column) {
        m_path = path;
        m_table = table;
        m_column = column;
    }

    /**
     * Confirm that the MappingDef is in a valid state.  This means that all
     * non-implicit roles have defined scopes.
     */
    void validate() {
        /*        try {
                  EventDef event = (EventDef) getParent().getParent();
                  String name = event.getName();
                  if (name != null) {
                  if (!(name.equals(m_role) || "link".equals(m_role))) {
                  if (m_role == null)
                  error("Scope required before " + m_attr);
                  else
                  error("No such scope: " + m_role);
                  }
                  }
                  } catch (ClassCastException e) {
                  // Do nothing, this means we're inside a query event.
                  }
        */
    }

    public String[] getPath() {
        return m_path;
    }

    Mapping generateMapping() {
        Mapping mapping = new Mapping(getPath(), m_table, m_column);
        initLineInfo(mapping);
        return mapping;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append(getPrettyPath());
        result.append(" = ");

        if (m_table != null)
            result.append(m_table + ".");

        result.append(m_column);

        return result.toString();
    }

}
