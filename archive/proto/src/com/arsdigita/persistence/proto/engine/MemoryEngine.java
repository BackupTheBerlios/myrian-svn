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
 * @version $Revision: #16 $ $Date: 2003/02/17 $
 **/

public class MemoryEngine extends Engine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/MemoryEngine.java#16 $ by $Author: rhs $, $DateTime: 2003/02/17 13:30:53 $";

    private static final Logger LOG = Logger.getLogger(MemoryEngine.class);

    private static class EventList extends ArrayList {
        public Event getEvent(int index) {
            return (Event) this.get(index);
        }
    }

    private static final EventList DATA = new EventList();

    private EventList m_uncomitted = new EventList();
    private EventList m_unflushed = new EventList();

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
        return drs;
    }

    private final Event.Switch m_switch = new Event.Switch() {
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

    protected void write(Event ev) {
        ev.dispatch(m_switch);
    }

    protected synchronized void flush() {
        m_uncomitted.addAll(m_unflushed);
        m_unflushed.clear();
    }

    private void checkLiveness(Object o) {
        if (o == null) {
            return;
        }

        if (!Adapter.getAdapter(o.getClass()).getObjectType(o).hasKey()) {
            return;
        }

        EventList[] els = {m_uncomitted, DATA};
        for (int j = 0; j < els.length; j++) {
            for (int i = els[j].size() - 1; i >= 0; i--) {
                Event ev = els[j].getEvent(i);
                if (ev.getObject().equals(o)) {
                    if (ev instanceof DeleteEvent) {
                        throw new Error("dead object:" + o);
                    } else if (ev instanceof CreateEvent) {
                        return;
                    }
                }
            }
        }

        throw new Error("dead object:" + o);
    }

    private Object get(Object obj, Property prop) {
        if (prop.isCollection()) {
            Set result = new HashSet();
            EventList[] els = {DATA, m_uncomitted};
            for (int j = 0; j < els.length; j++) {
                for (int i = 0; i < els[j].size(); i++) {
                    Event ev = els[j].getEvent(i);
                    if (ev instanceof PropertyEvent) {
                        PropertyEvent pev = (PropertyEvent) ev;
                        if (pev.getProperty().equals(prop) &&
                            obj.equals(pev.getObject())) {
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

            for (Iterator it = result.iterator(); it.hasNext(); ) {
                checkLiveness(it.next());
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
                            obj.equals(sev.getObject())) {
                            Object o = sev.getArgument();
                            checkLiveness(o);
                            return o;
                        }
                    }
                }
            }

            return null;
        }
    }

    private Object get(Query query, Object obj, Path p) {
        if (p == null) {
            return obj;
        }

        Path parent = p.getParent();
        if (parent == null) {
            Parameter param = query.getSignature().getParameter(p);
            if (param == null) {
                return get(obj,
                           Adapter.getAdapter(obj.getClass())
                           .getObjectType(obj).getProperty(p.getName()));
            } else {
                return query.get(param);
            }
        } else {
            Object value = get(query, obj, p.getParent());
            if (value == null) {
                return null;
            }

            return get(value, Adapter.getAdapter(obj.getClass())
                       .getObjectType(value).getProperty(p.getName()));
        }
    }

    private class DumbRecordSet extends RecordSet {

        private Query m_query;
        private Set m_objs = new HashSet();
        private Iterator m_it = null;
        private Object m_obj = null;

        public DumbRecordSet(Query query) {
            super(query.getSignature());
            m_query = query;

            EventList[] els = {DATA, m_uncomitted};
            for (int j = 0; j < els.length; j++) {
                for (int i = 0; i < els[j].size(); i++) {
                    Event ev = els[j].getEvent(i);
                    Object obj = ev.getObject();
                    if (!Adapter.getAdapter(obj.getClass()).getObjectType(obj)
                        .isSubtypeOf(getSignature().getObjectType())) {
                        continue;
                    }

                    if (ev instanceof CreateEvent) {
                        m_objs.add(ev.getObject());
                    } else if (ev instanceof DeleteEvent) {
                        m_objs.remove(ev.getObject());
                    }
                }
            }

            Filter f = m_query.getFilter();
            if (f != null) {
                for (Iterator it = m_objs.iterator(); it.hasNext(); ) {
                    Object obj = it.next();
                    if (!accept(obj, m_query, f)) {
                        it.remove();
                    }
                }
            }
        }

        public Set getObjects() {
            return m_objs;
        }

        public boolean next() {
            if (m_it == null) {
                m_it = m_objs.iterator();
            }

            if (m_it.hasNext()) {
                m_obj = m_it.next();
                return true;
            } else {
                return false;
            }
        }

        public Object get(Path p) {
            return MemoryEngine.this.get(m_query, m_obj, p);
        }

        public String toString() {
            return m_objs.toString();
        }

    }

    boolean accept(final Object obj, final Query q, Filter filter) {
        final boolean[] result = { false };

        filter.dispatch(new Filter.Switch() {

                public void onAnd(AndFilter f) {
                    result[0] = accept(obj, q, f.getLeft()) &&
                        accept(obj, q, f.getRight());
                }

                public void onOr(OrFilter f) {
                    result[0] = accept(obj, q, f.getLeft()) ||
                        accept(obj, q, f.getRight());
                }

                public void onNot(NotFilter f) {
                    result[0] = !accept(obj, q, f.getOperand());
                }

                public void onEquals(EqualsFilter f) {
                    Object left = get(q, obj, f.getLeft());
                    Object right = get(q, obj, f.getRight());
                    if (left == right) {
                        result[0] = true;
                    } else if (left == null) {
                        result[0] = right == null;
                    } else {
                        result[0] = left.equals(right);
                    }
                }

                public void onIn(InFilter f) {
                    Object val = get(q, obj, f.getPath());
                    DumbRecordSet drs = new DumbRecordSet(f.getQuery());
                    result[0] = drs.getObjects().contains(val);
                }

                public void onContains(ContainsFilter f) {
                    Set vals = (Set) get(q, obj, f.getCollection());
                    Object element = get(q, obj, f.getElement());
                    result[0] = vals.contains(element);
                }

            });

        return result[0];
    }

}
