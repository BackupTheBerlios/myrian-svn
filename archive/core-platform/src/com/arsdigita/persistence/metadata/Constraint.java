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

package com.arsdigita.persistence.metadata;

import java.util.*;

/**
 * Constraint
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2002/08/14 $
 **/

abstract class Constraint {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/Constraint.java#4 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    private Table m_table;
    private String m_name;
    private Column[] m_columns;

    Constraint(Table table, String name, Column[] columns) {
        m_table = table;
        m_name = name;
        m_columns = columns;

        if (m_table.getConstraint(getClass(), m_columns) != null) {
            throw new IllegalArgumentException(
                                               "Table already has constraint: " + m_table.getName()
                                               );
        }

        m_table.addConstraint(this);

        Set cols = new HashSet();
        for (int i = 0; i < m_columns.length; i++) {
            m_columns[i].addConstraint(this);
            cols.add(m_columns[i]);
            if (!m_columns[i].getTable().equals(table)) {
                throw new IllegalArgumentException(
                                                   "All column constraints must be from the same table."
                                                   );
            }
        }

        if (cols.size() != m_columns.length) {
            throw new IllegalArgumentException(
                                               "Duplicate columns"
                                               );
        }

        if (m_name == null) {
            generateName();
        }
    }

    private String generateName() {
        StringBuffer buf = new StringBuffer();

        buf.append(abbreviate(m_table.getName()));

        for (int i = 0; i < m_columns.length; i++) {
            buf.append("_");
            buf.append(m_columns[i].getName());
        }

        if (buf.length() > 27) {
            buf.setLength(27);
        }

        buf.append(getSuffix());

        String result = buf.toString();

        return result;
    }

    private static final String abbreviate(String name) {
        StringBuffer result = new StringBuffer();
        boolean grab = true;
        for (int i = 0; i < name.length(); i++) {
            if (grab) {
                result.append(name.charAt(i));
                grab = false;
            }

            if (name.charAt(i) == '_') {
                grab = true;
            }
        }

        return result.toString();
    }

    public Table getTable() {
        return m_table;
    }

    public String getName() {
        //if (m_name == null) {
        //return generateName();
        //} else {
        return m_name;
        // }
    }

    public Column[] getColumns() {
        return m_columns;
    }

    abstract boolean isDeferred();

    abstract String getSuffix();

    abstract String getColumnSQL();

    abstract String getSQL();

    String getColumnList() {
        return getColumnList(false);
    }

    String getColumnList(boolean sort) {
        List cols = new ArrayList(Arrays.asList(m_columns));

        if (sort) {
            Collections.sort(cols, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        Column c1 = (Column) o1;
                        Column c2 = (Column) o2;
                        return c1.getName().compareTo(c2.getName());
                    }
                });
        }

        StringBuffer result = new StringBuffer("(");

        for (Iterator it = cols.iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            result.append(col.getName());
            if (it.hasNext()) {
                result.append(", ");
            }
        }

        result.append(")");

        return result.toString();
    }

}
