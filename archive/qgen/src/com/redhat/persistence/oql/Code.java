package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.metadata.Static;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Code
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #15 $ $Date: 2004/02/28 $
 **/

class Code {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Code.java#15 $ by $Author: rhs $, $DateTime: 2004/02/28 08:30:26 $";

    private static final Logger s_log = Logger.getLogger(Code.class);

    static final String TRUE = "1 = 1";
    static final String FALSE = "1 = 0";
    static final String NULL = "null";

    static String join(Collection objs, String sep) {
        StringBuffer result = new StringBuffer();
        for (Iterator it = objs.iterator(); it.hasNext(); ) {
            result.append(it.next());
            if (it.hasNext()) {
                result.append(sep);
            }
        }
        return result.toString();
    }

    static String[] names(Column[] columns, String alias) {
        String[] result = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            Column col = columns[i];
            if (alias == null) {
                result[i] = col.getName();
            } else {
                result[i] = alias + "." + col.getName();
            }
        }
        return result;
    }

    static Mapping getMapping(Property prop) {
        Root root = prop.getRoot();
        if (root == null) {
            throw new IllegalStateException("null root: " + prop);
        }
        ObjectMap om = root.getObjectMap(prop.getContainer());
        if (om == null) {
            throw new IllegalStateException("null object map: " + prop);
        }
        Mapping m = om.getMapping(Path.get(prop.getName()));
        if (m == null) {
            throw new IllegalStateException("null mapping: " + prop);
        }
        return m;
    }

    static String[] columns(final Property prop, final String alias) {
        Mapping m = getMapping(prop);

        if (m.getRetrieve() != null) {
            return columns(prop.getType(), alias);
        }

        final String[][] result = new String[][] { null };

        m.dispatch(new Mapping.Switch() {
            public void onValue(Value v) {
                result[0] = names(new Column[] {v.getColumn()}, alias);
            }
            public void onJoinTo(JoinTo j) {
                result[0] = columns(j.getKey(), alias);
            }
            public void onJoinFrom(JoinFrom j) {
                result[0] =
                    columns(j.getKey().getTable().getPrimaryKey(), alias);
            }
            public void onJoinThrough(JoinThrough jt) {
                result[0] = columns(jt.getTo(), alias);
            }
            public void onStatic(Static s) {
                ObjectMap map = s.getObjectMap();
                SQLBlock block = map.getRetrieveAll();
                result[0] = columns
                    (paths(prop.getType(), s.getPath()), block);
            }
        });

        return result[0];
    }

    static String[] columns(Constraint c, String alias) {
        return names(c.getColumns(), alias);
    }

    static void equals(String[] left, String[] right, StringBuffer buf) {
        if (left.length != right.length) {
            throw new IllegalArgumentException
                ("left size does not match right size" +
                 "\nleft = " + Arrays.asList(left) +
                 "\nright= " + Arrays.asList(right));
        }
        for (int i = 0; i < left.length; i++) {
            buf.append(left[i]);
            buf.append(" = ");
            buf.append(right[i]);
            if (i < left.length - 1) {
                buf.append(" and ");
            }
        }
    }

    static String table(final Property prop) {
        Mapping m = getMapping(prop);

        if (m.getRetrieve() != null) {
            return table(prop.getType());
        }

        final String[] result = new String[] { null };

        m.dispatch(new Mapping.Switch() {
            public void onValue(Value v) {
                result[0] = v.getTable().getName();
            }
            public void onJoinTo(JoinTo j) {
                result[0] = j.getTable().getName();
            }
            public void onJoinFrom(JoinFrom j) {
                result[0] = j.getKey().getTable().getName();
            }
            public void onJoinThrough(JoinThrough jt) {
                result[0] = jt.getFrom().getTable().getName();
            }
            public void onStatic(Static s) {}
        });

        return result[0];
    }

    static String table(ObjectType type) {
        ObjectMap om = type.getRoot().getObjectMap(type);
        if (om.getRetrieveAll() != null) {
            return "(" + om.getRetrieveAll().getSQL() + ")";
        } else {
            return table(om).getName();
        }
    }

    static Table table(ObjectMap map) {
        Table table = null;
        for (ObjectMap om = map; om != null; om = om.getSuperMap()) {
            table = om.getTable();
            if (table != null) { break; }
        }
        if (table == null) {
            throw new IllegalStateException
                ("type does not have table: " + map.getObjectType());
        }
        return table;
    }

    static void bind(SQL sql, Map values, StringBuffer buf) {
        for (SQLToken t = sql.getFirst(); t != null; t = t.getNext()) {
            if (t.isBind()) {
                Path key = Path.get(t.getImage().substring(1));
                String value = (String) values.get(key);
                if (value == null) {
                    throw new IllegalStateException
                        ("no value for: " + key + " in " + values);
                }
                buf.append(value);
            } else {
                buf.append(t.getImage());
            }
        }
    }

    static Path[] paths(ObjectType type, Path parent) {
        ArrayList result = new ArrayList();
        paths(type, parent, result);
        return (Path[]) result.toArray(new Path[result.size()]);
    }

    static void paths(ObjectType type, Path parent, Collection result) {
        Collection props = properties(type);
        if (props.isEmpty()) {
            if (parent != null) {
                result.add(parent);
            }
            return;
        }
        for (Iterator it = props.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            paths(prop.getType(), Path.add(parent, prop.getName()), result);
        }
    }

    static Map map(Path[] keys, String[] values) {
        Map result = new HashMap();
        for (int i = 0; i < keys.length; i++) {
            result.put(keys[i], values[i]);
        }
        return result;
    }

    static String[] columns(Path[] paths, SQLBlock block) {
        String[] result = new String[paths.length];
        for (int i = 0; i < result.length; i++) {
            Path column = block.getMapping(paths[i]);
            if (column == null) {
                throw new IllegalStateException
                    ("no mapping for path: " + paths[i]);
            }
            result[i] = column.getPath();
        }
        return result;
    }

    static String[] columns(ObjectType type, String alias) {
        ObjectMap om = type.getRoot().getObjectMap(type);
        if (om.getRetrieveAll() != null) {
            String[] columns = columns(paths(type, null), om.getRetrieveAll());
            if (alias == null) {
                return columns;
            } else {
                return concat(alias + ".", columns);
            }
        } else {
            return names(table(om).getPrimaryKey().getColumns(), alias);
        }
    }

    static String[] concat(String prefix, String[] strs) {
        String[] result = new String[strs.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = prefix + strs[i];
        }
        return result;
    }

    static int span(ObjectType type) {
        Collection props = properties(type);
        if (props.isEmpty()) {
            return 1;
        } else {
            int result = 0;
            for (Iterator it = props.iterator(); it.hasNext(); ) {
                Property prop = (Property) it.next();
                result += span(prop.getType());
            }
            return result;
        }
    }

    static Collection properties(ObjectType type) {
        Collection props = type.getKeyProperties();
        if (props.isEmpty()) {
            return type.getImmediateProperties();
        } else {
            return props;
        }
    }

}
