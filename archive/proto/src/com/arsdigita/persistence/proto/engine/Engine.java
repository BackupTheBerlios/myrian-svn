package com.arsdigita.persistence.proto.engine;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.Event;
import com.arsdigita.persistence.OID;
import java.util.*;

/**
 * Engine
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/04 $
 **/

public class Engine implements PersistenceEngine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/Engine.java#2 $ by $Author: rhs $, $DateTime: 2002/12/04 19:18:22 $";

    private static class EventList extends ArrayList {
        public Event getEvent(int index) {
            return (Event) this.get(index);
        }
    }

    private static final EventList DATA = new EventList();

    private EventList m_uncomitted = new EventList();
    private EventList m_unflushed = new EventList();

    private Session m_ssn;

    public Engine(Session ssn) {
        m_ssn = ssn;
    }

    public void commit() {
        synchronized (DATA) {
            DATA.addAll(m_uncomitted);
            m_uncomitted.clear();
        }
    }

    public synchronized void rollback() {
        m_uncomitted.clear();
    }

    public RecordSet execute(Query query) {
        System.out.println("Executing " + query);
        DumbRecordSet drs = new DumbRecordSet(query);
        System.out.println(" --> " + drs.getOIDs());
        return drs;
    }

    public synchronized void write(Event event) {
        m_unflushed.add(event);
    }

    public synchronized void flush() {
        m_uncomitted.addAll(m_unflushed);
        m_unflushed.clear();
    }

    public FilterSource getFilterSource() {
        return new FilterSource() {

                public AndFilter getAnd(Filter leftOperand,
                                        Filter rightOperand) {
                    return new DumbAndFilter(leftOperand, rightOperand);
                }

                public OrFilter getOr(Filter leftOperand,
                                      Filter rightOperand) {
                    return new DumbOrFilter(leftOperand, rightOperand);
                }

                public NotFilter getNot(Filter operand) {
                    return new DumbNotFilter(operand);
                }

                public EqualsFilter getEquals(Path path, Object value) {
                    return new DumbEqualsFilter(path, value);
                }

                public InFilter getIn(Path path, Query query) {
                    return new DumbInFilter(path, query);
                }

                public ContainsFilter getContains(Path path, Object value) {
                    return new DumbContainsFilter(path, value);
                }
            };
    }

    private static final EventSource ES = new EventSource() {

            public CreateEvent getCreate(Session ssn, OID oid) {
                return new CreateEvent(ssn, oid) {};
            }

            public DeleteEvent getDelete(Session ssn, OID oid) {
                return new DeleteEvent(ssn, oid) {};
            }

            public SetEvent getSet(Session ssn, OID oid, Property prop,
                                   Object arg) {
                return new SetEvent(ssn, oid, prop, arg) {};
            }

            public AddEvent getAdd(Session ssn, OID oid, Property prop,
                                   Object arg) {
                return new AddEvent(ssn, oid, prop, arg) {};
            }

            public RemoveEvent getRemove(Session ssn, OID oid, Property prop,
                                   Object arg) {
                return new RemoveEvent(ssn, oid, prop, arg) {};
            }

        };

    public EventSource getEventSource() {
        return ES;
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
            return Engine.this.get(m_oid, p);
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
