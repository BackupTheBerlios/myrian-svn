package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Code
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/01/23 $
 **/

class Code {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Code.java#1 $ by $Author: rhs $, $DateTime: 2004/01/23 15:34:30 $";

    private LinkedList m_stack = new LinkedList();
    private Map m_frames = new HashMap();
    private Map m_contexts = new HashMap();
    private Set m_packages = new HashSet();

    private StringBuffer m_sql = new StringBuffer();
    private int m_counter = 0;

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

    // XXX: hacks for package lookup
    void addPackage(Expression expr) {
        m_packages.add(expr);
    }

    boolean isPackage(Expression expr) {
        return m_packages.contains(expr);
    }

    String getSQL() {
        return m_sql.toString();
    }

    void append(String sql) {
        m_sql.append(sql);
    }

    String var() {
        return "v" + m_counter++;
    }

    String[] vars(ObjectType type) {
        ArrayList vars = new ArrayList();
        vars(type, vars);
        return (String[]) vars.toArray(new String[vars.size()]);
    }

    private void vars(ObjectType type, List vars) {
        Collection props = type.getKeyProperties();
        if (props.isEmpty()) {
            props = type.getImmediateProperties();
        }
        if (props.isEmpty()) {
            vars.add(var());
        }
        for (Iterator it = props.iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            vars(prop.getType(), vars);
        }
    }

    class Frame {

        ObjectType type;
        private Map m_columns = new HashMap();

        private Frame(ObjectType type) {
            this.type = type;
        }

        void setColumns(Property prop, String[] columns) {
            m_columns.put(prop, columns);
        }

        void setColumns(String prop, String[] columns) {
            setColumns(type.getProperty(prop), columns);
        }

        String[] getColumns(Property prop) {
            return (String[]) m_columns.get(prop);
        }

        String[] getColumns(String prop) {
            return getColumns(type.getProperty(prop));
        }

        String[] getColumns() {
            ArrayList result = new ArrayList();
            Collection props = type.getKeyProperties();
            if (props.isEmpty()) {
                props = type.getImmediateProperties();
            }
            if (props.isEmpty()) {
                props = Collections.singleton(null);
            }
            for (Iterator it = props.iterator(); it.hasNext(); ) {
                Property prop = (Property) it.next();
                String[] cols = getColumns(prop);
                for (int i = 0; i < cols.length; i++) {
                    result.add(cols[i]);
                }
            }
            if (result.isEmpty()) {
                return null;
            } else {
                return (String[]) result.toArray(new String[result.size()]);
            }
        }

        void alias() {
            Collection props = type.getKeyProperties();
            if (props.isEmpty()) {
                props = type.getImmediateProperties();
            }
            if (props.isEmpty()) {
                setColumns((Property) null, vars(type));
            }
            for (Iterator it = props.iterator(); it.hasNext(); ) {
                Property prop = (Property) it.next();
                setColumns(prop, vars(prop.getType()));
            }
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("<frame ");
            buf.append(type);
            buf.append(" ");
            for (Iterator it = m_columns.entrySet().iterator();
                 it.hasNext(); ) {
                Map.Entry me = (Map.Entry) it.next();
                Property prop = (Property) me.getKey();
                String[] columns = (String[]) me.getValue();
                buf.append(prop);
                buf.append(" = ");
                buf.append(Arrays.asList(columns));
            }
            buf.append(">");
            return buf.toString();
        }

    }

    static String[] names(Column[] columns) {
        String[] result = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            Column col = columns[i];
            result[i] = col.getTable().getName() + "." + col.getName();
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

    void alias(Property prop, final String[] vars) {
        Mapping m = getMapping(prop);
        m.dispatch(new Mapping.Switch() {
            public void onValue(Value v) {
                alias(new Column[] {v.getColumn()}, vars);
            }
            public void onJoinTo(JoinTo j) {
                alias(j.getKey(), vars);
            }
            public void onJoinFrom(JoinFrom j) {
                alias(j.getKey().getTable().getPrimaryKey(), vars);
            }
            public void onJoinThrough(JoinThrough jt) {
                alias(jt.getTo(), vars);
            }
            public void onStatic(Static s) {
                throw new IllegalStateException("static: " + s);
            }
        });
    }

    void alias(ObjectType type, String[] vars) {
        ObjectMap om = type.getRoot().getObjectMap(type);
        alias(om.getTable().getPrimaryKey(), vars);
    }

    void alias(Constraint c, String[] vars) {
        alias(c.getColumns(), vars);
    }

    void alias(Column[] columns, String[] vars) {
        alias(names(columns), vars);
    }

    void alias(String[] columns, String[] vars) {
        if (columns.length != vars.length) {
            throw new IllegalArgumentException
                ("columns: " + Arrays.asList(columns) + ", vars: " +
                 Arrays.asList(vars) + "\n" + getTrace());
        }
        for (int i = 0; i < columns.length; i++) {
            append(columns[i]);
            append(" as ");
            append(vars[i]);
            if (i < columns.length - 1) {
                append(", ");
            }
        }
    }

    void condition(Property prop, final String[] key) {
        Mapping m = getMapping(prop);
        m.dispatch(new Mapping.Switch() {
            public void onValue(Value v) {
                Code.this.equals(v.getTable().getPrimaryKey(), key);
            }
            public void onJoinTo(JoinTo j) {
                Code.this.equals(j.getTable().getPrimaryKey(), key);
            }
            public void onJoinFrom(JoinFrom j) {
                Code.this.equals(j.getKey(), key);
            }
            public void onJoinThrough(JoinThrough jt) {
                Code.this.equals(jt.getFrom(), key);
            }
            public void onStatic(Static s) {
                throw new IllegalStateException("static: " + s);
            }
        });
    }

    void equals(Constraint c, String[] columns) {
        equals(c.getColumns(), columns);
    }

    void equals(Column[] left, String[] right) {
        equals(names(left), right);
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

    void table(Property prop) {
        Mapping m = getMapping(prop);
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
            public void onStatic(Static s) {
                throw new IllegalStateException("static: " + s);
            }
        });
    }

    void table(ObjectType type) {
        ObjectMap om = type.getRoot().getObjectMap(type);
        append(om.getTable().getName());
    }

}
