package com.redhat.persistence;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * EventStream objects are containers for events that provide mechanisms
 * for filtering the stream down to the set of events that are of interest.
 *
 * @author <a href="mailto:ashah@redhat.com">Archit Shah</a>
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class EventStream {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/EventStream.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    // all events
    private final LinkedList m_events = new LinkedList();
    // obj -> current object event
    private final Map m_objectEvents = new HashMap();
    // CK(obj,prop) -> current event
    private final Map m_setEvents = new HashMap();
    // CK(obj,prop) -> current event list
    private final Map m_collectionEvents = new HashMap();

    private final boolean m_coalescing;

    public EventStream() { this(false); }

    public EventStream(boolean coalescing) { m_coalescing = coalescing; }

    public int size() { return m_events.size(); }

    public List getEvents() { return m_events; }

    public Iterator iterator() { return m_events.iterator(); }

    public ListIterator listIterator() { return m_events.listIterator(); }

    public void clear() {
        m_events.clear();
        m_objectEvents.clear();
        m_setEvents.clear();
        m_collectionEvents.clear();
    }

    private final static Object getKey(Object obj) {
        return Session.getSessionKey(obj);
    }

    private final static Object getKey(Object obj, Property prop) {
        return new CompoundKey(Session.getSessionKey(obj), prop);
    }

    public void add(Event ev) {
        m_events.add(ev);
        ev.dispatch(new Event.Switch() {
            private void onObjectEvent(ObjectEvent e) {
                m_objectEvents.put(getKey(e.getObject()), e);
                ObjectType type = Session.getObjectType(e.getObject());
                for (Iterator it = type.getProperties().iterator();
                     it.hasNext(); ) {
                    Property prop = (Property) it.next();
                    Object key = getKey(e.getObject(), prop);
                    if (prop.isCollection()) {
                        if (m_collectionEvents.containsKey(key)) {
                            m_collectionEvents.remove(key);
                        }
                    } else {
                        if (m_setEvents.containsKey(key)) {
                            m_setEvents.remove(key);
                        }
                    }
                }
            }
            public void onCreate(CreateEvent e) { onObjectEvent(e); }
            public void onDelete(DeleteEvent e) {
                Object key = getKey(e.getObject());
                boolean found = false;
                if (m_coalescing && getLastEvent(e.getObject()) != null) {
                    CreateEvent ce = (CreateEvent) getLastEvent(e.getObject());
                    for (Iterator it = m_events.iterator(); it.hasNext(); ) {
                        Event ev = (Event) it.next();
                        if (ev.equals(ce)) {
                            found = true;
                        }

                        if (!found) { continue; }

                        if (getKey(ev.getObject()).equals(key)) {
                            it.remove();
                        } else if (ev instanceof PropertyEvent) {
                            PropertyEvent pe = (PropertyEvent) ev;
                            if (key.equals(getKey(pe.getArgument()))) {
                                it.remove();
                            }
                        }
                    }

                    if (!found) { throw new IllegalStateException(); }
                }
                onObjectEvent(e);
                if (m_coalescing && found) {
                    m_objectEvents.remove(key);
                    for (int i = m_events.size() - 1; i >= 0; i--) {
                        if (m_events.get(i) instanceof DeleteEvent) {
                            DeleteEvent de = (DeleteEvent) m_events.get(i);
                            if (key.equals(getKey(de.getObject()))) {
                                m_objectEvents.put(key, de);
                                break;
                            }
                        }
                    }
                }
            }
            public void onSet(SetEvent e) {
                m_setEvents.put((getKey(e.getObject(), e.getProperty())), e);
            }
            private void onCollectionEvent(PropertyEvent e) {
                Object key = getKey(e.getObject(), e.getProperty());
                List lst = (List) m_collectionEvents.get(key);
                if (lst == null) {
                    lst = new LinkedList();
                    m_collectionEvents.put(key, lst);
                }
                lst.add(e);
            }
            public void onAdd(AddEvent e) { onCollectionEvent(e); }
            public void onRemove(RemoveEvent e) { onCollectionEvent(e); }
        });
    }

    void remove(Event ev) {
        m_events.remove(ev);
        ev.dispatch(new Event.Switch() {
            private void onObjectEvent(ObjectEvent e) {
                if (e.equals(getLastEvent(e.getObject()))) {
                    m_objectEvents.remove(getKey(e.getObject()));
                }
            }
            public void onCreate(CreateEvent e) { onObjectEvent(e); }
            public void onDelete(DeleteEvent e) { onObjectEvent(e); }
            public void onSet(SetEvent e) {
                if (e.equals
                    (getLastEvent(e.getObject(), e.getProperty()))) {
                    m_setEvents.remove
                        (getKey(e.getObject(), e.getProperty()));
                }
            }
            private void onCollectionEvent(PropertyEvent e) {
                List lst = (List) m_collectionEvents.get
                    (getKey(e.getObject(), e.getProperty()));
                if (lst != null) { lst.remove(e); }
            }
            public void onAdd(AddEvent e) { onCollectionEvent(e); }
            public void onRemove(RemoveEvent e) { onCollectionEvent(e); }
        });
    }

    public ObjectEvent getLastEvent(Object obj) {
        if (obj == null) { return null; }
        return (ObjectEvent) m_objectEvents.get(getKey(obj));
    }

    public PropertyEvent getLastEvent(Object obj, Property prop) {
        if (prop.isCollection()) {
            LinkedList lst =
                (LinkedList) m_collectionEvents.get(getKey(obj, prop));
            if (lst == null) { return null; }
            return (PropertyEvent) lst.getLast();
        }
        return (PropertyEvent) m_setEvents.get(getKey(obj, prop));
    }

    public PropertyEvent getLastEvent(Object obj, Property prop,
                                         Object arg) {
        if (!prop.isCollection()) { throw new IllegalArgumentException(); }

        List events = (List) m_collectionEvents.get(getKey(obj, prop));

        if (events == null) { return null; }

        for (int i = events.size() - 1; i >= 0; i--) {
            PropertyEvent old = (PropertyEvent) events.get(i);
            if (old.getArgument().equals(arg)) { return old; }
        }

        return null;
    }

    public PropertyEvent getLastEvent(PropertyEvent pe) {
        if (pe.getProperty().isCollection()) {
            return getLastEvent
                (pe.getObject(), pe.getProperty(), pe.getArgument());
        } else {
            return getLastEvent(pe.getObject(), pe.getProperty());
        }
    }

    public Collection getCurrentEvents(Object obj, Property prop) {
        if (!prop.isCollection()) { throw new IllegalArgumentException(); }

        List evs = (List) m_collectionEvents.get(getKey(obj, prop));
        if (evs == null) { return Collections.EMPTY_LIST; }
        return evs;
    }

    public Collection getReachablePropertyEvents(Object obj) {
        ArrayList result = new ArrayList();

        ObjectType ot = Session.getObjectType(obj);
        for (Iterator it = ot.getProperties().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            if (prop.isCollection()) {
                result.addAll(getCurrentEvents(obj, prop));
            } else {
                Event e = getLastEvent(obj, prop);
                if (e != null) {
                    result.add(e);
                }
            }
        }

        return result;
    }

}
