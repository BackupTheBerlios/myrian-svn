/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Table
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2004/05/02 $
 **/

public class Table extends Element {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/metadata/Table.java#3 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    private String m_name;
    private Mist m_columns = new Mist(this);
    private Set m_constraints = new HashSet();
    private UniqueKey m_key = null;

    public Table(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public Root getRoot() {
        return (Root) getParent();
    }

    public void setPrimaryKey(UniqueKey constraint) {
        m_key = constraint;
    }

    public UniqueKey getPrimaryKey() {
        return m_key;
    }

    private static ThreadLocal s_cols = new ThreadLocal() {
        public Object initialValue() {
            return new HashSet();
        }
    };

    private static ThreadLocal s_key = new ThreadLocal() {
        public Object initialValue() {
            return new HashSet();
        }
    };

    Constraint getConstraint(Class type, Column[] columns) {
        Set cols = (Set) s_cols.get();
        cols.clear();
        for (int i = 0; i < columns.length; i++) {
            cols.add(columns[i]);
        }

        Set key = (Set) s_key.get();
        key.clear();
        for (Iterator it = m_constraints.iterator(); it.hasNext(); ) {
            Constraint con = (Constraint) it.next();
            if (type.isInstance(con)) {
                Column[] keyCols = con.getColumns();
                key.clear();
                for (int i = 0; i < keyCols.length; i++) {
                    key.add(keyCols[i]);
                }

                if (cols.equals(key)) {
                    return con;
                }
            }
        }

        return null;
    }

    public UniqueKey getUniqueKey(Column[] columns) {
        return (UniqueKey) getConstraint(UniqueKey.class, columns);
    }

    public UniqueKey getUniqueKey(Column column) {
        return getUniqueKey(new Column[] {column});
    }

    public ForeignKey getForeignKey(Column[] columns) {
        return (ForeignKey) getConstraint(ForeignKey.class, columns);
    }

    public ForeignKey getForeignKey(Column column) {
        return getForeignKey(new Column[] {column});
    }

    public void addColumn(Column column) {
        m_columns.add(column);
    }

    public Column getColumn(String name) {
        return (Column) m_columns.get(name);
    }

    void addConstraint(Constraint constraint) {
        m_constraints.add(constraint);
    }

    public Set getConstraints() {
        return m_constraints;
    }

    public String getSQL() {
        return getSQL(true);
    }

    public String getSQL(boolean defer) {
        StringBuffer result = new StringBuffer();

        List columns = new ArrayList();
        columns.addAll(m_columns);

        for (int i = 0; i < columns.size(); i++) {
            Column col = (Column) columns.get(i);
            if (col.hasPrimaryKey()) {
                columns.remove(i);
                columns.add(0, col);
            }
        }

        result.append("create table " + getName() + " (\n");

        String comment = null;
        for (int i = 0; i < columns.size(); i++) {
            Column column = (Column) columns.get(i);
            result.append(column.getInlineSQL(defer));
            boolean hasNext = i < columns.size() - 1;
            if (hasNext) {
                result.append(",\n");
            }
            if (defer && column.hasDeferredConstraints()) {
                comment = "        -- referential constraint for " +
                    column.getName() +
                    " deferred due to circular dependencies";
                if (hasNext) {
                    result.append(comment);
                    result.append("\n");
                    comment = null;
                }
            }
        }

        boolean compoundDeferred = false;

        for (Iterator it = m_constraints.iterator(); it.hasNext(); ) {
            Constraint con = (Constraint) it.next();
            if (con.getColumns().length > 1) {
                if (con.isDeferred()) {
                    compoundDeferred = true;
                } else {
                    if (comment != null) {
                        result.append(",\n");
                        result.append(comment);
                        result.append("\n");
                        comment = null;
                    } else {
                        result.append(",\n");
                    }
                    result.append(con.getSQL());
                }
            }
        }

        if (comment != null) {
            result.append("\n");
            result.append(comment);
            comment = null;
        }

        if (compoundDeferred) {
            result.append("\n    -- compound referential constraints " +
                          "deferred due to circular dependencies");
        }

        result.append("\n)");

        return result.toString();
    }

    public boolean isCircular() {
        return getAllDependencies().contains(this);
    }

    private Set getAllDependencies() {
        Set result = new HashSet();

        getDependencies(result, true);

        int before;

        do {
            before = result.size();
            List tables = new ArrayList();
            tables.addAll(result);

            for (Iterator it = tables.iterator(); it.hasNext(); ) {
                Table table = (Table) it.next();
                table.getDependencies(result, true);
            }
        } while (result.size() > before);

        return result;
    }

    public Set getDependencies() {
        Set result = new HashSet();
        getDependencies(result, false);
        return result;
    }

    private void getDependencies(Set result, boolean includeDeferred) {
        for (Iterator it = m_constraints.iterator(); it.hasNext(); ) {
            Constraint con = (Constraint) it.next();
            if (!includeDeferred && con.isDeferred()) {
                continue;
            }
            if (con instanceof ForeignKey) {
                ForeignKey fk = (ForeignKey) con;
                Table table = fk.getUniqueKey().getTable();
                if (table != this) {
                    result.add(table);
                }
            }
        }
    }

    Object getElementKey() {
        return getName();
    }

    public String toString() {
        return m_name;
    }

}
