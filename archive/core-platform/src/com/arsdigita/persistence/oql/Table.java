package com.arsdigita.persistence.oql;

import com.arsdigita.util.*;
import com.arsdigita.persistence.metadata.*;
import java.util.*;
import org.apache.log4j.Category;

/**
 * Table
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/05/21 $
 **/

class Table {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Table.java#2 $ by $Author: rhs $, $DateTime: 2002/05/21 20:57:49 $";

    private static final Category s_log = Category.getInstance(Table.class);

    private Node m_node;
    private String m_name;
    private Map m_columns = new HashMap();
    private Set m_entering = new HashSet();
    private Set m_leaving = new HashSet();

    Table(Node node, String name) {
        m_node = node;
        m_name = name;

        node.addTable(this);
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
        if (condition.getRight().getTable().equals(this)) {
            if (m_entering.size() > 0) {
                getQuery().error("Table is already targeted: " + getAlias());
            }
            m_entering.add(condition);
        } else {
            m_leaving.add(condition);
        }
    }

    Set getEntering() {
        return m_entering;
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
        return getQuery().abbreviate(m_node.getAlias() + "__" + getName());
    }

    public String toString() {
        return getAlias();
    }

}
