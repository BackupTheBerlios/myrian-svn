package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * RDBMSEngine
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/01/17 $
 **/

public class RDBMSEngine extends Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/RDBMSEngine.java#1 $ by $Author: rhs $, $DateTime: 2003/01/17 11:07:02 $";

    private static final Set getTables(ObjectMap om) {
        final HashSet result = new HashSet();
        for (Iterator it = om.getObjectType().getProperties().iterator();
             it.hasNext(); ) {
            Property prop = (Property) it.next();
            Mapping m = om.getMapping(Path.get(prop.getName()));
            // XXX: no metadata
            if (m == null) {
                return null;
            }
            m.dispatch(new Mapping.Switch() {
                    public void onValue(ValueMapping vm) {
                        result.add(vm.getColumn().getTable());
                    }

                    public void onReference(ReferenceMapping rm) {
                        if (rm.isJoinTo()) {
                            result.add(rm.getJoin(0).getFrom().getTable());
                        }
                    }
                });
        }
        return result;
    }

    private EventHandler m_handler = new EventHandler() {

            private void onObjectEvent(ObjectEvent e) {
                OID oid = e.getOID();
                Set tables = getTables(oid.getObjectMap());
                // XXX: no metadata
                if (tables == null) {
                    return;
                }
                for (Iterator it = tables.iterator(); it.hasNext(); ) {
                    Table table = (Table) it.next();
                    if (e instanceof CreateEvent) {
                        m_operations.add(new Insert(table, oid));
                    } else if (e instanceof DeleteEvent) {
                        m_operations.add(new Delete(table, oid));
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
                            Operation op = findOperation(col.getTable(), oid);
                            op.set(col, e.getArgument());
                        }

                        public void onReference(ReferenceMapping rm) {
                            if (rm.isJoinFrom()) {
                                Column col = rm.getJoin(0).getTo();
                                Operation op = findOperation(col.getTable(),
                                                             aoid);
                                op.set(col, oid);
                            } else if (rm.isJoinThrough()) {
                                Column from = rm.getJoin(0).getTo();
                                Column to = rm.getJoin(1).getFrom();
                                Operation op;
                                if (e instanceof AddEvent ||
                                    e instanceof SetEvent) {
                                    op = new Insert(from.getTable(), null);
                                } else if (e instanceof RemoveEvent) {
                                    op = new Delete(from.getTable(), null);
                                } else {
                                    throw new IllegalArgumentException
                                        ("not a set, add, or remove");
                                }
                                m_operations.add(op);
                                op.set(from, oid);
                                op.set(to, aoid);
                            } else if (rm.isJoinTo()) {
                                Column col = rm.getJoin(0).getFrom();
                                Operation op = findOperation(col.getTable(),
                                                             oid);
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

    public RDBMSEngine(Session ssn) {
        super(ssn);
    }

    private Operation findOperation(Table table, OID oid) {
        for (int i = m_operations.size() - 1; i >= 0; i--) {
            Operation op = (Operation) m_operations.get(i);
            if (table.equals(op.getTable()) && oid.equals(op.getOID())) {
                return op;
            }
        }

        Operation result = new Update(table, oid);
        m_operations.add(result);
        return result;
    }

    protected void commit() {}

    protected void rollback() {}
    protected RecordSet execute(Query query) { return null; }
    public EventHandler getEventHandler() { return m_handler; }
    protected void flush() {}

    public List getOperations() {
        return m_operations;
    }

}
