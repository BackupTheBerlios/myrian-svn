package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * EventSwitch
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/02/13 $
 **/

class EventSwitch extends Event.Switch {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/EventSwitch.java#3 $ by $Author: rhs $, $DateTime: 2003/02/13 18:36:15 $";

    private static final Path KEY = Path.get("__key__");
    private static final Path KEY_FROM = Path.get("__key_from__");
    private static final Path KEY_TO = Path.get("__key_to__");

    private RDBMSEngine m_engine;
    private final Session m_ssn;

    public EventSwitch(RDBMSEngine engine) {
        m_engine = engine;
        m_ssn = engine.getSession();
    }

    private static final Path getKey(Table table) {
        return Path.get(table.getPrimaryKey().getColumns()[0].toString());
    }

    private static final Path getPath(Column column) {
        return Path.get(column.toString());
    }

    private Object getKeyValue(Object obj) {
        if (obj == null) { return null; }
        Adapter ad = Adapter.getAdapter(obj.getClass());
        ObjectType type = ad.getObjectType(obj);
        Collection keys = type.getKeyProperties();
        if (keys.size() != 1) {
            throw new Error("not implemented");
        }
        return ad.getProperties(obj).get((Property) keys.iterator().next());
    }

    private DML findOperation(Object obj, Table table) {
        DML op = m_engine.getOperation(obj, table);
        if (op != null) { return op; }

        DML result =
            new Update(table, new EqualsCondition(getKey(table), KEY));
        result.set(KEY, getKeyValue(obj));
        m_engine.addOperation(obj, result);
        return result;
    }

    private void onObjectEvent(ObjectEvent e) {
        Object obj = e.getObject();
        Collection tables = m_ssn.getObjectMap(obj).getTables();
        for (Iterator it = tables.iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            if (e instanceof CreateEvent) {
                m_engine.addOperation(obj, new Insert(table));
            } else if (e instanceof DeleteEvent) {
                DML del = new Delete
                    (table, new EqualsCondition(getKey(table), KEY));
                del.set(KEY, getKeyValue(obj));
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

        final ObjectMap om = m_ssn.getObjectMap(obj);
        Mapping m = om.getMapping(Path.get(e.getProperty().getName()));
        // XXX: no metadata
        if (m == null) {
            return;
        }
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
                        op.set(col, getKeyValue(obj));
                    } else if (rm.isJoinThrough()) {
                        Column from = rm.getJoin(0).getTo();
                        Column to = rm.getJoin(1).getFrom();
                        DML op;
                        if (e instanceof AddEvent ||
                            e instanceof SetEvent) {
                            op = new Insert(from.getTable());
                            op.set(from, getKeyValue(obj));
                            op.set(to, getKeyValue(arg));
                        } else if (e instanceof RemoveEvent) {
                            op = new Delete
                                (from.getTable(), new AndCondition
                                    (new EqualsCondition(getPath(from),
                                                         KEY_FROM),
                                     new EqualsCondition(getPath(to),
                                                         KEY_TO)));
                            op.set(KEY_FROM, getKeyValue(obj));
                            op.set(KEY_TO, getKeyValue(arg));
                        } else {
                            throw new IllegalArgumentException
                                ("not a set, add, or remove");
                                }
                        m_engine.addOperation(op);
                    } else if (rm.isJoinTo()) {
                        Column col = rm.getJoin(0).getFrom();
                        DML op = findOperation(obj, col.getTable());
                        op.set(col, getKeyValue(arg));
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
