package com.redhat.persistence;

import com.redhat.persistence.metadata.ObjectMap;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Event
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public abstract class Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/Event.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

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
