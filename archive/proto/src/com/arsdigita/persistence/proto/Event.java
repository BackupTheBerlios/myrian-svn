package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.ObjectMap;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * Event
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #9 $ $Date: 2003/02/17 $
 **/

public abstract class Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Event.java#9 $ by $Author: rhs $, $DateTime: 2003/02/17 13:30:53 $";

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

    Event(Session ssn, Object obj) {
        m_ssn = ssn;
        m_obj = obj;
    }

    public Session getSession() {
        return m_ssn;
    }

    public Object getObject() {
        return m_obj;
    }

    public ObjectMap getObjectMap() {
        return m_ssn.getObjectMap(m_obj);
    }

    public abstract void dispatch(Switch sw);

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
