package com.arsdigita.persistence.oql;

import com.arsdigita.util.*;
import com.arsdigita.persistence.metadata.*;
import java.util.*;
import org.apache.log4j.Category;

/**
 * Table
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2002/06/10 $
 **/

class Table {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Table.java#5 $ by $Author: rhs $, $DateTime: 2002/06/10 15:35:38 $";

    private static final Category s_log = Category.getInstance(Table.class);

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
            Condition cond = (Condition) it.next();
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

    Column defineColumn(com.arsdigita.persistence.metadata.Column source) {
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

    void addCondition(Condition condition) {
        if (condition.getHead().getTable().equals(this)) {
            m_entering.add(condition);
        } else {
            m_leaving.add(condition);
        }
    }

    void removeCondition(Condition condition) {
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
            ObjectType type = node.getObjectType();
            return getName().equals(
                type.getReferenceKey() != null ?
                type.getReferenceKey().getTableName() : null
                );
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
            Condition cond = (Condition) it.next();
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
        int result = 0;
        ObjectType type = getNode().getObjectType();

        while (type != null) {
            com.arsdigita.persistence.metadata.Column refKey =
                type.getReferenceKey();
            if (refKey != null &&
                refKey.getTableName().equals(getName())) {
                break;
            }

            for (Iterator it = type.getJoinPaths(); it.hasNext(); ) {
                JoinPath jp = (JoinPath) it.next();
                for (Iterator iter = jp.getJoinElements(); iter.hasNext(); ) {
                    JoinElement je = (JoinElement) iter.next();
                    if (je.getFrom().getTableName().equals(getName()) ||
                        je.getTo().getTableName().equals(getName())) {
                        break;
                    }
                }
            }

            type = type.getSupertype();
            result++;
        }

        return result;
    }

}
