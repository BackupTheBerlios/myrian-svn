package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * EventSwitch
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #19 $ $Date: 2003/03/31 $
 **/

class EventSwitch extends Event.Switch {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/EventSwitch.java#19 $ by $Author: rhs $, $DateTime: 2003/03/31 14:34:45 $";

    private static final Logger LOG = Logger.getLogger(EventSwitch.class);

    private static final Path KEY = Path.get("__key__");
    private static final Path KEY_FROM = Path.get("__key_from__");
    private static final Path KEY_TO = Path.get("__key_to__");

    private RDBMSEngine m_engine;

    public EventSwitch(RDBMSEngine engine) {
        m_engine = engine;
    }

    private static final Path getPath(Column column) {
        return Path.get(column.toString());
    }

    // I think this will be broken if obj is null or if it contains any keys
    // that are null.
    void flatten(Path prefix, Object obj, List paths, Map values) {
        LinkedList stack = new LinkedList();
        stack.add(prefix);
        stack.add(obj);

        while (stack.size() > 0) {
            Object o = stack.removeLast();
            Path p = (Path) stack.removeLast();

            Collection keys = null;

            if (o != null) {
                ObjectType type = Session.getObjectType(o);
                keys = type.getKeyProperties();
            }

            if (keys == null || keys.size() == 0) {
                paths.add(p);
                values.put(p, o);
                continue;
            }

            PropertyMap props = Session.getProperties(o);
            ArrayList revKeys = new ArrayList(keys.size());
            revKeys.addAll(keys);
            Collections.reverse(revKeys);

            for (Iterator it = revKeys.iterator(); it.hasNext(); ) {
                Property key = (Property) it.next();
                if (p == null) {
                    stack.add(Path.get(key.getName()));
                } else {
                    stack.add(Path.get(p.getPath() + "." + key.getName()));
                }
                stack.add(props.get(key));
            }
        }
    }

    private void filter(Mutation mut, Constraint constraint, Path prefix,
                        Object obj) {
        LinkedList paths = new LinkedList();
        HashMap values = new HashMap();
        flatten(prefix, obj, paths, values);

        Column[] cols = constraint.getColumns();
        int index = 0;
        Condition cond = mut.getCondition();

        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            Object o = values.get(p);
            Condition eq = new EqualsCondition(getPath(cols[index]), p);
            if (cond == null) {
                cond = eq;
            } else {
                cond = new AndCondition(eq, cond);
            }
            mut.set(p, o, cols[index].getType());
            index++;
        }

