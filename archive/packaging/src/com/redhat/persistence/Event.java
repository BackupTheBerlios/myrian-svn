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

package com.redhat.persistence;

import com.redhat.persistence.metadata.ObjectMap;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Event
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 **/

public abstract class Event {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/Event.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

    private static final Logger LOG = Logger.getLogger(Event.class);

    public static abstract class Switch {

        public abstract void onCreate(CreateEvent e);

        public abstract void onDelete(DeleteEvent e);

        public abstract void onSet(SetEvent e);

        public abstract void onAdd(AddEvent e);

        public abstract void onRemove(RemoveEvent e);

    }

    private Session m_ssn;
    private Object m_obj;

    private List m_dependentEvents = new ArrayList();

    // used by Session for flushing
    boolean m_flushable = false;

    Event(Session ssn, Object obj) {
        m_ssn = ssn;
        m_obj = obj;
        if (m_obj == null) {
            throw new NullException(ProtoException.OBJECT);
        }
    }

    public final Session getSession() {
        return m_ssn;
    }

    public final Object getObject() {
        ObjectData od = getObjectData();
        if (od == null) {
            return m_obj;
        } else {
            return getObjectData().getObject();
        }
    }

    public final ObjectMap getObjectMap() {
        return m_ssn.getObjectMap(m_obj);
    }

    public abstract void dispatch(Switch sw);

    final void addDependent(Event dependent) {
        m_dependentEvents.add(dependent);
    }

    final Iterator getDependentEvents() {
        return m_dependentEvents.iterator();
    }

    abstract ObjectData getObjectData();

    abstract void prepare();

    abstract void activate();

    abstract void sync();

    void dump() {
        PrintWriter pw = new PrintWriter(System.out);
        dump(pw);
        pw.flush();
    }

    abstract void dump(PrintWriter out);

    abstract String getName();

    final void log() {
        if (LOG.isDebugEnabled()) {
            StringBuffer msg = new StringBuffer();

            int level = m_ssn.getLevel();
            for (int i = 0; i < level + 1; i++) {
                msg.append("  ");
            }

            msg.append(this);
            LOG.debug(msg.toString());
        }
    }

}
