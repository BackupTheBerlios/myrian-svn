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

package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.util.*;
import com.arsdigita.persistence.proto.metadata.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Table
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/02/05 $
 **/

class Table {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Table.java#2 $ by $Author: rhs $, $DateTime: 2003/02/05 18:34:37 $";

    private static final Logger s_log = Logger.getLogger(Table.class);

    private Node m_node;
    private String m_name;
    private Map m_columns = new HashMap();
    private Set m_entering = new HashSet();
    private Set m_leaving = new HashSet();
    private Set m_selections = new HashSet();

    Table(Node node, String name) {
        m_node = node;
        m_name = name;

        node.addTable(this);
    }

    public void remove() {
        for (Iterator it = getConditions().iterator(); it.hasNext(); ) {
            OldCondition cond = (OldCondition) it.next();
            cond.remove();
        }

        ArrayList columns = new ArrayList(getColumns().size());
        columns.addAll(getColumns());
        for (Iterator it = columns.iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            col.remove();
        }

        m_node.removeTable(this);
    }

    public Node getNode() {
        return m_node;
    }

    public Query getQuery() {
        return m_node.getQuery();
    }

    void addColumn(Column column) {
        m_columns.put(column.getName(), column);
    }

    void removeColumn(Column column) {
        m_columns.remove(column.getName());
    }

    Column defineColumn(com.arsdigita.persistence.proto.metadata.Column source) {
        Column column = getColumn(source.getColumnName());
        if (column == null) {
            column = new Column(this, source.getColumnName());
        }

        column.getSources().add(source);

        return column;
    }

    public Column getColumn(String name) {
        return (Column) m_columns.get(name);
    }

    public Collection getColumns() {
        return m_columns.values();
    }

    void addCondition(OldCondition condition) {
        if (condition.getHead().getTable().equals(this)) {
            m_entering.add(condition);
        } else {
            m_leaving.add(condition);
        }
    }

    void removeCondition(OldCondition condition) {
        if (condition.getHead().getTable().equals(this)) {
            m_entering.remove(condition);
        } else {
            m_leaving.remove(condition);
        }
    }

    Set getEntering() {
        return m_entering;
    }

    Set getLeaving() {
        return m_leaving;
    }

    void removeSelection(Selection sel) {
        m_selections.remove(sel);
    }

    void addSelection(Selection sel) {
        m_selections.add(sel);
    }

    public Set getSelections() {
        return m_selections;
    }

    public Set getConditions() {
        Set result = new HashSet();
        result.addAll(m_entering);
        result.addAll(m_leaving);
        return result;
    }

    public String getName() {
        return m_name;
    }

    public String getAlias() {
        String alias = m_node.getAlias();
        if (alias == null) {
            alias = getName();
        } else {
            alias = alias + "__" + getName();
        }
        return getQuery().abbreviate(alias);
    }

    public String toString() {
        return getAlias();
    }

    /**
     * Returns true if this table corresponds to the table that stores the
     * reference key of the most specific type being fetched by this query.
     **/

    boolean isBase() {
        Node node = getNode();
        if (node.equals(getQuery())) {
            ObjectMap map = node.getObjectMap();
            return getName().equals
                (map.getSuperJoin() != null ?
                 map.getSuperJoin().getFrom().getTable().getName() : null);
        } else {
            return false;
        }
    }

    /**
     * Figures out whether removing the given table will effect the number of
     * rows returned by this query.
     **/

    boolean isEliminable() {
        if (isBase()) {
            return false;
        }

        Table other = null;
        for (Iterator it = getConditions().iterator(); it.hasNext(); ) {
            OldCondition cond = (OldCondition) it.next();
            Table candidate;
            if (cond.getHead().getTable().equals(this)) {
                candidate = cond.getTail().getTable();
            } else {
                candidate = cond.getHead().getTable();
            }

            if (other == null) {
                other = candidate;
            } else if (!other.equals(candidate)) {
                // Joins two other tables together and so cannot be
                // eliminated.
                return false;
            }
        }

        if (other == null) {
            // Has no conditions, should be the only table in the query.
            return false;
        }

        if (getNode().isOuter()) {
            // It is going to be outer joined, so it can't influence the
            // number of rows returned.
            return true;
        }

        if (getNode().equals(other.getNode())) {
            // Same node, need to compare rank.
            if (rank() > 0) {
                return true;
            } else {
                return other.rank() <= rank();
            }
        }

        // We must be an inner join at this point.
        if (other.getNode().isOuter()) {
            return false;
        }

        // We now know that the tables are in different nodes and they're both
        // inner joins. In this case we want to give preference to the table
        // that is closer to the root.

        if (getNode().depth() > other.getNode().depth()) {
            return true;
        }

        // Default to false to be safe.
        return false;
    }

    /**
     * Returns the rank of this table within its node. Tables with higher rank
     * have more rows in them.
     **/

    int rank() {
        ObjectMap map = getNode().getObjectMap();
        return map.getRank(map.getRoot().getTable(getName()));
    }

}