        mut.setCondition(cond);
    }

    private void set(DML op, Constraint constraint, Object obj) {
        set(op, constraint.getColumns(), obj);
    }

    private void set(DML op, Column[] cols, Object obj) {
        LinkedList paths = new LinkedList();
        HashMap values = new HashMap();
        flatten(null, obj, paths, values);

        int index = 0;

        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            Object o = values.get(p);
            op.set(cols[index++], o);
        }
    }

    private void set(Object obj, Constraint cons, Object arg) {
        set(obj, cons.getColumns(), arg);
    }

    private void set(Object obj, Column col, Object arg) {
        set(obj, new Column[] { col }, arg);
    }

    private void set(Object obj, Column[] cols, Object arg) {
        Table table = cols[0].getTable();

        DML op = m_engine.getOperation(obj, table);
        if (op == null) {
            if (!getTables(Session.getObjectMap(obj),
                           false, true, false).contains(table)) {
                return;
            }

            Update up = new Update(table, null);
            filter(up, table.getPrimaryKey(), KEY, obj);
            m_engine.addOperation(obj, up);
	    m_engine.markUpdate(obj, up);
            op = up;
        }

        set(op, cols, arg);
    }

    private Collection getTables(ObjectMap om, boolean ins, boolean up,
                                 boolean del) {
        Collection tables = om.getTables();

        while (om != null) {
            if ((ins && om.getDeclaredInserts() != null) ||
                (up && om.getDeclaredUpdates() != null) ||
                (del && om.getDeclaredDeletes() != null)) {
                tables.removeAll(om.getDeclaredTables());
            }

            om = om.getSuperMap();
        }

        return tables;
    }

    private void addDML(Object obj, Collection tables, boolean insert) {
        for (Iterator it = tables.iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            if (insert) {
                DML ins = new Insert(table);
                set(ins, table.getPrimaryKey(), obj);
                m_engine.addOperation(obj, ins);
            } else {
                Delete del = new Delete(table, null);
                filter(del, table.getPrimaryKey(), KEY, obj);
                m_engine.addOperation(obj, del);
            }
        }
    }

    private List getObjectMaps(ObjectMap om) {
        if (om == null) {
            return new ArrayList();
        } else {
            List result = getObjectMaps(om.getSuperMap());
            result.add(om);
            return result;
        }
    }

    private void onObjectEvent(ObjectEvent e) {
        Object obj = e.getObject();
        List oms = getObjectMaps(e.getObjectMap());
        if (e instanceof DeleteEvent) {
            Collections.reverse(oms);
        }

	if (e instanceof DeleteEvent) {
	    m_engine.removeUpdates(obj);
	} else {
	    if (m_engine.hasUpdates(obj)) {
		throw new IllegalStateException
		    ("updates exist for object being created");
	    }
	}

        for (Iterator it = oms.iterator(); it.hasNext(); ) {
            ObjectMap om = (ObjectMap) it.next();
            if (e instanceof CreateEvent) {
                if (om.getDeclaredInserts() != null) {
                    addOperations(obj, om.getDeclaredInserts());
                } else {
                    addDML(obj, om.getDeclaredTables(), true);
                }
            } else if (e instanceof DeleteEvent) {
                if (om.getDeclaredDeletes() != null) {
                    addOperations(obj, om.getDeclaredDeletes());
                } else {
                    addDML(obj, om.getDeclaredTables(), false);
                }
            } else {
                throw new IllegalArgumentException
                    ("not a create or delete event");
            }
        }
    }

    public void onCreate(final CreateEvent e) {
        onObjectEvent(e);
    }

    public void onDelete(final DeleteEvent e) {
        onObjectEvent(e);
    }

    private void onPropertyEvent(final PropertyEvent e) {
        final Object obj = e.getObject();
        final Object arg = e.getArgument();

        final ObjectMap om = e.getObjectMap();
        Mapping m = om.getMapping(Path.get(e.getProperty().getName()));
        if ((e instanceof AddEvent && m.getAdds() != null) ||
            (e instanceof RemoveEvent && m.getRemoves() != null)) {
            return;
        }

        final Role role = (Role) e.getProperty();

        m.dispatch(new Mapping.Switch() {
                public void onStatic(Static m) {
                    // do nothing;
                }

                public void onValue(Value m) {
                    Column col = m.getColumn();
                    set(obj, col, arg);
                }

                public void onJoinTo(JoinTo m) {
                    set(obj, m.getKey(), arg);
                }

                public void onJoinFrom(JoinFrom m) {
                    set(arg, m.getKey(), obj);
                }

                public void onJoinThrough(JoinThrough m) {
                    Column from = m.getFrom().getColumns()[0];
                    Column to = m.getTo().getColumns()[0];
                    Table table = from.getTable();

                    DML op = m_engine.getOperation(obj, arg, table);
                    // This should eliminate duplicates, but we could be
                    // smarter by canceling out inserts and updates which
                    // we don't do right now.
                    if (op != null) { return; }

                    boolean one2n = role.isReversable() &&
                        !role.getReverse().isCollection();
                    if (one2n) {
                        op = m_engine.getOperation(arg, null, table);
                    } else {
                        op = m_engine.getOperation(arg, obj, table);
                    }
                    if (op != null) { return; }

                    if (e instanceof AddEvent ||
                        e instanceof SetEvent &&
                        arg != null) {
                        op = new Insert(table);
                        set(op, m.getFrom(), obj);
                        set(op, m.getTo(), arg);
                        m_engine.addOperation(obj, arg, op);
                    } else if (e instanceof RemoveEvent ||
                               e instanceof SetEvent &&
                               arg == null) {
                        Delete del = new Delete(table, null);
                        if (e instanceof SetEvent) {
                            filter(del, m.getFrom(), KEY_FROM, obj);
                        } else if (one2n) {
                            filter(del, m.getTo(), KEY_TO, arg);
                        } else {
                            filter(del, m.getFrom(), KEY_FROM, obj);
                            filter(del, m.getTo(), KEY_TO, arg);
                        }

                        if (one2n) {
                            m_engine.addOperation(arg, null, del);
                        } else {
                            m_engine.addOperation(obj, arg, del);
                        }
                    } else {
                        throw new IllegalArgumentException
                            ("not a set, add, or remove");
                    }
                }
            });
    }

    public void onSet(final SetEvent e) {
        onPropertyEvent(e);

        Object obj = e.getObject();
	if (!m_engine.hasUpdates(obj)) {
	    m_engine.markUpdate(obj);
            addOperations(obj, e.getObjectMap().getUpdates(), false);
        }

        Property prop = e.getProperty();
        Path path = Path.get(prop.getName());

        Environment env = m_engine.getEnvironment(obj);
        set(env, prop.getType(), e.getArgument(), path);
    }

    public void onAdd(AddEvent e) {
        onPropertyEvent(e);

        Property prop = e.getProperty();
        Mapping m = e.getObjectMap().getMapping(Path.get(prop.getName()));
        if (m.getAdds() != null) {
            addOperations(e.getObject(), prop, e.getArgument(), m.getAdds());
        }
    }

    public void onRemove(final RemoveEvent e) {
        onPropertyEvent(e);

        Property prop = e.getProperty();
        Mapping m = e.getObjectMap().getMapping(Path.get(prop.getName()));
        if (m.getRemoves() != null) {
            addOperations(e.getObject(), prop, e.getArgument(),
                          m.getRemoves());
        }
    }

    private void addOperations(Object obj, Collection blocks) {
	addOperations(obj, blocks, true);
    }

    private void addOperations(Object obj, Collection blocks,
			       boolean initialize) {
        ObjectType type = Session.getObjectType(obj);
        for (Iterator it = blocks.iterator(); it.hasNext(); ) {
            SQLBlock block = (SQLBlock) it.next();
            Environment env = m_engine.getEnvironment(obj);
            StaticOperation op = new StaticOperation(block, env, initialize);
            set(env, type, obj, null);
            m_engine.addOperation(op);
	    // We're overloading initialize here to figure out that
	    // this is an update
	    if (!initialize) {
		m_engine.markUpdate(obj, op);
	    }
        }
    }

    private void addOperations(Object from, Property prop, Object to,
                               Collection blocks) {
        Environment fromEnv = m_engine.getEnvironment(from);
        set(fromEnv, prop.getContainer(), from, null);
        Environment toEnv = m_engine.getEnvironment(to);
        set(toEnv, prop.getType(), to, null);

        Path path = Path.get(prop.getName());
        Environment env = new SpliceEnvironment(fromEnv, path, toEnv);
        Role role = (Role) prop;
        if (role.isReversable()) {
            env = new SpliceEnvironment
                (env, Path.get(role.getReverse().getName()), fromEnv);
        }

        for (Iterator it = blocks.iterator(); it.hasNext(); ) {
            SQLBlock block = (SQLBlock) it.next();
            StaticOperation op = new StaticOperation(block, env);
            m_engine.addOperation(op);
        }
    }

    private void set(Environment env, ObjectType type, Object obj,
                     Path path) {
        if (!type.hasKey()) {
            env.set(path, obj);
            return;
        }

        PropertyMap props;
        if (obj == null) {
            props = new PropertyMap(type);
        } else {
            props = Session.getProperties(obj);
        }

        for (Iterator it = type.getKeyProperties().iterator();
             it.hasNext(); ) {
            Property key = (Property) it.next();

            Path keyPath;
            if (path == null) {
                keyPath = Path.get(key.getName());
            } else {
                keyPath = Path.get(path.getPath() + "." + key.getName());
            }

            set(env, key.getType(), props.get(key), keyPath);
        }
    }

}
