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

package com.arsdigita.persistence.oql;

import java.util.Set;
import java.util.HashSet;

/**
 * Column
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

class Column {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/oql/Column.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    private Table m_table;
    private String m_name;
    private Set m_sources = new HashSet();

    public Column(Table table, String name) {
        m_table = table;
        m_name = name;

        m_table.addColumn(this);
    }

    public void remove() {
        m_table.removeColumn(this);
    }

    public Table getTable() {
        return m_table;
    }

    String getFullName() {
        return m_table.getName() + "." + getName();
    }

    public String getName() {
        return m_name;
    }

    public String getQualifiedName() {
        return m_table.getAlias() + "." + m_name;
    }

    public String toString() {
        return getQualifiedName();
    }

    Set getSources() {
        return m_sources;
    }

}
