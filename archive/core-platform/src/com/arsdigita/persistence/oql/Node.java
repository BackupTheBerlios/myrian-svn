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
 * @version $Revision: #5 $ $Date: 2002/07/10 $
 **/

abstract class Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Node.java#5 $ by $Author: rhs $, $DateTime: 2002/07/10 16:04:39 $";

    private static final Category s_log = Category.getInstance(Node.class);

    private Node m_parent;
    private ObjectType m_type;

    private Map m_children = new HashMap();
    private Map m_selections = new HashMap();
    private Map m_tables = new HashMap();
    private Set m_conditions = new HashSet();

    public Node(Node parent, ObjectType type) {
        m_parent = parent;
        m_type = type;
    }

    public final int depth() {
        if (m_parent == null) {
            return 0;
        } else {
            return m_parent.depth() + 1;
        }
    }

    public Node getParent() {
        return m_parent;
    }

    public Collection getChildren() {
        return m_children.values();
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

    void removeTable(Table table) {
        m_tables.remove(table.getName());
    }

    void addCondition(Condition condition) {
        m_conditions.add(condition);
    }

    void removeCondition(Condition condition) {
        m_conditions.remove(condition);
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

    abstract boolean isNullable();

    abstract void error(String message);

    void traverse(Actor actor) {
        actor.act(this);

        ArrayList tables = new ArrayList(m_tables.size());
        tables.addAll(m_tables.values());

        for (Iterator it = tables.iterator(); it.hasNext(); ) {
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
                defineColumn(je.getFrom());
            Column to   = defineTable(je.getTo().getTableName()).
                defineColumn(je.getTo());

            if (to.equals(from)) {
                Set sources = from.getSources();
                StringBuffer msg = new StringBuffer(
                    "Duplicate columns: "
                    );
                for (Iterator iter = sources.iterator(); iter.hasNext(); ) {
                    com.arsdigita.persistence.metadata.Column col =
                        (com.arsdigita.persistence.metadata.Column) iter.next();
                    msg.append(col.getFilename() + ": " +
                               col.getLineNumber() + " column " +
                               col.getColumnNumber());

                    if (iter.hasNext()) { msg.append(", "); }
                }
                throw new OQLException(msg.toString());
            }
        }

        Set connected = new HashSet();
        Table start = null;
        for (Iterator it = getTables().iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            if (table.getEntering().size() > 0) {
                if (start == null) {
                    start = table;
                }
                connected.add(table);
            }
        }

        if (start == null && getTables().size() > 0) {
            start = (Table) getTables().iterator().next();
            connected.add(start);
        }

        int before;

        do {
            before = connected.size();

            for (Iterator it = elements.iterator(); it.hasNext(); ) {
                JoinElement je = (JoinElement) it.next();

                Column from = getFrom(je);
                Column to = getTo(je);

                if (connected.contains(from.getTable()) &&
                    !connected.contains(to.getTable())) {
                    new Condition(this, from, to);
                    connected.add(to.getTable());
                }
            }

            for (Iterator it = elements.iterator(); it.hasNext(); ) {
                JoinElement je = (JoinElement) it.next();

                Column from = getTo(je);
                Column to = getFrom(je);

                if (connected.contains(from.getTable()) &&
                    !connected.contains(to.getTable())) {
                    new Condition(this, from, to);
                    connected.add(to.getTable());
                }
            }
        } while (connected.size() > before);

        if (connected.size() < getTables().size()) {
            StringBuffer msg = new StringBuffer();
            msg.append("Could not form a properly constrained join:\n" +
                       "Tables being joined: ");
            for (Iterator it = getTables().iterator(); it.hasNext(); ) {
                Table table = (Table) it.next();
                msg.append(table.getName());
                if (it.hasNext()) {
                    msg.append(", ");
                }
            }

            msg.append("\nConstraints used: ");

            for (Iterator it = m_conditions.iterator(); it.hasNext(); ) {
                Condition cond = (Condition) it.next();
                msg.append(
                    cond.getTail().getFullName() + " = " +
                    cond.getHead().getFullName()
                    );
            }

            error(msg.toString());
        }

        for (Iterator it = getSelections().iterator(); it.hasNext(); ) {
            Selection sel = (Selection) it.next();
            Property prop = sel.getProperty();
            Table table = defineTable(prop.getColumn().getTableName());
            sel.setColumn(table.defineColumn(prop.getColumn()));
        }
    }

    private final Column getFrom(JoinElement je) {
        return getTable(je.getFrom().getTableName()).getColumn(
            je.getFrom().getColumnName()
            );
    }

    private final Column getTo(JoinElement je) {
        return getTable(je.getTo().getTableName()).getColumn(
            je.getTo().getColumnName()
            );
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

    void toDot(Map env, StringBuffer result) {
        env.put(this, "cluster_node" + env.size());
        result.append("    subgraph " + env.get(this) + " {\n");

        for (Iterator outer = getTables().iterator();
             outer.hasNext(); ) {
            Table table = (Table) outer.next();

            env.put(table, "table" + env.size());
            result.append("        " + env.get(table) + " [ label=\"");

            for (Iterator it = table.getColumns().iterator();
                 it.hasNext(); ) {
                Column col = (Column) it.next();
                env.put(col, "column" + env.size());
                result.append("<" + env.get(col) + ">" +
                              col.getQualifiedName());
                Set sels = getQuery().getSelections(col);
                for (Iterator inner = sels.iterator(); inner.hasNext(); ) {
                    Selection sel = (Selection) inner.next();
                    result.append("\\n(" + sel.getName() + ")");
                }

                if (it.hasNext()) {
                    result.append("|");
                }
            }

            result.append("\"];\n");
        }

        for (Iterator it = getChildren().iterator(); it.hasNext(); ) {
            Node child = (Node) it.next();
            child.toDot(env, result);
        }

        result.append("    }\n");
    }

}
