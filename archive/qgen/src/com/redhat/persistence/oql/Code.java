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
 * @version $Revision: #20 $ $Date: 2004/03/18 $
 **/

public class Code {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Code.java#20 $ by $Author: rhs $, $DateTime: 2004/03/18 17:18:33 $";

    private static final Logger s_log = Logger.getLogger(Code.class);

    public static class Binding {

        private Object m_value;
        private int m_type;

        Binding(Object value, int type) {
            m_value = value;
            m_type = type;
        }

        public Object getValue() {
            return m_value;
        }

        public int getType() {
            return m_type;
        }

        public int hashCode() {
            return m_value.hashCode();
        }

        public boolean equals(Object o) {
            if (o instanceof Binding) {
                Binding b = (Binding) o;
                return m_value.equals(b.m_value);
            } else {
                return super.equals(o);
            }
        }

        public String toString() {
            return "(" + m_value.toString() + ": " + m_value.getClass() +
                ", " + Column.getTypeName(m_type) + ")";
        }

    }

    static final Code TRUE = new Code("1 = 1");
    static final Code FALSE = new Code("1 = 0");
    static final Code NULL = new Code("null");
    static final Code EMPTY = new Code();

    private StringBuffer m_sql;
    private int m_lower;
    private int m_upper;
    private List m_bindings;

    private Code(StringBuffer sql, List bindings) {
        m_sql = sql;
        m_bindings = bindings;
    }

    Code(String sql, List bindings) {
        m_sql = new StringBuffer(sql);
        m_lower = 0;
        m_upper = m_sql.length();
        m_bindings = bindings;
    }

    Code(String sql) {
        this(sql, Collections.EMPTY_LIST);
    }

    Code() {
        this("");
    }

    public String getSQL() {
        return m_sql.substring(m_lower, m_upper);
    }

    public List getBindings() {
        return m_bindings;
    }

    boolean isTrue() {
        return equals(TRUE);
    }

    boolean isFalse() {
        return equals(FALSE);
    }

    boolean isNull() {
        return equals(NULL);
    }

    boolean isEmpty() {
        return equals(EMPTY);
    }

    Code add(String sql) {
        Code result = new Code(m_sql, m_bindings);
        result.m_lower = m_lower;
        m_sql.append(sql);
        result.m_upper = m_sql.length();
        return result;
    }

    Code add(Code code) {
        List bindings;
        if (m_bindings.isEmpty()) {
            bindings = code.m_bindings;
        } else if (code.m_bindings.isEmpty()) {
            bindings = m_bindings;
        } else {
            bindings = new ArrayList();
            bindings.addAll(m_bindings);
            bindings.addAll(code.m_bindings);
        }
        Code result = new Code(m_sql, bindings);
        result.m_lower = m_lower;
        m_sql.append(code.getSQL());
        result.m_upper = m_sql.length();
        return result;
    }

