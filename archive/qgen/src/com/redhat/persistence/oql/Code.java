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
 * @version $Revision: #9 $ $Date: 2004/02/21 $
 **/

class Code {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Code.java#9 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    private static final Logger s_log = Logger.getLogger(Code.class);

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

    class Var {
        String alias;
        String column;
        Var(String alias, String column) {
            this.alias = alias;
            this.column = column;
        }
        Var(String qualified) {
            Path p = Path.get(qualified);
            column = p.getName();
            alias = p.getParent() == null ? null : p.getParent().getPath();
        }
        public String toString() {
            if (alias == null) {
                return column;
            } else {
                return alias + "." + column;
            }
        }
    }

    class Cond {
        Var left;
        Var right;
        Cond(Var left, Var right) {
            this.left = left;
            this.right = right;
        }
        public String toString() {
            String l = "" + left;
            String r = "" + right;
            if (l.equals("null")) {
                return right + " is " + left;
            } else if (r.equals("null")) {
                return left + " is " + right;
            } else {
                return left + " = " + right;
            }
        }
    }

    // alias -> table
    Map tables = new HashMap();
    // {alias, alias}
    Set duplicates = new HashSet();

    void setTable(String alias, String table) {
        tables.put(alias, table);
    }

    String getTable(String alias) {
        return (String) tables.get(alias);
    }

