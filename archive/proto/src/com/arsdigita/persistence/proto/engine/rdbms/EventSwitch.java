package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * EventSwitch
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #11 $ $Date: 2003/03/05 $
 **/

class EventSwitch extends Event.Switch {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/EventSwitch.java#11 $ by $Author: rhs $, $DateTime: 2003/03/05 18:41:57 $";

    private static final Path KEY = Path.get("__key__");
    private static final Path KEY_FROM = Path.get("__key_from__");
    private static final Path KEY_TO = Path.get("__key_to__");

    private RDBMSEngine m_engine;

    public EventSwitch(RDBMSEngine engine) {
        m_engine = engine;
    }

    private static final Column getKey(Table table) {
        UniqueKey uk = table.getPrimaryKey();
        if (uk == null) {
            throw new IllegalArgumentException("no primary key: " + table);
        } else {
            return uk.getColumns()[0];
        }
    }

    private static final Path getPath(Column column) {
        return Path.get(column.toString());
    }

    private void set(Object obj, Column col, Object arg) {
        Table table = col.getTable();

        DML op = m_engine.getOperation(obj, table);
        if (op == null) {
            if (!getTables(Session.getObjectMap(obj),
                           false, true, false).contains(table)) {
                return;
            }

            Column key = getKey(table);
            op = new Update(table, new EqualsCondition(getPath(key), KEY));
            op.set(KEY, RDBMSEngine.getKeyValue(obj), key.getType());
            m_engine.addOperation(obj, op);
        }

        op.set(col, arg);
    }

    private Collection getTables(ObjectMap om, boolean ins, boolean up,
                                 boolean del) {
        Collection tables = om.getTables();

        while (om != null) {
            if ((ins && om.getInserts().size() > 0) ||
                (up && om.getUpdates().size() > 0) ||
                (del && om.getDeletes().size() > 0)) {
                tables.removeAll(om.getDeclaredTables());
            }

            om = om.getSuperMap();
        }

        return tables;
    }

    private void onObjectEvent(ObjectEvent e) {
        Object obj = e.getObject();

        Collection tables = getTables
            (e.getObjectMap(), e instanceof CreateEvent, false,
             e instanceof DeleteEvent);

        for (Iterator it = tables.iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            if (e instanceof CreateEvent) {
                DML ins = new Insert(table);
                ins.set(getKey(table), RDBMSEngine.getKeyValue(obj));
                m_engine.addOperation(obj, ins);
            } else if (e instanceof DeleteEvent) {
                Column key = getKey(table);
                DML del =
                    new Delete(table, new EqualsCondition(getPath(key), KEY));
                del.set(KEY, RDBMSEngine.getKeyValue(obj), key.getType());
                m_engine.addOperation(obj, del);
            } else {
                throw new IllegalArgumentException
                    ("not a create or delete event");
            }
        }
    }

    public void onCreate(final CreateEvent e) {
        onObjectEvent(e);

        addOperations(e.getObject(), e.getObjectMap().getInserts());
    }

    public void onDelete(final DeleteEvent e) {
        onObjectEvent(e);

        addOperations(e.getObject(), e.getObjectMap().getDeletes());
    }

    private void onPropertyEvent(final PropertyEvent e) {
        final Object obj = e.getObject();
        final Object arg = e.getArgument();

        final ObjectMap om = e.getObjectMap();
        Mapping m = om.getMapping(Path.get(e.getProperty().getName()));
        if ((e instanceof AddEvent && m.getAdds().size() > 0) ||
            (e instanceof RemoveEvent && m.getRemoves().size() > 0)) {
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
                    Column col = m.getKey().getColumns()[0];
                    set(obj, col, RDBMSEngine.getKeyValue(arg));
                }

                public void onJoinFrom(JoinFrom m) {
                    Column col = m.getKey().getColumns()[0];
                    set(arg, col, obj);
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
                        op.set(from, RDBMSEngine.getKeyValue(obj));
                        op.set(to, RDBMSEngine.getKeyValue(arg));
                        m_engine.addOperation(obj, arg, op);
                    } else if (e instanceof RemoveEvent ||
                               e instanceof SetEvent &&
                               arg == null) {
                        Condition cond;
                        if (e instanceof SetEvent) {
                            cond = new EqualsCondition(getPath(from),
                                                       KEY_FROM);
                        } else if (one2n) {
                            cond = new EqualsCondition(getPath(to),
                                                       KEY_TO);
                        } else {
                            cond = new AndCondition
                                (new EqualsCondition(getPath(from),
                                                     KEY_FROM),
                                 new EqualsCondition(getPath(to),
                                                     KEY_TO));
                        }

                        op = new Delete(table, cond);
                        op.set(KEY_FROM, RDBMSEngine.getKeyValue(obj),
                               from.getType());
                        op.set(KEY_TO, RDBMSEngine.getKeyValue(arg),
                               to.getType());
                        if (one2n) {
                            m_engine.addOperation(arg, null, op);
                        } else {
                            m_engine.addOperation(obj, arg, op);
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
        Collection ops = m_engine.getOperations(obj);
        if (ops == null) {
            addOperations(obj, e.getObjectMap().getUpdates());
            ops = m_engine.getOperations(obj);
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
        addOperations(e.getObject(), prop, e.getArgument(), m.getAdds());
    }

    public void onRemove(final RemoveEvent e) {
        onPropertyEvent(e);

        Property prop = e.getProperty();
        Mapping m = e.getObjectMap().getMapping(Path.get(prop.getName()));
        addOperations(e.getObject(), prop, e.getArgument(), m.getRemoves());
    }

    private void addOperations(Object obj, Collection blocks) {
        ObjectType type = Session.getObjectType(obj);
        m_engine.addOperations(obj);
        m_engine.clearOperations(obj);
        for (Iterator it = blocks.iterator(); it.hasNext(); ) {
            SQLBlock block = (SQLBlock) it.next();
            Environment env = m_engine.getEnvironment(obj);
            StaticOperation op = new StaticOperation(block, env);
            set(env, type, obj, null);
            m_engine.addOperation(obj, op);
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
            Path p = Path.get(path.getPath());
            env.set(p, obj);
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
