/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.AddEvent;
import com.redhat.persistence.CreateEvent;
import com.redhat.persistence.DeleteEvent;
import com.redhat.persistence.Event;
import com.redhat.persistence.ObjectEvent;
import com.redhat.persistence.PropertyEvent;
import com.redhat.persistence.RemoveEvent;
import com.redhat.persistence.Session;
import com.redhat.persistence.SetEvent;
import com.redhat.persistence.common.CompoundKey;
import com.redhat.persistence.common.IdentityKey;
import com.redhat.persistence.metadata.Mapping;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * Aggregator
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #9 $ $Date: 2004/09/30 $
 **/

class Aggregator extends Event.Switch {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/engine/rdbms/Aggregator.java#9 $ by $Author: rhs $, $DateTime: 2004/09/30 15:44:52 $";

    private static final Logger LOG = Logger.getLogger(Aggregator.class);

    private static class EventMap extends HashMap {

        public Event getEvent(Object key) {
            return (Event) get(key);
        }

        public void setEvent(Object key, Event ev) {
            put(key, ev);
        }

        public Collection getEvents(Object key) {
            return getEvents(key, false);
        }

        private Collection getEvents(Object key, boolean create) {
            key = new CompoundKey(Collection.class, key);
            Collection result = (Collection) get(key);
            if (result == null && create) {
                result = new ArrayList();
                put(key, result);
            }
            return result;
        }

        private void addEvent(Object key, Event ev) {
            Collection events = getEvents(key, true);
            if (!events.contains(ev)) {
                events.add(ev);
            }
        }

    }

    private RDBMSEngine m_engine;

    private ArrayList m_nodes = new ArrayList();

    // Used to track event dependencies.
    private EventMap m_objects = new EventMap();
    private EventMap m_properties = new EventMap();
    private EventMap m_depending = new EventMap();

    // Used to track required merges.
    private EventMap m_violations = new EventMap();
    private EventMap m_twoWay = new EventMap();
    private EventMap m_attributes = new EventMap();

    public Aggregator(RDBMSEngine engine) {
        m_engine = engine;
    }

    public Collection getNodes() {
        return m_nodes;
    }

    Object key(Object obj) {
        return new IdentityKey(obj);
    }

    private Event getObjectEvent(Object obj) {
        return m_objects.getEvent(key(obj));
    }

    private void setObjectEvent(Object obj, Event ev) {
        m_objects.setEvent(key(obj), ev);
    }

    private Event getPropertyEvent(Object obj, Property prop) {
        return m_properties.getEvent(new CompoundKey(key(obj), prop));
    }

    private void setPropertyEvent(Object obj, Property prop, Event ev) {
        m_properties.setEvent(new CompoundKey(key(obj), prop), ev);
    }

    private Event getPropertyEvent(Object obj, Property prop, Object arg) {
        return m_properties.getEvent
            (new CompoundKey(key(obj), new CompoundKey(prop, key(arg))));
    }

    private void setPropertyEvent(Object obj, Property prop, Object arg,
                                  Event ev) {
        m_properties.setEvent
            (new CompoundKey(key(obj), new CompoundKey(prop, key(arg))), ev);
    }

    private void addObjectEventDependency(Node nd, Object obj) {
        if (m_engine.getSession().isManaged(obj)) {
            nd.addDependency(getObjectEvent(obj));
        }
    }

    private void addDependingEvent(Object obj, Event ev) {
        if (m_engine.getSession().isManaged(obj)) {
            m_depending.addEvent(key(obj), ev);
        }
    }

    private Collection getDependingEvents(Object obj) {
        return m_depending.getEvents(key(obj));
    }

    private Node findNode(Event ev) {
        for (Iterator it = m_nodes.iterator(); it.hasNext(); ) {
            Node nd = (Node) it.next();
            if (nd.getEvents().contains(ev)) {
                return nd;
            }
        }

        throw new IllegalArgumentException
            ("event not in any node: " + ev);
    }

