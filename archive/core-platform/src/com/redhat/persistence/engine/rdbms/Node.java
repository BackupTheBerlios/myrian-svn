/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.Event;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Node
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/04/07 $
 **/

class Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/Node.java#5 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

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
