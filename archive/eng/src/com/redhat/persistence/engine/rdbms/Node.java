/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
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
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

class Node {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/engine/rdbms/Node.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
