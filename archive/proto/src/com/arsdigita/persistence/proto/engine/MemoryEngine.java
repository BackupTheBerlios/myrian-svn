package com.arsdigita.persistence.proto.engine;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.Event;
import com.arsdigita.persistence.OID;

import org.apache.log4j.Logger;

import java.util.*;


/**
 * MemoryEngine
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/10 $
 **/

public class MemoryEngine extends Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/MemoryEngine.java#2 $ by $Author: rhs $, $DateTime: 2002/12/10 15:09:40 $";

    private static final Logger LOG = Logger.getLogger(MemoryEngine.class);

    private static class EventList extends ArrayList {
        public Event getEvent(int index) {
            return (Event) this.get(index);
        }
    }

    private static final EventList DATA = new EventList();

    private EventList m_uncomitted = new EventList();
    private EventList m_unflushed = new EventList();

    public MemoryEngine(Session ssn) {
        super(ssn);
    }

    protected void commit() {
        synchronized (DATA) {
            DATA.addAll(m_uncomitted);
            m_uncomitted.clear();
        }
    }

    protected synchronized void rollback() {
        m_uncomitted.clear();
    }

    protected RecordSet execute(Query query) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("execute: " + query);
        }
        DumbRecordSet drs = new DumbRecordSet(query);
        if (LOG.isDebugEnabled()) {
            LOG.debug("result: " + drs);
        }
        return drs;
    }

    private final EventHandler m_handler = new EventHandler() {
            public void onCreate(CreateEvent e) {
                m_unflushed.add(e);
            }

            public void onDelete(DeleteEvent e) {
                m_unflushed.add(e);
            }

            public void onSet(SetEvent e) {
                m_unflushed.add(e);
            }

            public void onAdd(AddEvent e) {
                m_unflushed.add(e);
            }

            public void onRemove(RemoveEvent e) {
                m_unflushed.remove(e);
            }
        };

    protected EventHandler getEventHandler() {
        return m_handler;
    }

    protected synchronized void flush() {
        m_uncomitted.addAll(m_unflushed);
        m_unflushed.clear();
    }

    protected Filter getAnd(Filter leftOperand, Filter rightOperand) {
        return new DumbAndFilter(leftOperand, rightOperand);
    }

    protected Filter getOr(Filter leftOperand, Filter rightOperand) {
        return new DumbOrFilter(leftOperand, rightOperand);
    }

    protected Filter getNot(Filter operand) {
        return new DumbNotFilter(operand);
    }

    protected Filter getEquals(Path path, Object value) {
        return new DumbEqualsFilter(path, value);
    }

    protected Filter getIn(Path path, Query query) {
        return new DumbInFilter(path, query);
    }

    protected Filter getContains(Path path, Object value) {
        return new DumbContainsFilter(path, value);
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

    private Object get(OID oid, Path p) {
        Path parent = p.getParent();
        if (parent == null) {
            return get(oid, oid.getObjectType().getProperty(p.getName()));
        } else {
            Object value = get(oid, p.getParent());
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

            DumbFilter df = (DumbFilter) query.getFilter();
            if (df != null) {
                for (Iterator it = m_oids.iterator(); it.hasNext(); ) {
                    OID oid = (OID) it.next();
                    if (!df.accept(oid)) {
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
            return MemoryEngine.this.get(m_oid, p);
        }

        public String toString() {
            return m_oids.toString();
        }

    }

    interface DumbFilter {
        boolean accept(OID oid);
    }

    class DumbAndFilter extends AndFilter implements DumbFilter {
        public DumbAndFilter(Filter leftOperand, Filter rightOperand) {
            super(leftOperand, rightOperand);
        }

        public boolean accept(OID oid) {
            return ((DumbFilter) getLeftOperand()).accept(oid) &&
                ((DumbFilter) getRightOperand()).accept(oid);
        }
    }

    class DumbOrFilter extends OrFilter implements DumbFilter {
        public DumbOrFilter(Filter leftOperand, Filter rightOperand) {
            super(leftOperand, rightOperand);
        }

        public boolean accept(OID oid) {
            return ((DumbFilter) getLeftOperand()).accept(oid) ||
                ((DumbFilter) getRightOperand()).accept(oid);
        }
    }

    class DumbNotFilter extends NotFilter implements DumbFilter {
        public DumbNotFilter(Filter operand) {
            super(operand);
        }

        public boolean accept(OID oid) {
            return !((DumbFilter) getOperand()).accept(oid);
        }
    }


    class DumbEqualsFilter extends EqualsFilter implements DumbFilter {
        public DumbEqualsFilter(Path path, Object value) {
            super(path, value);
        }

        public boolean accept(OID oid) {
            Object left = get(oid, getPath());
            Object right = getValue();
            if (left == right) {
                return true;
            } else if (left == null) {
                return right == null;
            } else {
                return left.equals(right);
            }
        }
    }

    class DumbInFilter extends InFilter implements DumbFilter {
        public DumbInFilter(Path path, Query query) {
            super(path, query);
        }

        public boolean accept(OID oid) {
            Object val = get(oid, getPath());
            DumbRecordSet drs = new DumbRecordSet(getQuery());
            return drs.getOIDs().contains(val);
        }
    }

    class DumbContainsFilter extends ContainsFilter implements DumbFilter {
        public DumbContainsFilter(Path path, Object value) {
            super(path, value);
        }

        public boolean accept(OID oid) {
            Set vals = (Set) get(oid, getPath());
            return vals.contains(getValue());
        }
    }

}