    private Node merge(Node to, Node from, String msg) {
        if (to == from) {
            return to;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Merging[" + msg + "]\n\n" + from + "\n\ninto\n\n" +
                      to + "\n\n");
        }

        if (!m_nodes.contains(to)) {
            throw new IllegalArgumentException
                ("merging into nonexistent node\nto = " + to +
                 "\nfrom = " + from);
        }

        if (!m_nodes.contains(from)) {
            throw new IllegalArgumentException
                ("merging from nonexistent node\nto = " + to +
                 "\n from = " + from);
        }

        to.merge(from);
        m_nodes.remove(from);

        return to;
    }

    private Event getViolation(Object obj, Property prop) {
        return m_violations.getEvent(new CompoundKey(key(obj), prop));
    }

    private void setViolation(Object obj, Property prop, Event ev) {
        m_violations.setEvent(new CompoundKey(key(obj), prop), ev);
    }

    private void clearViolation(Object obj, Property prop) {
        m_violations.remove(new CompoundKey(key(obj), prop));
    }

    private Event getViolation(Object obj) {
        return m_violations.getEvent(key(obj));
    }

    private void setViolation(Object obj, Event ev) {
        m_violations.setEvent(key(obj), ev);
    }

    private void clearViolation(Object obj) {
        m_violations.remove(key(obj));
    }

    private void setTwoWayEvent(Object obj, Property prop, Object arg,
                                 Event ev) {
        Role role = (Role) prop;
        if (!role.isReversable()) { return; }
        m_twoWay.setEvent
            (new CompoundKey(key(obj), new CompoundKey(prop, key(arg))), ev);
    }

    private Event getTwoWayEvent(Object obj, Property prop, Object arg) {
        return m_twoWay.getEvent
            (new CompoundKey(key(obj), new CompoundKey(prop, key(arg))));
    }

    private void setAttributeEvent(Object obj, Event ev) {
        m_attributes.setEvent(key(obj), ev);
    }

    private Event getAttributeEvent(Object obj) {
        return m_attributes.getEvent(key(obj));
    }

    private void clearAttributeEvent(Object obj) {
        m_attributes.remove(key(obj));
    }


    /**
     * Set up dependencies.
     **/

    private Node onEvent(Event e) {
        Node nd = new Node();
        nd.addEvent(e);
        m_nodes.add(nd);
        addObjectEventDependency(nd, e.getObject());
        return nd;
    }

    private Node onObjectEvent(ObjectEvent e) {
        Node nd = onEvent(e);
        setObjectEvent(e.getObject(), e);
        return nd;
    }

    public void onCreate(CreateEvent e) {
        Node nd = onObjectEvent(e);

        Object obj = e.getObject();

        Collection roles = e.getObjectMap().getObjectType().getRoles();
        for (Iterator it = roles.iterator(); it.hasNext(); ) {
            Role role = (Role) it.next();
            if (!role.isNullable()) {
                setViolation(obj, role, e);
            }
        }

        setAttributeEvent(obj, e);

        if (e.getObjectMap().isNested()) {
            setViolation(obj, e);
        }
    }

    public void onDelete(DeleteEvent e) {
        Node nd = onObjectEvent(e);
        Object obj = e.getObject();
        nd.addDependencies(getDependingEvents(obj));

        Collection roles = e.getObjectMap().getObjectType().getRoles();
        for (Iterator it = roles.iterator(); it.hasNext(); ) {
            Role role = (Role) it.next();
            Event vile = getViolation(obj, role);
            if (vile != null) {
                nd = merge(nd, findNode(vile), "violations from delete");
                clearViolation(obj, role);
            }
        }

        Event attr = getAttributeEvent(obj);
        if (attr != null) {
            nd = merge(nd, findNode(attr), "attributes from delete");
        }

        clearAttributeEvent(obj);

        Event ovile = getViolation(obj);
        if (ovile != null) {
            nd = merge(nd, findNode(ovile),
                       "violation from nested type (delete)");
        }

        clearViolation(obj);
    }

    private Node onPropertyEvent(PropertyEvent e) {
        Node nd = onEvent(e);

        Object obj = e.getObject();
        Property prop = e.getProperty();
        Object arg = e.getArgument();

        addObjectEventDependency(nd, arg);

        Mapping m = e.getObjectMap().getMapping(prop);
        if (arg != null && m.isNested() && m.isCompound()) {
            Event ovile = getViolation(arg);
            if (ovile != null) {
                nd = merge(findNode(ovile), nd,
                           "violation from nested type (add)");
                clearViolation(arg);
            }
        }

        Event prev;
        if (prop.isCollection()) {
            prev = getPropertyEvent(obj, prop, arg);
        } else {
            prev = getPropertyEvent(obj, prop);
        }
        nd.addDependency(prev);

        if (prop.isCollection()) {
            setPropertyEvent(obj, prop, arg, e);
        } else {
            setPropertyEvent(obj, prop, e);
        }

        addDependingEvent(obj, e);
        if (arg != null) {
            addDependingEvent(arg, e);
        }

        return nd;
    }

    private Node mergeTwoWay(Node nd, Object obj, Property prop, Object arg,
                             String msg) {
        Role role = (Role) prop;
        if (!role.isReversable()) { return nd; }
        Role rev = role.getReverse();

        Node result = nd;

        Event ev = getTwoWayEvent(obj, prop, arg);
        if (ev != null) {
            result = merge(findNode(ev), result, msg + " (forward)");
        }

        ev = getTwoWayEvent(arg, rev, obj);
        if (ev != null) {
            result = merge(findNode(ev), result, msg + " (reverse)");
        }

        return result;
    }

    public void onSet(SetEvent e) {
        Node nd = onPropertyEvent(e);

        Object prev = e.getPreviousValue();
        addObjectEventDependency(nd, prev);
        if (prev != null) {
            addDependingEvent(prev, e);
        }

        Object obj = e.getObject();
        Property prop = e.getProperty();
        Object arg = e.getArgument();
        Mapping mapping = e.getObjectMap().getMapping(prop);

        if (prev != null) {
            nd = mergeTwoWay(nd, obj, prop, prev,
                             "two way previous value from set");
        }
        if (arg != null) {
            nd = mergeTwoWay(nd, obj, prop, arg, "two way arg from set");
        }

        Event vile = getViolation(obj, prop);
        if (vile != null) {
            nd = merge(findNode(vile), nd, "violation from set");
            if (arg != null) {
                clearViolation(obj, prop);
            }
        }

        if (!prop.getType().isKeyed()) {
            Event attr = getAttributeEvent(obj);
            if (attr == null) {
                setAttributeEvent(obj, e);
            } else {
                nd = merge(findNode(attr), nd, "attributes from set");
            }
        }

        if (!prop.isNullable() && arg == null) {
            setViolation(obj, prop, e);
        }

        if (prev != null) {
            setTwoWayEvent(obj, prop, prev, e);
        }
        if (arg != null) {
            setTwoWayEvent(obj, prop, arg, e);
        }

        if (prev != null && mapping.isNested() && mapping.isCompound()) {
            setViolation(prev, e);
        }
    }

    public void onAdd(AddEvent e) {
        Node nd = onPropertyEvent(e);
        nd = mergeTwoWay(nd, e.getObject(), e.getProperty(), e.getArgument(),
                         "two way from add");
        setTwoWayEvent(e.getObject(), e.getProperty(), e.getArgument(), e);
    }

    public void onRemove(RemoveEvent e) {
        Node nd = onPropertyEvent(e);
        nd = mergeTwoWay(nd, e.getObject(), e.getProperty(), e.getArgument(),
                         "two way from remove");
        setTwoWayEvent(e.getObject(), e.getProperty(), e.getArgument(), e);

        Mapping m = e.getObjectMap().getMapping(e.getProperty());
        if (m.isNested() && m.isCompound()) {
            setViolation(e.getArgument(), e);
        }
    }

    public void clear() {
        m_nodes.clear();
        m_objects.clear();
        m_properties.clear();
        m_depending.clear();
        m_violations.clear();
        m_twoWay.clear();
        m_attributes.clear();
    }

}
