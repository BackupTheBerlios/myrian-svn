package com.arsdigita.persistence.oql;

import com.arsdigita.util.*;
import com.arsdigita.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Category;

/**
 * The Query class is an optimizing query generator that uses the metadata
 * specified in a PDL file to generate sql queries.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2002/05/30 $
 **/

public class Query extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Query.java#4 $ by $Author: rhs $, $DateTime: 2002/05/30 15:15:09 $";

    private static final Category s_log = Category.getInstance(Query.class);

    private Set m_conditions = new HashSet();

    /**
     * Constructs a query for retrieving all objects of the given type. By
     * default fetches only the key properties of the type (see {@link
     * #fetchKey()}). Additional properties can be fetched via the {@link
     * #fetchDefault()} and {@link #fetch()} methods.
     *
     * @param type The object type for the query to fetch.
     **/

    public Query(ObjectType type) {
        super(null, type);
    }

    void addCondition(Condition condition) {
        m_conditions.add(condition);
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

    public void generate() {
        traverse(BUILD_QUERY);
        traverse(CHECK_QUERY);

        if (m_errors.size() > 0) {
            ObjectType type = getObjectType();
            throw new Error(
                type.getFilename() + ": " + type.getLineNumber() +
                " column " + type.getColumnNumber() +
                " errors generating query: " + m_errors + "\n\n" + toDot()
                );
        }

        // Phase one, identify all the tables from which we must do direct
        // selects.
        // Phase two, compute the long join paths for those tables.
        // Phase three, collapse the long join paths.
        // Phase four, add any missing tables required for joins.
        // Phase five, remove any redundent tables.
    }

    public String toSQL() {
        generate();
        return getSQL();
    }

    public String getSQL() {
        final StringBuffer result = new StringBuffer();
        result.append(" select ");

        final boolean[] first = { true };

        traverse(new Actor() {
                public void act(Node node) {
                    for (Iterator it = node.getSelections().iterator();
                         it.hasNext(); ) {
                        Selection sel = (Selection) it.next();

                        if (first[0]) {
                            first[0] = false;
                        } else {
                            result.append(",\n        ");
                        }

                        result.append(sel.getColumn().getQualifiedName());
                        result.append(" as ");
                        result.append(sel.getAlias());
                    }
                }
            });

        result.append("\n   from ");

        first[0] = true;

        traverse(new Actor() {
                public void act(Table table) {
                    if (first[0]) {
                        first[0] = false;
                    } else {
                        result.append(",\n        ");
                    }

                    result.append(table.getName() + " " + table.getAlias());
                }
            });

        if (m_conditions.size() > 0) {
            result.append("\n  where ");
        }

        first[0] = true;

        for (Iterator it = m_conditions.iterator(); it.hasNext(); ) {
            Condition condition = (Condition) it.next();

            if (first[0]) {
                first[0] = false;
            } else {
                result.append("\n    and ");
            }

            result.append(condition.getLeft().getQualifiedName() + " = " +
                          condition.getRight().getQualifiedName());

            if (condition.isOuter()) {
                result.append("(+)");
            }
        }

        return result.toString();
    }

    private static final Actor BUILD_QUERY = new Actor() {
            public void act(Node node) {
                s_log.debug("Building query:\n" + node);
                node.buildQuery();
                s_log.debug("Done building query:\n" + node);
            }
        };

    private static final Actor CHECK_QUERY = new Actor() {
            public void act(Table table) {
                if (table.getConditions().size() == 0 &&
                    table.getQuery().numTables() > 1) {
                    s_log.error(
                        "Unconstrained table in query for "
                        + table.getQuery().getObjectType().getQualifiedName()
                        + ": " + table);
                }
            }
        };

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

    public String toString() {
        return super.toString() + "\n(conditions: " + m_conditions + ")";
    }

    public String toDot() {
        final StringBuffer result = new StringBuffer();
        result.append("digraph " + getName() + " {\n");
        result.append("    size=\"11,17\";\n");
        result.append("    center=1;\n\n");

        Map env = new HashMap();

        toDot(env, result);

        for (Iterator it = m_conditions.iterator(); it.hasNext(); ) {
            Condition cond = (Condition) it.next();
            result.append("    " + env.get(cond.getLeft()) + " -> " +
                          env.get(cond.getRight()));
            if (cond.isOuter()) { result.append(" [color=blue]"); }
            result.append(";\n");
        }

        result.append("}");

        return result.toString();
    }

    private List m_errors = new ArrayList();

    void error(String message) {
        m_errors.add(message);
    }

}
