package com.arsdigita.persistence.proto.engine;

import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.persistence.proto.*;

import org.apache.log4j.Logger;

import java.util.*;


/**
 * MemoryEngine
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2003/01/06 $
 **/

public class MemoryEngine extends Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/MemoryEngine.java#4 $ by $Author: rhs $, $DateTime: 2003/01/06 16:31:02 $";

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

    protected RecordSet execute(Binding binding) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("execute: " + binding);
        }
        DumbRecordSet drs = new DumbRecordSet(binding);
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
                m_unflushed.add(e);
            }
        };

    protected EventHandler getEventHandler() {
        return m_handler;
    }

    protected synchronized void flush() {
        m_uncomitted.addAll(m_unflushed);
        m_unflushed.clear();
    }

    protected Filter getAnd(Filter left, Filter right) {
        return new DumbAndFilter(left, right);
    }

    protected Filter getOr(Filter left, Filter right) {
        return new DumbOrFilter(left, right);
    }

    protected Filter getNot(Filter operand) {
        return new DumbNotFilter(operand);
    }

    protected Filter getEquals(Path left, Path right) {
        return new DumbEqualsFilter(left, right);
    }

    protected Filter getIn(Path path, Binding binding) {
        return new DumbInFilter(path, binding);
    }

    protected Filter getContains(Path collection, Path element) {
        return new DumbContainsFilter(collection, element);
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

    private Object get(Binding binding, OID oid, Path p) {
        if (p == null) {
            return new PersistentObjectSource()
                .getPersistentObject(getSession(), oid);
        }

        Path parent = p.getParent();
        if (parent == null) {
            Parameter param = binding.getQuery().getParameter(p);
            if (param == null) {
                return get(oid,
                           oid.getObjectType().getProperty(p.getName()));
            } else {
                return binding.get(param);
            }
        } else {
            Object value = get(binding, oid, p.getParent());
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

        private Binding m_binding;
        private Query m_query;
        private Set m_oids = new HashSet();
        private Iterator m_it = null;
        private OID m_oid = null;

        public DumbRecordSet(Binding binding) {
            super(binding.getQuery().getSignature());
            m_binding = binding;
            m_query = m_binding.getQuery();

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

            DumbFilter df = (DumbFilter) m_query.getFilter();
            if (df != null) {
                for (Iterator it = m_oids.iterator(); it.hasNext(); ) {
                    OID oid = (OID) it.next();
                    if (!df.accept(m_binding, oid)) {
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
            return MemoryEngine.this.get(m_binding, m_oid, p);
        }

        public String toString() {
            return m_oids.toString();
        }

    }

    interface DumbFilter {
        boolean accept(Binding b, OID oid);
    }

    class DumbAndFilter extends AndFilter implements DumbFilter {
        public DumbAndFilter(Filter left, Filter right) {
            super(left, right);
        }

        public boolean accept(Binding b, OID oid) {
            return ((DumbFilter) getLeft()).accept(b, oid) &&
                ((DumbFilter) getRight()).accept(b, oid);
        }
    }

    class DumbOrFilter extends OrFilter implements DumbFilter {
        public DumbOrFilter(Filter left, Filter right) {
            super(left, right);
        }

        public boolean accept(Binding b, OID oid) {
            return ((DumbFilter) getLeft()).accept(b, oid) ||
                ((DumbFilter) getRight()).accept(b, oid);
        }
    }

    class DumbNotFilter extends NotFilter implements DumbFilter {
        public DumbNotFilter(Filter operand) {
            super(operand);
        }

        public boolean accept(Binding b, OID oid) {
            return !((DumbFilter) getOperand()).accept(b, oid);
        }
    }


    class DumbEqualsFilter extends EqualsFilter implements DumbFilter {
        public DumbEqualsFilter(Path left, Path right) {
            super(left, right);
        }

        public boolean accept(Binding b, OID oid) {
            Object left = get(b, oid, getLeft());
            Object right = get(b, oid, getRight());
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
        public DumbInFilter(Path path, Binding binding) {
            super(path, binding);
        }

        public boolean accept(Binding b, OID oid) {
            Object val = get(b, oid, getPath());
            DumbRecordSet drs = new DumbRecordSet(getBinding());
            return drs.getOIDs().contains(val);
        }
    }

    class DumbContainsFilter extends ContainsFilter implements DumbFilter {
        public DumbContainsFilter(Path collection, Path element) {
            super(collection, element);
        }

        public boolean accept(Binding b, OID oid) {
            Set vals = (Set) get(b, oid, getCollection());
            Object element = get(b, oid, getElement());
            boolean result = vals.contains(element);
            return result;
        }
    }

}