    public int hashCode() {
        return getSQL().hashCode() ^ getBindings().hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof Code) {
            Code c = (Code) o;
            return getSQL().equals(c.getSQL())
                && getBindings().equals(c.getBindings());
        } else {
            return super.equals(o);
        }
    }

    public String toString() {
        return "<" + getSQL() + ": " + getBindings() + ">";
    }

    static Code join(Collection parts, String sep) {
        Code result = null;
        for (Iterator it = parts.iterator(); it.hasNext(); ) {
            Code part = (Code) it.next();
            if (result == null) {
                result = part;
            } else {
                result = result.add(sep).add(part);
            }
        }
        return result;
    }

    /**
     * Random static utility methods used by other oql classes.
     **/

    private static String[] names(Column[] columns, String alias) {
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

    private static boolean isStaticAttribute(Mapping m) {
        if (m instanceof Static) { return true; }
        ObjectMap map = m.getObjectMap();
        if (map.getTable() == null && map.getRetrieveAll() != null) {
            return true;
        }

        return false;
    }

    static String[] columns(final Property prop, final String alias) {
        Mapping m = getMapping(prop);

        if (m.getRetrieve() != null) {
            return columns(prop.getType(), alias);
        }

        if (isStaticAttribute(m)) {
            ObjectMap map = m.getObjectMap();
            SQLBlock block = map.getRetrieveAll();
            if (block == null && m instanceof Static) {
                throw new MetadataException
                    (map.getRoot(), map,
                     "Specify metadata for property " + prop.getName()
                     + " in type " + prop.getContainer().getQualifiedName()
                     + " or include a retrieve all for the type");
            }
            return columns(paths(prop.getType(), m.getPath()), block);
        }

        final String[][] result = new String[][] { null };

        m.dispatch(new Mapping.Switch() {
            public void onValue(Value v) {
                result[0] = names(new Column[] {v.getColumn()}, alias);
            }
            public void onJoinTo(JoinTo j) {
                result[0] = columns(j.getKey(), prop.getType(), alias);
            }
            public void onJoinFrom(JoinFrom j) {
                result[0] = columns
                    (j.getKey().getTable().getPrimaryKey(),
                     prop.getType(),
                     alias);
            }
            public void onJoinThrough(JoinThrough jt) {
                result[0] = columns(jt.getTo(), prop.getType(), alias);
            }
            public void onStatic(Static s) {
                throw new IllegalStateException();
            }
        });

        return result[0];
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

        if (isStaticAttribute(m)) {
            return null;
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
            public void onStatic(Static s) {
                throw new IllegalStateException();
            }
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

    /**
     * @pre map.getRetrieveAll() == null
     */
    static Table table(ObjectMap map) {
        for (ObjectMap om = map; om != null; om = om.getSuperMap()) {
            Table table = om.getTable();
            if (table != null) { return table; }
        }

        throw new MetadataException
            (map.getRoot(), map,
             "No retrieve all or mapping metadata for type "
             + map.getObjectType().getQualifiedName());
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
            return names(cols(type), alias);
        }
    }

    static String[] columns(Constraint c, ObjectType type, String alias) {
        return names(cols(c, type), alias);
    }

    private static Column[] cols(Constraint c, ObjectType type) {
        Column[] cols = c.getColumns();
        Root root = type.getRoot();
        ObjectType basetype = type.getBasetype();
        ObjectMap map = root.getObjectMap(basetype);
        Column[] currentOrder = table(map).getPrimaryKey().getColumns();
        Column[] desiredOrder = cols(basetype);
        return sort(cols, currentOrder, desiredOrder);
    }

    private static Column[] cols(ObjectType type) {
        Root root = type.getRoot();
        ObjectMap map = root.getObjectMap(type);
        if (type.getSupertype() == null) {
            Collection props = map.getKeyProperties();
            final ArrayList result = new ArrayList();

            for (Iterator it = props.iterator(); it.hasNext(); ) {
                final Property prop = (Property) it.next();
                Mapping mapping = map.getMapping(Path.get(prop.getName()));
                mapping.dispatch(new Mapping.Switch() {
                    public void onValue(Value m) {
                        result.add(m.getColumn());
                    }
                    public void onJoinTo(JoinTo m) {
                        result.addAll
                            (Arrays.asList(cols(m.getKey(), prop.getType())));
                    }
                    public void onJoinFrom(JoinFrom m) {
                        throw new UnsupportedOperationException();
                    }
                    public void onJoinThrough(JoinThrough m) {
                        throw new UnsupportedOperationException();
                    }
                    public void onStatic(Static m) {
                        throw new UnsupportedOperationException();
                    }
                });
            }

            return (Column[]) result.toArray(new Column[] {});
        } else {
            return cols(table(map).getPrimaryKey(), type);
        }
    }

    private static Column[] sort(Column[] toSort,
                                 Column[] currentOrder,
                                 Column[] desiredOrder) {
        final int length = toSort.length;
        if (length != currentOrder.length || length != desiredOrder.length) {
            throw new IllegalArgumentException("length of arrays differs");
        }

        Column[] result = new Column[length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (desiredOrder[i].equals(currentOrder[j])) {
                    result[i] = toSort[j];
                    break;
                }
            }
            if (result[i] == null) {
                throw new IllegalStateException();
            }
        }

        return result;
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
