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
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Node
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/01/30 $
 **/

abstract class Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Node.java#2 $ by $Author: rhs $, $DateTime: 2003/01/30 17:57:25 $";

    private static final Logger s_log = Logger.getLogger(Node.class);

    private Node m_parent;
    private ObjectMap m_map;

    private Map m_children = new HashMap();
    private Map m_selections = new HashMap();
    private Map m_tables = new HashMap();
    private Set m_conditions = new HashSet();

    public Node(Node parent, ObjectMap map) {
        m_parent = parent;
        m_map = map;
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

    /**
     *  This returns the child node that corresponds to the passed in property
     *  This will return null if no such node exists.
     *  This facilitates the existence of LinkAttributes
     */
    Node getChildNode(Property property) {
        return (Node)m_children.get(property.getName());
    }

    public ObjectMap getObjectMap() {
        return m_map;
    }

    public ObjectType getObjectType() {
        return m_map.getObjectType();
    }

    Collection getSelections() {
        return m_selections.values();
    }

    /**
     *  This returns the selection corresponding to the given
     *  node/property pair.
     *  It returns null if no such selection exists.
     */
    /*
      Selection getSelection(Node node, Property property) {
      // right now, we ignore the node parameter but we have it in
      // the UI to reserve the right to use it later
      return (Selection)m_selections.get(property.getName())
      }
    */

    void addSelection(Node node, Property property) {
        if (m_selections.get(property.getName()) == null) {
            m_selections.put(property.getName(),
                             new Selection(node, property));
        }
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
        for (Iterator it = m_map.getKeyProperties().iterator();
             it.hasNext(); ) {
            Property prop = (Property) it.next();
            fetch(prop.getName());
        }
    }


    /**
     * Instructs this Query to fetch all the default properties for this
     * object type.
     **/

    public void fetchDefault() {
        for (Iterator it = m_map.getFetchedPaths().iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            fetch(p.getPath());
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
        final String first, rest;
        int index = path.indexOf('.');
        if (index < 0) {
            first = path;
            rest = null;
        } else {
            first = path.substring(0, index);
            rest = path.substring(index+1);
        }

        final Property prop = getObjectType().getProperty(first);
        Mapping mapping = m_map.getMapping(Path.get(first));
        if (mapping == null) {
            return;
        }

        if (prop == null) {
            throw new IllegalArgumentException("No such property: " + first);
        }

        mapping.dispatch(new Mapping.Switch() {
                public void onValue(ValueMapping vm) {
                    addSelection(Node.this, prop);
                }

                public void onReference(ReferenceMapping rm) {
                    Node child = (Node) m_children.get(first);
                    if (child == null) {
                        child = new PropertyNode(Node.this, prop);
                        m_children.put(first, child);
                    }

                    if (rest == null) {
                        child.fetchDefault();
                    } else {
                        child.fetch(rest);
                    }
                }
            });
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
        Set elements = new HashSet();
        elements.addAll(m_map.getAllJoins());

        for (Iterator it = elements.iterator(); it.hasNext(); ) {
            Join je = (Join) it.next();

            Column from = defineTable(je.getFrom().getTableName()).
                defineColumn(je.getFrom());
            Column to   = defineTable(je.getTo().getTableName()).
                defineColumn(je.getTo());

            if (to.equals(from)) {
                Set sources = from.getSources();
                StringBuffer msg = new StringBuffer
                    ("Duplicate columns: ");
                for (Iterator iter = sources.iterator(); iter.hasNext(); ) {
                    com.arsdigita.persistence.proto.metadata.Column col =
                        (com.arsdigita.persistence.proto.metadata.Column) iter.next();
                    /*msg.append(col.getFilename() + ": " +
                               col.getLineNumber() + " column " +
                               col.getColumnNumber());*/
                    msg.append(col);

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
                Join je = (Join) it.next();

                Column from = getFrom(je);
                Column to = getTo(je);

                if (connected.contains(from.getTable()) &&
                    !connected.contains(to.getTable())) {
                    new Condition(this, from, to);
                    connected.add(to.getTable());
                }
            }

            for (Iterator it = elements.iterator(); it.hasNext(); ) {
                Join je = (Join) it.next();

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
            ValueMapping vm =
                (ValueMapping) m_map.getMapping(Path.get(prop.getName()));
            Table table = defineTable(vm.getColumn().getTableName());
            sel.setColumn(table.defineColumn(vm.getColumn()));
        }
    }

    private final Column getFrom(Join je) {
        return getTable(je.getFrom().getTableName()).getColumn
            (je.getFrom().getColumnName());
    }

    private final Column getTo(Join je) {
        return getTable(je.getTo().getTableName()).getColumn
            (je.getTo().getColumnName());
    }

    Table defineTable(String name) {
        Table table = getTable(name);

        if (table == null) {
            table = new Table(this, name);
        }

        return table;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("(node:\n  name=" + getName() +
                      " type=" + getObjectType().getQualifiedName());

        for (Iterator it = getSelections().iterator(); it.hasNext(); ) {
            Selection sel = (Selection) it.next();
            Property prop = sel.getProperty();
            result.append("\n  fetch " + prop.getName());
            ValueMapping vm =
                (ValueMapping) m_map.getMapping(Path.get(prop.getName()));
            result.append("(" + vm.getColumn().getQualifiedName() + ")");
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
