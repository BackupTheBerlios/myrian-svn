package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;


/**
 * Aggregator
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/07/07 $
 **/

class Aggregator extends Event.Switch {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/engine/rdbms/Aggregator.java#2 $ by $Author: vadim $, $DateTime: 2003/07/07 12:16:50 $";

    private ArrayList m_nodes = new ArrayList();
    private HashMap m_events = new HashMap();
    private HashMap m_violations = new HashMap();
    private HashMap m_props = new HashMap();
    private HashMap m_attributeNodes = new HashMap();

    public Collection getNodes() {
        return m_nodes;
    }

    private Event getEvent(Object key) {
        return (Event) m_events.get(key);
    }

    private void setEvent(Object key, Event ev) {
        m_events.put(key, ev);
    }

    private Collection getEvents(Object key) {
        return getEvents(key, false);
    }

    private Collection getEvents(Object key, boolean create) {
        key = new CompoundKey(Collection.class, key);
        Collection result = (Collection) m_events.get(key);
        if (result == null && create) {
            result = new ArrayList();
            m_events.put(key, result);
        }
        return result;
    }

    private void addEvent(Object key, Event ev) {
        Collection events = getEvents(key, true);
        if (!events.contains(ev)) {
            events.add(ev);
        }
    }

    private Event getObjectEvent(Object obj) {
        return getEvent(obj);
    }

    private void setObjectEvent(Object obj, Event ev) {
        setEvent(obj, ev);
    }

    private Event getPropertyEvent(Object obj, Property prop) {
        return getEvent(new CompoundKey(obj, prop));
    }

    private void setPropertyEvent(Object obj, Property prop, Event ev) {
        setEvent(new CompoundKey(obj, prop), ev);
    }

    private Event getPropertyEvent(Object obj, Property prop, Object arg) {
        return getEvent(new CompoundKey(obj, new CompoundKey(prop, arg)));
    }

    private void setPropertyEvent(Object obj, Property prop, Object arg,
                                  Event ev) {
        setEvent(new CompoundKey(obj, new CompoundKey(prop, arg)), ev);
    }

    private void addDependingEvent(Object obj, Event ev) {
        addEvent(obj, ev);
    }

    private Collection getDependingEvents(Object obj) {
        return getEvents(obj);
    }

    private Node merge(Node to, Node from) {
        if (!m_nodes.contains(to)) {
            throw new IllegalArgumentException
                ("merging into nonexistent node");
        }
        to.merge(from);
        m_nodes.remove(from);
        return to;
    }

    private Node getViolation(Object obj, Property prop) {
        return (Node) m_violations.get(new CompoundKey(obj, prop));
    }

    private void setViolation(Object obj, Property prop, Node nd) {
        m_violations.put(new CompoundKey(obj, prop), nd);
    }

    private void clearViolation(Object obj, Property prop) {
        m_violations.remove(new CompoundKey(obj, prop));
    }

    private void setPropertyNode(Object obj, Property prop, Object arg,
                                 Node nd) {
        m_props.put(new CompoundKey(obj, new CompoundKey(prop, arg)), nd);
    }

    private Node getPropertyNode(Object obj, Property prop, Object arg) {
        return (Node) m_props.get
            (new CompoundKey(obj, new CompoundKey(prop, arg)));
    }

    private void clearPropertyNode(Object obj, Property prop, Object arg) {
        m_props.remove(new CompoundKey(obj, new CompoundKey(prop, arg)));
    }

    private void setAttributeNode(Object obj, Node nd) {
        m_attributeNodes.put(obj, nd);
    }

    private Node getAttributeNode(Object obj) {
        return (Node) m_attributeNodes.get(obj);
    }

    private void clearAttributeNode(Object obj) {
        m_attributeNodes.remove(obj);
    }


    /**
     * Set up dependencies.
     **/

    private Node onEvent(Event e) {
        Node nd = new Node();
        nd.addEvent(e);
        m_nodes.add(nd);
        nd.addDependency(getObjectEvent(e.getObject()));
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
                setViolation(obj, role, nd);
            }
        }

        setAttributeNode(obj, nd);
    }

    public void onDelete(DeleteEvent e) {
        Node nd = onObjectEvent(e);
        Object obj = e.getObject();
        nd.addDependencies(getDependingEvents(obj));

        Collection roles = e.getObjectMap().getObjectType().getRoles();
        for (Iterator it = roles.iterator(); it.hasNext(); ) {
            Role role = (Role) it.next();
            Node vile = getViolation(obj, role);
            if (vile != null) {
                merge(nd, vile);
                clearViolation(obj, role);
            }
        }

        Node attrNd = getAttributeNode(obj);
        if (attrNd != null) {
            merge(nd, attrNd);
        }

        clearAttributeNode(obj);
    }

    private Node onPropertyEvent(PropertyEvent e) {
        Node nd = onEvent(e);

        Object arg = e.getArgument();
        nd.addDependency(getObjectEvent(arg));

        Object obj = e.getObject();
        Property prop = e.getProperty();
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

    private Node mergeTwoWay(Node nd, Object obj, Property prop, Object arg) {
        Role role = (Role) prop;
        if (!role.isReversable()) { return nd; }
        Role rev = role.getReverse();
        Node prev = getPropertyNode(arg, rev, obj);
        if (prev != null) {
            merge(prev, nd);
            clearPropertyNode(arg, rev, obj);
            return prev;
        } else {
            return nd;
        }
    }

    public void onSet(SetEvent e) {
        Node nd = onPropertyEvent(e);

        Object prev = e.getPreviousValue();
        nd.addDependency(getObjectEvent(prev));
        if (prev != null) {
            addDependingEvent(prev, e);
        }

        Object obj = e.getObject();
        Property prop = e.getProperty();
        Object arg = e.getArgument();

        if (prev != null) {
            nd = mergeTwoWay(nd, obj, prop, prev);
        }
        if (arg != null) {
            nd = mergeTwoWay(nd, obj, prop, arg);
        }

        Node vile = getViolation(obj, prop);
        if (vile != null) {
            nd = merge(vile, nd);
            if (arg != null) {
                clearViolation(obj, prop);
            }
        }

        if (!prop.getType().isKeyed()) {
            Node attrNd = getAttributeNode(obj);
            if (attrNd == null) {
                setAttributeNode(obj, nd);
            } else if (attrNd != nd) {
                nd = merge(attrNd, nd);
            }
        }

        if (!prop.isNullable() && arg == null) {
            setViolation(obj, prop, nd);
        }

        if (prev != null) {
            setPropertyNode(obj, prop, prev, nd);
        }
        if (arg != null) {
            setPropertyNode(obj, prop, arg, nd);
        }
    }

    public void onAdd(AddEvent e) {
        Node nd = onPropertyEvent(e);
        nd = mergeTwoWay(nd, e.getObject(), e.getProperty(), e.getArgument());
        setPropertyNode(e.getObject(), e.getProperty(), e.getArgument(), nd);
    }

    public void onRemove(RemoveEvent e) {
        Node nd = onPropertyEvent(e);
        nd = mergeTwoWay(nd, e.getObject(), e.getProperty(), e.getArgument());
        setPropertyNode(e.getObject(), e.getProperty(), e.getArgument(), nd);
    }

    public void clear() {
        m_nodes.clear();
        m_events.clear();
        m_violations.clear();
        m_props.clear();
        m_attributeNodes.clear();
    }

}
