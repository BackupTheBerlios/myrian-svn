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
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public class Engine implements PersistenceEngine {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/engine/Engine.java#1 $ by $Author: rhs $, $DateTime: 2002/11/27 17:41:53 $";

    private static class EventList extends ArrayList {
        public Event getEvent(int index) {
            return (Event) get(index);
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

    public Cursor execute(Query query) {
        return new DumbCursor(query);
    }

    public synchronized void write(Event event) {
        m_unflushed.add(event);
    }

    public synchronized void flush() {
        m_uncomitted.addAll(m_unflushed);
        m_unflushed.clear();
    }

    private static final FilterSource FS = new FilterSource() {

            public AndFilter getAnd(Filter leftOperand, Filter rightOperand) {
                return new AndFilter(leftOperand, rightOperand) {};
            }

            public OrFilter getOr(Filter leftOperand, Filter rightOperand) {
                return new OrFilter(leftOperand, rightOperand) {};
            }

            public NotFilter getNot(Filter operand) {
                return new NotFilter(operand) {};
            }

        };

    public FilterSource getFilterSource() {
        return FS;
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

    private class DumbCursor extends Cursor {

        private Query m_query;
        private Set m_oids = new HashSet();
        private Iterator m_it = null;
        private OID m_oid = null;

        public DumbCursor(Query query) {
            super(m_ssn, query.getSignature());
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
        }

        protected boolean fetchRow() {
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

        private Object fetch(OID oid, Property prop) {
            EventList[] els = {m_uncomitted, DATA};
            for (int j = 0; j < els.length; j++) {
                for (int i = els[j].size() - 1; i >= 0; i--) {
                    Event ev = els[j].getEvent(i);
                    if (ev instanceof SetEvent) {
                        SetEvent sev = (SetEvent) ev;
                        if (sev.getProperty().equals(prop) &&
                            m_oid.equals(sev.getOID())) {
                            return sev.getArgument();
                        }
                    }
                }
            }

            return null;
        }

        protected Object fetchPath(Path p) {
            Path parent = p.getParent();
            if (parent == null) {
                return fetch(m_oid,
                             m_oid.getObjectType().getProperty(p.getName()));
            } else {
                Object value = fetchPath(p.getParent());
                if (value == null) {
                    return null;
                } else if (value instanceof PersistentObject) {
                    OID oid = ((PersistentObject) value).getOID();
                    return fetch(oid,
                                 oid.getObjectType().getProperty(p.getName()));
                } else {
                    throw new IllegalArgumentException
                        ("Path references attribute of opaque type: " + p);
                }
            }
        }

    }

}
