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

package com.arsdigita.persistence.oql;

import com.arsdigita.db.DbHelper;
import com.arsdigita.util.*;
import com.arsdigita.persistence.metadata.*;
import java.util.*;
import java.io.*;

import org.apache.log4j.Logger;

/**
 * The Query class is an optimizing query generator that uses the metadata
 * specified in a PDL file to generate sql queries.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #16 $ $Date: 2002/10/14 $
 **/

public class Query extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Query.java#16 $ by $Author: richardl $, $DateTime: 2002/10/14 17:27:59 $";

    private static final Logger s_log = Logger.getLogger(Query.class);

    private Set m_conditions = new HashSet();
    private Set m_selections = new HashSet();
    private StringBuffer m_dot = new StringBuffer();
    private List m_errors = new ArrayList();
    private List m_modifications = new ArrayList();

    /**
     * Constructs a query for retrieving all objects of the given type. By
     * default fetches only the key properties of the type (see {@link
     * #fetchKey()}). Additional properties can be fetched via the {@link
     * #fetchDefault()} and {@link #fetch(String)} methods.
     *
     * @param type The object type for the query to fetch.
     **/
    public Query(ObjectType type) {
        super(null, type);
        fetchKey();
    }

    /**
     * This method is used to add link attributes to this query.  This
     * is typically only used for Associations.
     */
    public void addLinkAttributes(Property parentProperty, ObjectType link) {
        if (link != null) {
            // The properties that are not key properties are the "link"
            // properties
            for (Iterator it = link.getProperties(); it.hasNext(); ) {
                Property property = (Property) it.next();
                if (!link.isKeyProperty(property)) {
                    if (property.isAttribute() &&
                        property.getColumn() == null) {
                        throw new NoMetadataException
                            (property.getFilename() + ": " +
                             property.getLineNumber() +
                             ": No metadata found for property " +
                             property.getName() +
                             " while generating SQL for query: " +
                             getQuery());
                    }
                    Node parent = getChildNode(parentProperty);
                    parent.addLinkSelection(parent, property);
                }
            }
        }
    }

    Set getSelections(Column column) {
        Set result = new HashSet();
        for (Iterator it = m_selections.iterator(); it.hasNext(); ) {
            Selection sel = (Selection) it.next();
            if (column.equals(sel.getColumn())) {
                result.add(sel);
            }
        }

        return result;
    }

    void addSelection(Selection selection) {
        m_selections.add(selection);
    }

    void removeSelection(Selection selection) {
        m_selections.remove(selection);
    }

    void addCondition(Condition condition) {
        m_conditions.add(condition);
    }

    void removeCondition(Condition condition) {
        m_conditions.remove(condition);
    }

    String getName() {
        return "this";
    }

    String getAlias() {
        return null;
    }

    String getPrefix() {
        return "";
    }

    Query getQuery() {
        return this;
    }

    boolean isOuter() {
        return false;
    }

    boolean isNullable() {
        return false;
    }

    private static final char[] CHARS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
        'E', 'F', 'G', 'H', 'I', 'J', 'G', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
        'Y', 'Z', '_'
    };

    private Map m_abbreviations = new HashMap();
    private Set m_taken = new HashSet();

    String abbreviate(String str) {
        String result = (String) m_abbreviations.get(str);

        if (result == null) {
            if (str.length() < 30) {
                result = str;
            } else {
                StringBuffer abbrev = new StringBuffer();

                char old = '\0';
                for (int i = 0; i < str.length(); i++) {
                    char c = str.charAt(i);

                    if (old == '_') {
                        abbrev.append(c);
                    } else {
                        switch (c) {
                        case 'a':
                        case 'A':
                        case 'e':
                        case 'E':
                        case 'i':
                        case 'I':
                        case 'o':
                        case 'O':
                        case 'u':
                        case 'U':
                            break;
                        default:
                            abbrev.append(c);
                            break;
                        }
                    }

                    old = c;
                }

                result = abbrev.toString();

                if (result.length() >= 30) {
                    int begin = result.length() - 29;
                    loop:
                    while (begin < result.length()) {
                        switch (result.charAt(begin)) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                        case '_':
                            begin++;
                            break;
                        default:
                            break loop;
                        }
                    }
                    result = result.substring(begin);
                }
            }

            if (m_taken.contains(result)) {
                for (int i = 0; i < CHARS.length; i++) {
                    if (!m_taken.contains(result + CHARS[i])) {
                        result = result + CHARS[i];
                        break;
                    }
                }
            }

            int count = 0;
            while (m_taken.contains(result)) {
                result = "a" + count++;
            }

            m_abbreviations.put(str, result);
            m_taken.add(result);
        }

        return result;
    }

    int numTables() {
        final int[] result = {0};

        traverse(new Actor() {
                public void act(Table table) {
                    result[0]++;
                }
            });

        return result[0];
    }

    private boolean m_modified = false;

    public boolean  isModified() {
        return m_modified;
    }

    void modify(String msg) {
        m_modifications.add(msg);
        m_modified = true;
        dot();
        new Validator().act(this);
        if (m_errors.size() > 0) {
            generateError();
        }
    }

    private void generateError() {
        try {
            File file = File.createTempFile("oql", ".dot");
            String path = file.getCanonicalPath();
            dumpDot(file);
            ObjectType type = getObjectType();
            throw new OQLException(
                                   type.getFilename() + ": " + type.getLineNumber() +
                                   " column " + type.getColumnNumber() +
                                   ": Encountered an error while generating a query for " +
                                   type.getQualifiedName() + ".\nThe following errors were " +
                                   "reported:\n  " + join(m_errors, "\n  ") +
                                   "\nAfter making the following modifications:\n  " +
                                   join(m_modifications, "\n  ") +
                                   "\nA dot representation of the query after each stage of " +
                                   " modification has been written to  the file '" + path +
                                   "'. If you think this message is the result of a bug then " +
                                   "please include the contents of the above file in any bug " +
                                   "report."
                                   );
        } catch (IOException e) {
            s_log.error(e);
        }
    }

    private void dot() {
        if (m_dot.length() > 0) {
            m_dot.append("\n\n");
        }

        toDot(m_dot);
    }

    public void dumpDot(File file) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(m_dot.toString());
            writer.close();
        } catch (IOException e) {
            s_log.error(e.getMessage());
        }
    }

    private static final Set s_written = new HashSet();

    public void generate() {
        traverse(BUILD_QUERY);

        modify("Genesis");
        while (isModified()) {
            m_modified = false;
            traverse(OPTIMIZE_QUERY);
        }

        /*
        // I use this code to dump every query in the system in dot form.
        // We want it commented out unless we're debugging stuff.
        String dot = toDot();
        String op = getOperation().toString();
        if (!s_written.contains(op)) {
        try {
        FileWriter writer = new FileWriter(
        "/tmp/dmp/" + s_written.size() + ".dot"
        );
        writer.write(dot);
        writer.close();
        s_written.add(op);
        } catch (IOException e) {
        s_log.error(e.getMessage());
        }
        }*/
    }

    public String toSQL() {
        generate();
        return getSQL();
    }

    /**
     * Writes out the select clause for this query. This is shared by both the
     * postgres and the oracle versions of getSQL().
     **/

    private void writeANSISelect(StringBuffer result) {
        result.append(" select ");

        final List selections = new ArrayList();

        traverse(new Actor() {
                public void act(Node node) {
                    for (Iterator it = node.getSelections().iterator();
                         it.hasNext(); ) {
                        Selection sel = (Selection) it.next();
                        selections.add(sel.getColumn().getQualifiedName() +
                                       " as " + sel.getAlias());
                    }
                }
            });

        Collections.sort(selections);
        result.append(join(selections, ",\n        "));
    }

    private void writeOracleFrom(StringBuffer result) {
        result.append("\n   from ");

        final List tables = new ArrayList();

        traverse(new Actor() {
                public void act(Table table) {
                    tables.add(table.getName() + " " + table.getAlias());
                }
            });

        Collections.sort(tables);
        result.append(join(tables, ",\n        "));
    }

    private void writePostgresFrom(StringBuffer result) {
        result.append("\n   from ");

        Stack tables = new Stack();

        for (Iterator it = getTables().iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            if (table.getEntering().size() == 0) {
                tables.add(table);
                result.append(table.getName() + " " + table.getAlias());
                break;
            }
        }

        while (tables.size() > 0) {
            Table table = (Table) tables.pop();
            for (Iterator it = table.getLeaving().iterator();
                 it.hasNext(); ) {
                Condition condition = (Condition) it.next();
                result.append("\n        ");
                if (condition.isOuter()) {
                    result.append("left ");
                }
                result.append("join ");
                Table join = condition.getHead().getTable();
                result.append(join.getName() + " " + join.getAlias());
                result.append(" on (");
                result.append(condition.getTail().getQualifiedName() + " = " +
                              condition.getHead().getQualifiedName());
                result.append(")");

                tables.push(condition.getHead().getTable());
            }
        }
    }

    private void writeOracleWhere(StringBuffer result) {
        if (m_conditions.size() > 0) {
            result.append("\n  where ");
        }

        final List conditions = new ArrayList();
        for (Iterator it = m_conditions.iterator(); it.hasNext(); ) {
            Condition condition = (Condition) it.next();
            conditions.add(condition.getTail().getQualifiedName() + " = " +
                           condition.getHead().getQualifiedName() +
                           (condition.isOuter() ? "(+)" : ""));
        }

        Collections.sort(conditions);
        result.append(join(conditions, "\n    and "));
    }

    private void writePostgresWhere(StringBuffer result) {
        // Do nothing, postgres doesn't need a where clause since all the join
        // conditions are specified as part of the from clause.
    }

    private void writeOracleSQL(StringBuffer result) {
        writeANSISelect(result);
        writeOracleFrom(result);
        writeOracleWhere(result);
    }

    private void writePostgresSQL(StringBuffer result) {
        writeANSISelect(result);
        writePostgresFrom(result);
        writePostgresWhere(result);
    }

    public String getSQL() {
        final StringBuffer result = new StringBuffer();
        int database = DbHelper.getDatabase();
        if (database == DbHelper.DB_POSTGRES) {
            writePostgresSQL(result);
        } else if (database == DbHelper.DB_ORACLE) {
            writeOracleSQL(result);
        } else {
            DbHelper.unsupportedDatabaseError("optimizing query generator");
        }

        return result.toString();
    }

    private static final String join(List list, String sep) {
        StringBuffer result = new StringBuffer();

        for (Iterator it = list.iterator(); it.hasNext(); ) {
            result.append(it.next());
            if (it.hasNext()) {
                result.append(sep);
            }
        }

        return result.toString();
    }

    private static final Actor BUILD_QUERY = new Actor() {
            public void act(Node node) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Building query:\n" + node);
                }
                node.buildQuery();
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Done building query:\n" + node);
                }
            }
        };

    private static final Actor OPTIMIZE_QUERY = new Optimizer();

    public List getAllMappings() {
        final List result = new ArrayList();

        traverse(new Actor() {
                public void act(Node node) {
                    for (Iterator it = node.getSelections().iterator();
                         it.hasNext(); ) {
                        Selection sel = (Selection) it.next();
                        result.add(sel.getMapping());
                    }
                }
            });

        return result;
    }

    public Operation getOperation() {
        Operation result = new Operation(getSQL() + "\n");

        for (Iterator it = getAllMappings().iterator(); it.hasNext(); ) {
            result.addMapping((Mapping) it.next());
        }

        return result;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(getObjectType().getQualifiedName() + "(");

        for (Iterator it = m_selections.iterator(); it.hasNext(); ) {
            result.append(it.next());
            if (it.hasNext()) {
                result.append(", ");
            }
        }

        result.append(")");

        return result.toString();
    }

    public String toDot() {
        StringBuffer result = new StringBuffer();
        toDot(result);
        return result.toString();
    }

    private void toDot(StringBuffer result) {
        result.append("digraph " + getName() + " {\n");
        result.append("    size=\"11,17\";\n");
        result.append("    center=1;\n");
        result.append("    rankdir=LR;\n");
        result.append("    node [shape=record];\n\n");

        Map env = new HashMap();

        toDot(env, result);

        for (Iterator it = m_conditions.iterator(); it.hasNext(); ) {
            Condition cond = (Condition) it.next();
            result.append(
                          "    " + env.get(cond.getTail().getTable()) + ":" +
                          env.get(cond.getTail()) +
                          " -> " +
                          env.get(cond.getHead().getTable()) + ":" +
                          env.get(cond.getHead())
                          );
            if (cond.isOuter()) { result.append(" [color=blue]"); }
            result.append(";\n");
        }

        result.append("}");
    }

    void error(String message) {
        m_errors.add(message);
    }

}
