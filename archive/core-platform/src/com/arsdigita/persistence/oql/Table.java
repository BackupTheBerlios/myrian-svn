package com.arsdigita.persistence.oql;

import com.arsdigita.util.*;
import com.arsdigita.persistence.metadata.*;
import java.util.*;
import org.apache.log4j.Category;

/**
 * Table
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

class Table {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Table.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private static final Category s_log = Category.getInstance(Table.class);

    private Node m_node;
    private String m_name;
    private Map m_columns = new HashMap();
    private Set m_conditions = new HashSet();

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

    Column defineColumn(String name) {
        Column column = getColumn(name);
        if (column == null) {
            column = new Column(this, name);
        }

        return column;
    }

    public Column getColumn(String name) {
        return (Column) m_columns.get(name);
    }

    public Collection getColumns() {
        return m_columns.values();
    }

    void addCondition(Condition condition) {
        m_conditions.add(condition);
    }

    public Set getConditions() {
        return m_conditions;
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
