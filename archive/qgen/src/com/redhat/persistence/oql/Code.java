package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.metadata.Static;

import java.io.*;
import java.util.*;

/**
 * Code
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/02/13 $
 **/

class Code {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Code.java#8 $ by $Author: ashah $, $DateTime: 2004/02/13 21:49:42 $";

    private Root m_root;
    private LinkedList m_stack = new LinkedList();
    private Map m_frames = new HashMap();
    private Map m_contexts = new HashMap();

    private StringBuffer m_sql = new StringBuffer();
    private Map m_counters = new HashMap();

    Code(Root root) {
        m_root = root;
    }

    ObjectType getType(String type) {
        return m_root.getObjectType(type);
    }

    ObjectType getType(Object value) {
        Adapter ad = m_root.getAdapter(value.getClass());
        return ad.getObjectType(value);
    }

    Frame frame(ObjectType type) {
        return new Frame(type);
    }

    void push(Frame frame) {
        m_stack.addFirst(frame);
    }

    Frame pop() {
        return (Frame) m_stack.removeFirst();
    }

    Frame get(String name) {
        for (Iterator it = m_stack.iterator(); it.hasNext(); ) {
            Frame frame = (Frame) it.next();
            if (frame.type.hasProperty(name)) {
                return frame;
            }
        }
        return null;
    }

    void setFrame(Expression expr, Frame frame) {
        m_frames.put(expr, frame);
    }

    Frame getFrame(Expression expr) {
        return (Frame) m_frames.get(expr);
    }

    void setContext(Variable var, Frame frame) {
        m_contexts.put(var, frame);
    }

    Frame getContext(Variable var) {
        return (Frame) m_contexts.get(var);
    }

    String getSQL() {
        return m_sql.toString();
    }

    void append(String sql) {
        m_sql.append(sql);
    }

    String var(String stem) {
        Integer count = (Integer) m_counters.get(stem);
        if (count == null) {
            count = new Integer(0);
        }
        String result = stem + count;
        count = new Integer(count.intValue() + 1);
        m_counters.put(stem, count);
        return result;
    }

    class Frame {

        ObjectType type;
        private String[] m_columns = null;

        private Frame(ObjectType type) {
            this.type = type;
        }

        void setColumns(String[] columns) {
            for (int i = 0; i < columns.length; i++) {
                if (columns[i] == null) {
                    throw new IllegalArgumentException
                        ("null column: " + Arrays.asList(columns));
                }
            }
            m_columns = columns;
        }

        String[] getColumns(Property prop) {
            Collection props = properties(prop.getContainer());
            int offset = 0;
            for (Iterator it = props.iterator(); it.hasNext(); ) {
                Property p = (Property) it.next();
                if (p.equals(prop)) { break; }
                offset += span(p.getType());
            }
            if (offset == m_columns.length) { return null; }
            String[] result = new String[span(prop.getType())];
            System.arraycopy(m_columns, offset, result, 0, result.length);
            return result;
        }

        String[] getColumns(String prop) {
            return getColumns(type.getProperty(prop));
        }

        String[] getColumns() {
            return m_columns;
        }

        String alias(Property prop) {
            String alias = var("t");
            setColumns(columns(prop, alias));
            return alias;
        }

        String alias(ObjectType type) {
            String alias = var("t");
            setColumns(columns(type, alias));
            return alias;
        }

        String alias(int ncolumns) {
            String alias = var("t");
            String[] columns = new String[ncolumns];
            for (int i = 0; i < columns.length; i++) {
                columns[i] = alias + ".col" + i;
            }
            setColumns(columns);
            return alias;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("<frame ");
            buf.append(type);
            buf.append(" ");
            if (m_columns == null) {
                buf.append("null");
            } else {
                for (int i = 0; i < m_columns.length; i++) {
                    buf.append(m_columns[i]);
                    if (i < m_columns.length - 1) {
                        buf.append(" ");
                    }
                }
            }
            buf.append(">");
            return buf.toString();
        }

    }

