/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

package com.redhat.persistence.metadata;

import java.util.*;

/**
 * UniqueKey
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/08/15 $
 **/

public class UniqueKey extends Constraint {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/metadata/UniqueKey.java#2 $ by $Author: dennis $, $DateTime: 2003/08/15 13:46:34 $";

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