    private boolean isConstrained(String table, Collection columns) {
        Table t = m_root.getTable(table);
        if (t == null) { return false; }
        outer: for (Iterator it = t.getConstraints().iterator();
                    it.hasNext(); ) {
            Object o = it.next();
            if (o instanceof UniqueKey) {
                UniqueKey key = (UniqueKey) o;
                Column[] cols = key.getColumns();
                for (int i = 0; i < cols.length; i++) {
                    if (!columns.contains(cols[i].getName())) {
                        continue outer;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void fillConstraints(Collection aliases, Collection conditions,
                                 Map columns, Map constraints) {
        for (Iterator it = aliases.iterator(); it.hasNext(); ) {
            String alias = (String) it.next();
            for (Iterator iter = conditions.iterator(); iter.hasNext(); ) {
                Cond cond = (Cond) iter.next();
                Var me, outer;
                if (alias.equals(cond.left.alias)) {
                    me = cond.left;
                    outer = cond.right;
                } else if (alias.equals(cond.right.alias)) {
                    me = cond.right;
                    outer = cond.left;
                } else {
                    continue;
                }
                if (!aliases.contains(outer.alias)) {
                    Set cols = (Set) columns.get(me.alias);
                    if (cols == null) {
                        cols = new HashSet();
                        columns.put(me.alias, cols);
                    }
                    cols.add(me.column);

                    List l = (List) constraints.get(me.alias);
                    if (l == null) {
                        l = new ArrayList();
                        constraints.put(me.alias, l);
                    }
                    l.add(cond);
                }
            }
        }
    }

    List frames = new ArrayList();

    class Frame {

        ObjectType type;
        private String[] m_columns = null;

        // vars
        List vars = null;
        // aliases
        List aliases = new ArrayList();
        // var = var
        List conditions = new ArrayList();

        private Frame(ObjectType type) {
            this.type = type;
            frames.add(this);
        }

        void addCondition(Var left, Var right) {
            conditions.add(new Cond(left, right));
        }

        void dups() {
            for (Iterator it = aliases.iterator(); it.hasNext(); ) {
                String alias = (String) it.next();
                dups(alias);
            }
        }

        void dups(String alias) {
            String table = getTable(alias);
            if (table == null) { return; }

            List selfConditions = new ArrayList();
            for (Iterator it = conditions.iterator(); it.hasNext(); ) {
                Cond c = (Cond) it.next();
                if ((alias.equals(c.left.alias)
                     && table.equals(getTable(c.right.alias)))
                    || (alias.equals(c.right.alias)
                        && table.equals(getTable(c.left.alias)))) {
                    if (c.left.column.equals(c.right.column)) {
                        selfConditions.add(c);
                    }
                }
            }

            List others = new ArrayList(aliases);
            others.remove(alias);

            Map cols = new HashMap();
            Map conds = new HashMap();

            fillConstraints(others, selfConditions, cols, conds);

            for (Iterator it = others.iterator(); it.hasNext(); ) {
                String oalias = (String) it.next();
                Collection c = (Collection) cols.get(oalias);
                if (c == null) { continue; }
                if (isConstrained(getTable(oalias), c)) {
                    duplicates.add(new CompoundKey(oalias, alias));
                }
            }
        }

        void suckAll(Frame other) {
            if (this.equals(other)) { return; }
            aliases.addAll(other.aliases);
            conditions.addAll(other.conditions);
            other.aliases.clear();
            other.conditions.clear();
        }

        void suckConstrained(Frame other) {
            if (this.equals(other)) { return; }

            if (other.aliases.isEmpty()) {
                conditions.addAll(other.conditions);
                other.conditions.clear();
                return;
            }

            int before;
            do {
                before = aliases.size() + conditions.size();
                suckOnce(other);
            } while (aliases.size() + conditions.size() > before);
        }

        private void suckOnce(Frame other) {
            Map columns = new HashMap();
            Map conds = new HashMap();
            fillConstraints(other.aliases, other.conditions, columns, conds);
            for (Iterator it = other.aliases.iterator(); it.hasNext(); ) {
                String alias = (String) it.next();
                Collection cols = (Collection) columns.get(alias);
                if (cols == null) { continue; }
                String table = (String) tables.get(alias);
                if (isConstrained(table, cols)) {
                    aliases.add(alias);
                    it.remove();
                    List l = (List) conds.get(alias);
                    if (l != null) {
                        conditions.addAll(l);
                        other.conditions.removeAll(l);
                    }
                }
            }
        }

        String join() {
            Set joined = new HashSet();
            List conds = new ArrayList(conditions);
            StringBuffer buf = null;
            for (Iterator it = aliases.iterator(); it.hasNext(); ) {
                String alias = (String) it.next();
                joined.add(alias);
                StringBuffer on = null;
                for (Iterator iter = conds.iterator(); iter.hasNext(); ) {
                    Cond c = (Cond) iter.next();
                    if (joined.contains(c.left.alias) &&
                        joined.contains(c.right.alias)) {
                        if (on == null) {
                            on = new StringBuffer();
                        } else {
                            on.append(" and ");
                        }
                        on.append(c);
                        iter.remove();
                    }
                }

                if (buf == null) {
                    buf = new StringBuffer();
                } else if (on == null) {
                    buf.append(" cross join ");
                } else {
                    buf.append(" join ");
                }

                buf.append(getTable(alias));
                buf.append(" ");
                buf.append(alias);

                if (on != null) {
                    buf.append(" on ");
                    buf.append(on.toString());
                }
            }

            if (!conds.isEmpty()) {
                if (buf == null) {
                    buf = new StringBuffer();
                    buf.append("(select 1) " + var("fd"));
                }
                buf.append(" join ");
                buf.append("(select 2) " + var("fd"));
                buf.append(" on ");

                for (Iterator it = conds.iterator(); it.hasNext(); ) {
                    Cond c = (Cond) it.next();
                    buf.append(c.toString());
                    if (it.hasNext()) {
                        buf.append(" and ");
                    }
                }
            }

            if (buf == null) {
                return null;
            } else {
                return buf.toString();
            }
        }

        void setColumns(String[] columns) {
            vars = new ArrayList();
            for (int i = 0; i < columns.length; i++) {
                if (columns[i] == null) {
                    throw new IllegalArgumentException
                        ("null column: " + Arrays.asList(columns));
                }
                vars.add(new Var(columns[i]));
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
            aliases.add(alias);
            setColumns(columns(prop, alias));
            return alias;
        }

        String alias(ObjectType type) {
            String alias = var("t");
            aliases.add(alias);
            setColumns(columns(type, alias));
            return alias;
        }

        String alias(int ncolumns) {
            String alias = var("t");
            aliases.add(alias);
            String[] columns = new String[ncolumns];
            for (int i = 0; i < columns.length; i++) {
                columns[i] = alias + ".col" + i;
            }
            setColumns(columns);
            return alias;
        }

        void condition(Property prop, final String alias, final String[] key) {
            Mapping m = getMapping(prop);

            if (m.getRetrieve() != null) {
                StringBuffer buf = new StringBuffer();
                buf.append("exists(select 1 from (");
                bind(m.getRetrieve().getSQL(),
                     map(paths(prop.getContainer(), null), key), buf);
                buf.append(") sg where ");
                Path[] paths = paths(prop.getType(), Path.get(prop.getName()));
                Code.this.equals
                    (concat("sg.", columns(paths, m.getRetrieve())),
                     columns(prop.getType(), alias), buf);
                buf.append(")");
                final String cond = buf.toString();
                conditions.add(new Cond(new Var(null, null),
                                        new Var(null, null)) {
                    public String toString() {
                        return cond;
                    }
                });
                return;
            }

            m.dispatch(new Mapping.Switch() {
                public void onValue(Value v) {
                    condition(v.getTable().getPrimaryKey(), alias, key);
                }
                public void onJoinTo(JoinTo j) {
                    condition(j.getTable().getPrimaryKey(), alias, key);
                }
                public void onJoinFrom(JoinFrom j) {
                    condition(j.getKey(), alias, key);
                }
                public void onJoinThrough(JoinThrough jt) {
                    condition(jt.getFrom(), alias, key);
                }
                public void onStatic(Static s) {}
            });
        }

        void condition(Constraint c, String alias, String[] columns) {
            condition(c.getColumns(), alias, columns);
        }

        void condition(Column[] left, String alias, String[] right) {
            condition(names(left, alias), right);
        }

        void condition(String[] left, String[] right) {
            if (left.length != right.length) {
                throw new IllegalArgumentException
                    ("left size does not match right size");
            }
            for (int i = 0; i < left.length; i++) {
                addCondition(new Var(left[i]), new Var(right[i]));
            }
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
            buf.append(" vars = " + vars);
            buf.append(" aliases = " + aliases);
            buf.append(">");
            return buf.toString();
        }

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

    String getTrace() {
        StringBuffer buf = new StringBuffer();
        for (Iterator it = m_stack.iterator(); it.hasNext(); ) {
            buf.append(it.next());
            buf.append("\n");
        }
        return buf.toString();
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

    void alias(String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            append(columns[i]);
            append(" as col" + i);
            if (i < columns.length - 1) {
                append(", ");
            }
        }
    }

    static String[] columns(Property prop, final String alias) {
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
                 map(paths(prop.getContainer(), null), key), m_sql);
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
        equals(left, right, m_sql);
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
            return om.getTable().getName();
        }
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
            String[] columns = columns(paths(type, null), om.getRetrieveAll());
            if (alias == null) {
                return columns;
            } else {
                return concat(alias + ".", columns);
            }
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

    void opt(Expression expr) {
        Expression qualias = (Expression) m_qualiases.get(expr);
        qualias.opt(this);
        Frame f = getFrame(expr);
        Frame q = getFrame(qualias);
        f.suckAll(q);
    }

}
