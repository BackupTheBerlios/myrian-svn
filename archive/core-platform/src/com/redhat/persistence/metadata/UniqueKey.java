/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.redhat.persistence.metadata;

import java.util.HashSet;
import java.util.Set;

/**
 * UniqueKey
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2004/03/30 $
 **/

public class UniqueKey extends Constraint {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/metadata/UniqueKey.java#4 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    private Set m_foreignKeys = new HashSet();

    public UniqueKey(Table table, String name, Column[] columns) {
        super(checkDuplicates(table, columns), name, columns);
    }

    private static final Table checkDuplicates(Table table,
                                               Column[] columns) {
        if (table.getUniqueKey(columns) != null) {
            throw new IllegalArgumentException
                ("Table already has a unique key: " + table.getName());
        }
        return table;
    }

    public UniqueKey(String name, Column column) {
        this(column.getTable(), null, new Column[] {column});
    }

    public boolean isPrimaryKey() {
        return this.equals(getTable().getPrimaryKey());
    }

    public Set getForeignKeys() {
        return m_foreignKeys;
    }

    void addForeignKey(ForeignKey fk) {
        m_foreignKeys.add(fk);
    }

    public boolean isDeferred() {
        return false;
    }

    String getSuffix() {
        if (isPrimaryKey()) {
            return "_p";
        } else {
            return "_u";
        }
    }

    String getColumnSQL() {
        String keyword = isPrimaryKey() ? "primary key" : "unique";
        if (getName() == null) {
            return "        " + keyword;
        } else {
            return "        constraint " + getName() + "\n          " +
                keyword;
        }
    }

    public String getSQL() {
        String keyword = isPrimaryKey() ? "primary key" : "unique";
        if (getName() == null) {
            return "    " + keyword + getColumnList();
        } else {
            return "    constraint " + getName() + "\n      " + keyword +
                getColumnList();
        }
    }

}
