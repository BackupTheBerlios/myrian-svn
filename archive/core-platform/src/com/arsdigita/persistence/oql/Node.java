package com.arsdigita.persistence.oql;

import com.arsdigita.util.*;
import com.arsdigita.persistence.metadata.*;
//import com.arsdigita.persistence.metadata.Column;
import java.util.*;
import org.apache.log4j.Category;

/**
 * Node
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

abstract class Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Node.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private static final Category s_log = Category.getInstance(Node.class);

    private Node m_parent;
    private ObjectType m_type;

    private Map m_children = new HashMap();
    private Map m_selections = new HashMap();
    private Map m_tables = new HashMap();

    public Node(Node parent, ObjectType type) {
        m_parent = parent;
        m_type = type;

        fetchKey();
    }

    public Node getParent() {
        return m_parent;
    }

    public ObjectType getObjectType() {
        return m_type;
    }

    Collection getSelections() {
        return m_selections.values();
    }

    void addTable(Table table) {
        m_tables.put(table.getName(), table);
    }

    Table getTable(String name) {
        return (Table) m_tables.get(name);
    }

    Collection getTables() {
        return m_tables.values();
    }

    /**
     * Instructs this Query to fetch all the properties that form the object
     * key for the object type of this query.
     **/

    public void fetchKey() {
        for (Iterator it = m_type.getKeyProperties(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            fetch(prop.getName());
        }
    }


    /**
     * Instructs this Query to fetch all the default properties for this
     * object type.
     **/

    public void fetchDefault() {
        for (Iterator props = m_type.getProperties(); props.hasNext(); ) {
            Property p = (Property) props.next();
            if (!p.isRole()) {
                fetch(p.getName());
            }
        }
        for (Iterator aggs = m_type.getAllAggressiveLoads();
             aggs.hasNext(); ) {
            String[] ag = (String[]) aggs.next();
            String p = StringUtils.join(ag, '.');
            fetch(p);
        }
    }


    /**
     * Instructs this Query to fetch the property with the specified path. If
     * the path follows an association, the object key for that association
     * will automatically be fetched.
     *
     * @param path A path identifying the property to be fetched.
     **/

    public void fetch(String path) {
        String first, rest;
        int index = path.indexOf('.');
        if (index < 0) {
            first = path;
            rest = null;
        } else {
            first = path.substring(0, index);
            rest = path.substring(index+1);
        }

        Property prop = m_type.getProperty(first);

        if (prop == null) {
            throw new IllegalArgumentException("No such property: " + first);
        }

        if (prop.isAttribute() && rest != null) {
            throw new IllegalArgumentException("Not a role: " + first);
        }

        if (prop.isAttribute()) {
            if (!m_selections.containsKey(prop.getName())) {
                m_selections.put(prop.getName(), new Selection(this, prop));
            }
        } else {
            Node child = (Node) m_children.get(first);
            if (child == null) {
                child = new PropertyNode(this, prop);
                m_children.put(first, child);
            }

            if (rest == null) {
                child.fetchDefault();
            } else {
                child.fetch(rest);
            }
        }
    }

    abstract String getName();

    abstract String getAlias();

    abstract String getPrefix();

    abstract Query getQuery();

    abstract boolean isOuter();

    void traverse(Actor actor) {
        actor.act(this);

        for (Iterator it = m_tables.values().iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            actor.act(table);
        }

        for (Iterator it = m_children.values().iterator(); it.hasNext(); ) {
            Node child = (Node) it.next();
            child.traverse(actor);
        }
    }

    void buildQuery() {
        Query query = getQuery();
        Set elements = getJoinElements();
        for (Iterator it = elements.iterator(); it.hasNext(); ) {
            JoinElement je = (JoinElement) it.next();

            Column from = defineTable(je.getFrom().getTableName()).
                         defineColumn(je.getFrom().getColumnName());
            Column to   = defineTable(je.getTo().getTableName()).
                         defineColumn(je.getTo().getColumnName());
            query.addCondition(new Condition(from, to));
        }

        for (Iterator it = getSelections().iterator(); it.hasNext(); ) {
            Selection sel = (Selection) it.next();
            Property prop = sel.getProperty();
            Table table = defineTable(prop.getColumn().getTableName());
            sel.setColumn(
                table.defineColumn(prop.getColumn().getColumnName())
                );
        }
    }

    Table defineTable(String name) {
        Table table = getTable(name);

        if (table == null) {
            table = new Table(this, name);
        }

        return table;
    }

    Set getJoinElements() {
        Set result = new HashSet();

        for (Iterator it = m_type.getJoinPaths(); it.hasNext(); ) {
            addJoinPath(result, (JoinPath) it.next());
        }

        addReferenceKeys(result, m_type);

        return result;
    }

    void addJoinPath(Set result, JoinPath jp) {
        for (Iterator it = jp.getJoinElements(); it.hasNext(); ) {
            JoinElement je = (JoinElement) it.next();
            result.add(je);
        }
    }

    private void addReferenceKeys(Set result, ObjectType type) {
        ObjectType st = type.getSupertype();
        if (st == null) {
            return;
        } else {
            com.arsdigita.persistence.metadata.Column from =
                type.getReferenceKey();
            com.arsdigita.persistence.metadata.Column to =
                st.getReferenceKey();
            if (to == null) {
                to = ((Property) st.getKeyProperties().next()).getColumn();
            }

            // This is allowed if the subclass doesn't have its own table.
            if (from != null) {
                result.add(new JoinElement(from, to));
            }

            addReferenceKeys(result, st);
        }
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("(node:\n  name=" + getName() +
                      " type=" + m_type.getQualifiedName());

        for (Iterator it = getSelections().iterator(); it.hasNext(); ) {
            Selection sel = (Selection) it.next();
            Property prop = sel.getProperty();
            result.append("\n  fetch " + prop.getName() +
                          "(" + prop.getColumn().getQualifiedName() + ")");
        }

        result.append("\n  tables=" + m_tables.values());

        result.append(")");
        return result.toString();
    }

}
