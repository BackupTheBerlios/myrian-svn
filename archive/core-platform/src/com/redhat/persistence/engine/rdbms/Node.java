package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.*;

import java.util.*;

/**
 * Node
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

class Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/Node.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    private HashSet m_dependencies = new HashSet();
    private ArrayList m_events = new ArrayList();

    public void addEvent(Event ev) {
        if (m_events.contains(ev)) { return; }
        m_events.add(ev);
    }

    public Collection getEvents() {
        return m_events;
    }

    public void addEvents(Collection c) {
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            addEvent((Event) it.next());
        }
    }

    public void addDependency(Event ev) {
        if (ev == null) { return; }
        if (m_dependencies.contains(ev) || m_events.contains(ev)) {
            return;
        }
        m_dependencies.add(ev);
    }

    public Collection getDependencies() {
        return m_dependencies;
    }

    public void addDependencies(Collection c) {
        if (c == null) { return; }
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            addDependency((Event) it.next());
        }
    }

    private boolean containsAny(Collection c, Collection candidates) {
        for (Iterator it = candidates.iterator(); it.hasNext(); ) {
            if (c.contains(it.next())) { return true; }
        }
        return false;
    }

    public void merge(Node from) {
        if (from == this) {
            throw new IllegalArgumentException
                ("cannot merge a node with itself");
        }
        if (containsAny(m_events, from.getDependencies())) {
            addEvents(from.getEvents());
        } else {
            ArrayList evs = m_events;
            m_events = new ArrayList(from.m_events);
            addEvents(evs);
        }
        m_dependencies.addAll(from.getDependencies());
        m_dependencies.removeAll(m_events);
    }

    public String toString() {
        return "<node events: "  + m_events + "\n        deps: " +
            m_dependencies + ">";
    }

}
