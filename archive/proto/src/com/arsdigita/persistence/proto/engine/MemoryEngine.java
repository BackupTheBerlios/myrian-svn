package com.arsdigita.persistence.proto.engine;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.persistence.proto.*;

import org.apache.log4j.Logger;

import java.util.*;


/**
 * MemoryEngine
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2003/01/30 $
 **/

public class MemoryEngine extends Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/MemoryEngine.java#10 $ by $Author: rhs $, $DateTime: 2003/01/30 17:57:25 $";

    private static final Logger LOG = Logger.getLogger(MemoryEngine.class);

    private static class EventList extends ArrayList {
        public Event getEvent(int index) {
            return (Event) this.get(index);
        }
    }

    private static final EventList DATA = new EventList();

    private EventList m_uncomitted = new EventList();
    private EventList m_unflushed = new EventList();
    private com.arsdigita.persistence.proto.engine.rdbms.RDBMSEngine m_engine;
    private EventHandler m_subHandler;


    public MemoryEngine(Session ssn) {
        super(ssn);
        m_engine = new com.arsdigita.persistence.proto.engine.rdbms.RDBMSEngine(ssn);
        m_subHandler = m_engine.getEventHandler();
    }

    protected void commit() {
        synchronized (DATA) {
            DATA.addAll(m_uncomitted);
            m_uncomitted.clear();
        }
    }

    protected synchronized void rollback() {
        m_uncomitted.clear();
        m_unflushed.clear();
    }

    protected RecordSet execute(Query query) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("execute: " + query);
        }
        DumbRecordSet drs = new DumbRecordSet(query);
        if (LOG.isDebugEnabled()) {
            LOG.debug("result: " + drs);
        }
        m_engine.execute(query);
        return drs;
    }

    private final EventHandler m_handler = new EventHandler() {
            public void onCreate(CreateEvent e) {
                m_unflushed.add(e);
                m_subHandler.onCreate(e);
            }

            public void onDelete(DeleteEvent e) {
                m_unflushed.add(e);
                m_subHandler.onDelete(e);
            }

            public void onSet(SetEvent e) {
                m_unflushed.add(e);
                m_subHandler.onSet(e);
            }

            public void onAdd(AddEvent e) {
                m_unflushed.add(e);
                m_subHandler.onAdd(e);
            }

            public void onRemove(RemoveEvent e) {
                m_unflushed.add(e);
                m_subHandler.onRemove(e);
            }
        };

    protected EventHandler getEventHandler() {
        return m_handler;
    }

    protected synchronized void flush() {
        m_uncomitted.addAll(m_unflushed);
        m_unflushed.clear();

        LOG.debug(m_engine.getOperations());
        m_engine.getOperations().clear();
    }

    private Object get(OID oid, Property prop) {
        if (prop.isCollection()) {
            Set result = new HashSet();
            EventList[] els = {DATA, m_uncomitted};
            for (int j = 0; j < els.length; j++) {
                for (int i = 0; i < els[j].size(); i++) {
                    Event ev = els[j].getEvent(i);
                    if (ev instanceof PropertyEvent) {
                        PropertyEvent pev = (PropertyEvent) ev;
                        if (pev.getProperty().equals(prop) &&
                            oid.equals(pev.getOID())) {
                            if (pev instanceof AddEvent) {
                                result.add(pev.getArgument());
                            } else if (pev instanceof RemoveEvent) {
                                result.remove(pev.getArgument());
                            } else {
                                throw new IllegalStateException
                                    ("Can't mix add remove and set events " +
                                     "on a single property");
                            }
                        }
                    }
                }
            }

            return result;
        } else {
            EventList[] els = {m_uncomitted, DATA};
            for (int j = 0; j < els.length; j++) {
                for (int i = els[j].size() - 1; i >= 0; i--) {
                    Event ev = els[j].getEvent(i);
                    if (ev instanceof SetEvent) {
                        SetEvent sev = (SetEvent) ev;
                        if (sev.getProperty().equals(prop) &&
                            oid.equals(sev.getOID())) {
                            return sev.getArgument();
                        }
                    }
                }
            }

            return null;
        }
    }

    private Object get(Query query, OID oid, Path p) {
        if (p == null) {
            return new PersistentObjectSource()
                .getPersistentObject(getSession(), oid);
        }

        Path parent = p.getParent();
        if (parent == null) {
            Parameter param = query.getSignature().getParameter(p);
            if (param == null) {
                return get(oid,
                           oid.getObjectType().getProperty(p.getName()));
            } else {
                return query.get(param);
            }
        } else {
            Object value = get(query, oid, p.getParent());
            if (value == null) {
                return null;
            } else if (value instanceof PersistentObject) {
                OID poid = ((PersistentObject) value).getOID();
                return get(poid,
                           poid.getObjectType().getProperty(p.getName()));
            } else {
                throw new IllegalArgumentException
                    ("Path references attribute of opaque type: " + p);
            }
        }
    }

    private class DumbRecordSet extends RecordSet {

        private Query m_query;
        private Set m_oids = new HashSet();
        private Iterator m_it = null;
        private OID m_oid = null;

        public DumbRecordSet(Query query) {
            super(query.getSignature());
            m_query = query;

            EventList[] els = {DATA, m_uncomitted};
            for (int j = 0; j < els.length; j++) {
                for (int i = 0; i < els[j].size(); i++) {
                    Event ev = els[j].getEvent(i);
                    if (!ev.getOID().getObjectType().isSubtypeOf
                        (getSignature().getObjectType())) {
                        continue;
                    }

                    if (ev instanceof CreateEvent) {
                        m_oids.add(ev.getOID());
                    } else if (ev instanceof DeleteEvent) {
                        m_oids.remove(ev.getOID());
                    }
                }
            }

            Filter f = m_query.getFilter();
            if (f != null) {
                for (Iterator it = m_oids.iterator(); it.hasNext(); ) {
                    OID oid = (OID) it.next();
                    if (!accept(oid, m_query, f)) {
                        it.remove();
                    }
                }
            }
        }

        public Set getOIDs() {
            return m_oids;
        }

        public boolean next() {
            if (m_it == null) {
                m_it = m_oids.iterator();
            }

            if (m_it.hasNext()) {
                m_oid = (OID) m_it.next();
                return true;
            } else {
                return false;
            }
        }

        public Object get(Path p) {
            return MemoryEngine.this.get(m_query, m_oid, p);
        }

        public String toString() {
            return m_oids.toString();
        }

    }

    boolean accept(final OID oid, final Query q, Filter filter) {
        final boolean[] result = { false };

        filter.dispatch(new Filter.Switch() {

                public void onAnd(AndFilter f) {
                    result[0] = accept(oid, q, f.getLeft()) &&
                        accept(oid, q, f.getRight());
                }

                public void onOr(OrFilter f) {
                    result[0] = accept(oid, q, f.getLeft()) ||
                        accept(oid, q, f.getRight());
                }

                public void onNot(NotFilter f) {
                    result[0] = !accept(oid, q, f.getOperand());
                }

                public void onEquals(EqualsFilter f) {
                    Object left = get(q, oid, f.getLeft());
                    Object right = get(q, oid, f.getRight());
                    if (left == right) {
                        result[0] = true;
                    } else if (left == null) {
                        result[0] = right == null;
                    } else {
                        result[0] = left.equals(right);
                    }
                }

                public void onIn(InFilter f) {
                    Object val = get(q, oid, f.getPath());
                    DumbRecordSet drs = new DumbRecordSet(f.getQuery());
                    result[0] = drs.getOIDs().contains(val);
                }

                public void onContains(ContainsFilter f) {
                    Set vals = (Set) get(q, oid, f.getCollection());
                    Object element = get(q, oid, f.getElement());
                    result[0] = vals.contains(element);
                }

            });

        return result[0];
    }

}
