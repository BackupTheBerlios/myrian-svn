/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence.engine;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.*;

import org.apache.log4j.Logger;

import java.util.*;


/**
 * MemoryEngine
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/08/15 $
 **/

public class MemoryEngine extends Engine {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/MemoryEngine.java#2 $ by $Author: dennis $, $DateTime: 2003/08/15 13:46:34 $";

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

    protected long size(Query query) {
        RecordSet rs = execute(query);
        long result = 0;
        while (rs.next()) { result++; }
        return result;
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

    private Object get(final Query query, final Object obj, Expression e) {
        final Object[] result = { null };

        e.dispatch(new Expression.Switch() {
            public void onQuery(Query q) {
                throw new Error("not implemented");
            }

            public void onVariable(Expression.Variable v) {
                result[0] = get(query, obj, v.getPath());
            }

            public void onValue(Expression.Value v) {
                result[0] = v;
            }

            public void onPassthrough(Expression.Passthrough p) {
                throw new Error("not implemented");
            }

            public void onCondition(Condition c) {
                throw new Error("not implemented");
            }
        });

        return result[0];
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

            Expression filter = m_query.getFilter();
            if (filter != null) {
                for (Iterator it = m_objs.iterator(); it.hasNext(); ) {
                    Object obj = it.next();
                    if (!accept(obj, m_query, filter)) {
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

        public void close() {
            // do nothing
        }

        public String toString() {
            return m_objs.toString();
        }

    }

    boolean accept(final Object obj, final Query q, Expression filter) {
        final boolean[] result = { false };

        filter.dispatch(new Expression.Switch() {
            public void onQuery(Query q) {
                throw new Error("not implemented");
            }

            public void onVariable(Expression.Variable v) {
                throw new Error("not implemented");
            }

            public void onValue(Expression.Value v) {
                throw new Error("not implemented");
            }

            public void onPassthrough(Expression.Passthrough p) {
                throw new UnsupportedOperationException
                    ("unsupported filter type");
            }

            public void onCondition(Condition c) {
                c.dispatch(new Condition.Switch() {
                    public void onAnd(Condition.And f) {
                        result[0] = accept(obj, q, f.getLeft()) &&
                            accept(obj, q, f.getRight());
                    }

                    public void onOr(Condition.Or f) {
                        result[0] = accept(obj, q, f.getLeft()) ||
                            accept(obj, q, f.getRight());
                    }

                    public void onNot(Condition.Not f) {
                        result[0] = !accept(obj, q, f.getExpression());
                    }

                    public void onEquals(Condition.Equals f) {
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

                    public void onIn(Condition.In f) {
                        Object val = get(q, obj, f.getLeft());
                        DumbRecordSet drs = new DumbRecordSet
                            ((Query) f.getRight());
                        result[0] = drs.getObjects().contains(val);
                    }

                    public void onContains(Condition.Contains f) {
                        Set vals = (Set) get(q, obj, f.getLeft());
                        Object element = get(q, obj, f.getRight());
                        result[0] = vals.contains(element);
                    }
                });
            }
        });

        return result[0];
    }

}
