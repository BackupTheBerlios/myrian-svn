package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.Query;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.persistence.proto.metadata.Table;
import com.arsdigita.persistence.proto.metadata.Column;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * RDBMSEngine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2003/02/06 $
 **/

public class RDBMSEngine extends Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/RDBMSEngine.java#8 $ by $Author: rhs $, $DateTime: 2003/02/06 12:29:10 $";

    private static final Logger LOG = Logger.getLogger(RDBMSEngine.class);

    private Event.Switch m_switch = new Event.Switch() {

            private void onObjectEvent(ObjectEvent e) {
                OID oid = e.getOID();
                Collection tables = oid.getObjectMap().getTables();
                for (Iterator it = tables.iterator(); it.hasNext(); ) {
                    Table table = (Table) it.next();
                    if (e instanceof CreateEvent) {
                        addOperation(oid, new Insert(table));
                    } else if (e instanceof DeleteEvent) {
                        addOperation
                            (oid, new Delete
                                (table, makeCondition
                                 (table.getPrimaryKey().getColumns()[0],
                                  oid)));
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
                Mapping m =
                    om.getMapping(Path.get(e.getProperty().getName()));
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
                                addOperation(op);
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

        };

    private ArrayList m_operations = new ArrayList();
    private HashMap m_operationMap = new HashMap();

    public RDBMSEngine(Session ssn) {
        super(ssn);
    }

    private static final Condition makeCondition(Column col, OID oid) {
        return new EqualsCondition
            (Path.get(col.toString()), Path.get("bind: " + oid));
    }

    private void addOperation(OID oid, DML dml) {
        String key = oid + ":" + dml.getTable().getName();
        if (dml instanceof Delete) {
            DML prev = (DML) m_operationMap.get(key);
            if (prev != null) {
                m_operations.remove(prev);
            }
        }
        m_operationMap.put(key, dml);
        m_operations.add(dml);
    }

    private void addOperation(DML dml) {
        m_operations.add(dml);
    }

    private DML findOperation(OID oid, Table table) {
        DML op = (DML) m_operationMap.get(oid + ":" + table.getName());
        if (op != null) { return op; }

        DML result = new Update
            (table, makeCondition
             (table.getPrimaryKey().getColumns()[0], oid));
        addOperation(oid, result);
        return result;
    }

    protected void commit() {}

    protected void rollback() {}

    public RecordSet execute(Query query) {
        Signature sig = query.getSignature();
        com.arsdigita.persistence.proto.engine.rdbms.Query q =
            new com.arsdigita.persistence.proto.engine.rdbms.Query
                (Root.getRoot().getObjectMap(sig.getObjectType()));
        for (Iterator it = sig.getPaths().iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            q.fetch(p.getPath());
        }

        if (LOG.isDebugEnabled() &&
            q.getChildren().size() + q.getSelections().size() > 0) {
            LOG.debug(q.toSQL());
            QGen qg = new QGen(sig);
            LOG.debug(qg.generate());
        }
        return null;
    }

    public void write(Event ev) {
        ev.dispatch(m_switch);
    }

    public void flush() {
        if (LOG.isDebugEnabled()) {
            LOG.debug(m_operations);
        }
        m_operationMap.clear();
        m_operations.clear();
    }

}
