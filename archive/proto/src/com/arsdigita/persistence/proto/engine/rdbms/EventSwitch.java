package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * EventSwitch
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2003/02/18 $
 **/

class EventSwitch extends Event.Switch {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/EventSwitch.java#8 $ by $Author: rhs $, $DateTime: 2003/02/18 02:29:27 $";

    private static final Path KEY = Path.get("__key__");
    private static final Path KEY_FROM = Path.get("__key_from__");
    private static final Path KEY_TO = Path.get("__key_to__");

    private RDBMSEngine m_engine;

    public EventSwitch(RDBMSEngine engine) {
        m_engine = engine;
    }

    private static final Column getKey(Table table) {
        return table.getPrimaryKey().getColumns()[0];
    }

    private static final Path getPath(Column column) {
        return Path.get(column.toString());
    }

    private DML findOperation(Object obj, Table table) {
        DML op = m_engine.getOperation(obj, table);
        if (op != null) { return op; }

        Column key = getKey(table);
        DML result =
            new Update(table, new EqualsCondition(getPath(key), KEY));
        result.set(KEY, RDBMSEngine.getKeyValue(obj), key.getType());
        m_engine.addOperation(obj, result);
        return result;
    }

    private DML findOperation(Object from, Object to, Table map) {
        DML op = m_engine.getOperation(from, to, map);
        return op;
    }

    private void onObjectEvent(ObjectEvent e) {
        Object obj = e.getObject();
        Collection tables = e.getObjectMap().getTables();
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
    }

    public void onDelete(final DeleteEvent e) {
        onObjectEvent(e);
    }

    private void onPropertyEvent(final PropertyEvent e) {
        final Object obj = e.getObject();
        final Object arg = e.getArgument();

        final ObjectMap om = e.getObjectMap();
        Mapping m = om.getMapping(Path.get(e.getProperty().getName()));
        // XXX: no metadata
        if (m == null) {
            return;
        }

        final Role role = (Role) e.getProperty();

        m.dispatch(new Mapping.Switch() {
                public void onValue(ValueMapping vm) {
                    Column col = vm.getColumn();
                    DML op = findOperation(obj, col.getTable());
                    op.set(col, arg);
                }

                public void onReference(ReferenceMapping rm) {
                    if (rm.isJoinFrom()) {
                        Column col = rm.getJoin(0).getTo();
                        DML op = findOperation(arg, col.getTable());
                        op.set(col, RDBMSEngine.getKeyValue(obj));
                    } else if (rm.isJoinThrough()) {
                        Column from = rm.getJoin(0).getTo();
                        Column to = rm.getJoin(1).getFrom();
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
                    } else if (rm.isJoinTo()) {
                        Column col = rm.getJoin(0).getFrom();
                        DML op = findOperation(obj, col.getTable());
                        op.set(col, RDBMSEngine.getKeyValue(arg));
                    } else {
                        throw new IllegalArgumentException
                            ("not a join from, to, or through");
                            }
                }
            });
    }

    public void onSet(final SetEvent e) {
        onPropertyEvent(e);
    }

    public void onAdd(AddEvent e) {
        onPropertyEvent(e);
    }

    public void onRemove(final RemoveEvent e) {
        onPropertyEvent(e);
    }

}
