package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * EventSwitch
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/07 $
 **/

class EventSwitch extends Event.Switch {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/EventSwitch.java#1 $ by $Author: rhs $, $DateTime: 2003/02/07 16:00:49 $";

    private RDBMSEngine m_engine;

    public EventSwitch(RDBMSEngine engine) {
        m_engine = engine;
    }

    private static final Condition makeCondition(Table table, OID oid) {
        return makeCondition(table.getPrimaryKey().getColumns()[0], oid);
    }

    private static final Condition makeCondition(Column col, OID oid) {
        return new EqualsCondition
            (Path.get(col.toString()), Path.get("bind: " + oid));
    }

    private DML findOperation(OID oid, Table table) {
        DML op = m_engine.getOperation(oid, table);
        if (op != null) { return op; }

        DML result = new Update(table, makeCondition(table, oid));
        m_engine.addOperation(oid, result);
        return result;
    }

    private void onObjectEvent(ObjectEvent e) {
        OID oid = e.getOID();
        Collection tables = oid.getObjectMap().getTables();
        for (Iterator it = tables.iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            if (e instanceof CreateEvent) {
                m_engine.addOperation(oid, new Insert(table));
            } else if (e instanceof DeleteEvent) {
                DML del = new Delete(table, makeCondition(table, oid));
                m_engine.addOperation (oid, del);
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
        final OID oid = e.getOID();
        final OID aoid;

        if (e.getArgument() instanceof PersistentObject) {
            aoid = ((PersistentObject) e.getArgument()).getOID();
        } else {
            aoid = null;
        }

        final ObjectMap om = oid.getObjectMap();
        Mapping m = om.getMapping(Path.get(e.getProperty().getName()));
        // XXX: no metadata
        if (m == null) {
            return;
        }
        m.dispatch(new Mapping.Switch() {
                public void onValue(ValueMapping vm) {
                    Column col = vm.getColumn();
                    DML op = findOperation(oid, col.getTable());
                    op.set(col, e.getArgument());
                }

                public void onReference(ReferenceMapping rm) {
                    if (rm.isJoinFrom()) {
                        Column col = rm.getJoin(0).getTo();
                        DML op = findOperation(aoid, col.getTable());
                        op.set(col, oid);
                    } else if (rm.isJoinThrough()) {
                        Column from = rm.getJoin(0).getTo();
                        Column to = rm.getJoin(1).getFrom();
                        DML op;
                        if (e instanceof AddEvent ||
                            e instanceof SetEvent) {
                            op = new Insert(from.getTable());
                            op.set(from, oid);
                            op.set(to, aoid);
                        } else if (e instanceof RemoveEvent) {
                            op = new Delete
                                (from.getTable(), new AndCondition
                                    (makeCondition(from, oid),
                                     makeCondition(to, aoid)));
                        } else {
                            throw new IllegalArgumentException
                                ("not a set, add, or remove");
                                }
                        m_engine.addOperation(op);
                    } else if (rm.isJoinTo()) {
                        Column col = rm.getJoin(0).getFrom();
                        DML op = findOperation(oid, col.getTable());
                        op.set(col, e.getArgument());
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