    static String[] names(Column[] columns, String alias) {
        String[] result = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            Column col = columns[i];
            result[i] = alias + "." + col.getName();
        }
        return result;
    }

    String getTrace() {
        StringBuffer buf = new StringBuffer();
        for (Iterator it = m_stack.iterator(); it.hasNext(); ) {
            buf.append(it.next());
            buf.append("\n");
        }
        return buf.toString();
    }

    Mapping getMapping(Property prop) {
        Root root = prop.getRoot();
        if (root == null) {
            throw new IllegalStateException
                ("null root: " + prop + "\n" + getTrace());
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

    void alias(String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            append(columns[i]);
            append(" as col" + i);
            if (i < columns.length - 1) {
                append(", ");
            }
        }
    }

    String[] columns(Property prop, final String alias) {
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
            public void onStatic(Static s) {}
        });

        return result[0];
    }

    static String[] columns(Constraint c, String alias) {
        return names(c.getColumns(), alias);
    }

    void condition(Property prop, final String alias, final String[] key) {
        Mapping m = getMapping(prop);

        if (m.getRetrieve() != null) {
            append("exists(select 1 from (");
            bind(m.getRetrieve().getSQL(),
                 map(paths(prop.getContainer(), null), key));
            append(") sg where ");
            Path[] paths = paths(prop.getType(), Path.get(prop.getName()));
            equals(concat("sg.", columns(paths, m.getRetrieve())),
                   columns(prop.getType(), alias));
            append(")");
            return;
        }

        m.dispatch(new Mapping.Switch() {
            public void onValue(Value v) {
                Code.this.equals(v.getTable().getPrimaryKey(), alias, key);
            }
            public void onJoinTo(JoinTo j) {
                Code.this.equals(j.getTable().getPrimaryKey(), alias, key);
            }
            public void onJoinFrom(JoinFrom j) {
                Code.this.equals(j.getKey(), alias, key);
            }
            public void onJoinThrough(JoinThrough jt) {
                Code.this.equals(jt.getFrom(), alias, key);
            }
            public void onStatic(Static s) {}
        });
    }

    void equals(Constraint c, String alias, String[] columns) {
        equals(c.getColumns(), alias, columns);
    }

    void equals(Column[] left, String alias, String[] right) {
        equals(names(left, alias), right);
    }

    void equals(String[] left, String[] right) {
        if (left.length != right.length) {
            throw new IllegalArgumentException
                ("left size does not match right size");
        }
        for (int i = 0; i < left.length; i++) {
            append(left[i]);
            append(" = ");
            append(right[i]);
            if (i < left.length - 1) {
                append(" and ");
            }
        }
    }

    void table(final Property prop) {
        Mapping m = getMapping(prop);

        if (m.getRetrieve() != null) {
            table(prop.getType());
            return;
        }

        m.dispatch(new Mapping.Switch() {
            public void onValue(Value v) {
                append(v.getTable().getName());
            }
            public void onJoinTo(JoinTo j) {
                append(j.getTable().getName());
            }
            public void onJoinFrom(JoinFrom j) {
                append(j.getKey().getTable().getName());
            }
            public void onJoinThrough(JoinThrough jt) {
                append(jt.getFrom().getTable().getName());
            }
            public void onStatic(Static s) {}
        });
    }

    void table(ObjectType type) {
        ObjectMap om = type.getRoot().getObjectMap(type);
        if (om.getRetrieveAll() != null) {
            append("(");
            bind(om.getRetrieveAll().getSQL(), Collections.EMPTY_MAP);
            append(")");
        } else {
            append(om.getTable().getName());
        }
    }

    void bind(SQL sql, Map values) {
        for (SQLToken t = sql.getFirst(); t != null; t = t.getNext()) {
            if (t.isBind()) {
                Path key = Path.get(t.getImage().substring(1));
                String value = (String) values.get(key);
                if (value == null) {
                    throw new IllegalStateException
                        ("no value for: " + key + " in " + values);
                }
                append(value);
            } else {
                append(t.getImage());
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
            result.add(parent);
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
            return concat
                (alias + ".", columns(paths(type, null), om.getRetrieveAll()));
        } else {
            return names(om.getTable().getPrimaryKey().getColumns(), alias);
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

    private Map m_aliases = new HashMap();

    void setAlias(Expression expr, String alias) {
        m_aliases.put(expr, alias);
    }

    String getAlias(Expression expr) {
        return (String) m_aliases.get(expr);
    }

    private Set m_virtuals = new HashSet();

    void addVirtual(Expression e) {
        m_virtuals.add(e);
    }

    boolean isVirtual(Expression e) {
        return m_virtuals.contains(e);
    }

    void materialize(Expression e) {
        Frame f = getFrame(e);
        String[] columns = f.getColumns();

        if (isVirtual(e) && columns == null) {
            e.emit(this);
            return;
        }

        if (isVirtual(e)) {
            if (columns.length > 1) {
                append("(");
            }
        } else {
            append("(select ");
        }
        for (int i = 0; i < columns.length; i++) {
            append(columns[i]);
            if (i < columns.length - 1) {
                append(", ");
            }
        }
        if (isVirtual(e)) {
            if (columns.length > 1) {
                append(")");
            }
        } else {
            append(" from ");
            e.emit(this);
            append(")");
        }
    }

    boolean isQualias(Property prop) {
        if (prop.getRoot() == null) {
            return false;
        }
        return getMapping(prop) instanceof Qualias;
    }

    private Map m_qualiases = new HashMap();

    Code.Frame frame(Expression expr, Code.Frame parent, Property prop) {
        Qualias q = (Qualias) getMapping(prop);
        OQLParser p = new OQLParser(new StringReader(q.getQuery()));
        Expression qualias;
        try { qualias = p.expression(); }
        catch (ParseException e) {
            throw new IllegalStateException(e.getMessage());
        }
        Frame thisFrame = frame(Define.define("this", parent.type));
        thisFrame.setColumns(parent.getColumns());
        LinkedList stack = m_stack;
        try {
            m_stack = new LinkedList();
            push(parent);
            push(thisFrame);
            try {
                Code.Frame frame = qualias.frame(this);
                m_qualiases.put(expr, qualias);
                return frame;
            } finally {
                pop();
                pop();
            }
        } finally {
            m_stack = stack;
        }
    }

    void emit(Expression expr) {
        Expression qualias = (Expression) m_qualiases.get(expr);
        qualias.emit(this);
    }

    private Map m_staticChildren = new HashMap();

    void setChildren(com.redhat.persistence.oql.Static expr, List children) {
        m_staticChildren.put(expr, children);
    }

    List getChildren(com.redhat.persistence.oql.Static expr) {
        return (List) m_staticChildren.get(expr);
    }
}
