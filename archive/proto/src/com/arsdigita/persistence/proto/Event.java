package com.arsdigita.persistence.proto;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * Event
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/01/02 $
 **/

public abstract class Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Event.java#5 $ by $Author: rhs $, $DateTime: 2003/01/02 15:38:03 $";

    private static final Logger LOG = Logger.getLogger(Event.class);

    private Session m_ssn;
    private OID m_oid;
    private Event m_next;

    Event(Session ssn, OID oid) {
        m_ssn = ssn;
        m_oid = oid;
    }

    public Session getSession() {
        return m_ssn;
    }

    public OID getOID() {
        return m_oid;
    }

    abstract void fire(EventHandler handler);

    abstract void sync();

    Event getNext() {
        return m_next;
    }

    boolean isHead() {
        return m_ssn.m_head == this;
    }

    private void setHead() {
        m_ssn.m_head = this;
    }

    boolean isTail() {
        return m_ssn.m_tail == this;
    }

    private void setTail() {
        m_ssn.m_tail = this;
    }

    private static final void link(Event from, Event to) {
        if (from != null) {
            from.m_next = to;

            if (from.isTail()) {
                if (to != null) {
                    to.setTail();
                }
            }
        }

        if (to != null && to.isHead()) {
            if (from != null) {
                from.setHead();
            }
        }
    }

    void insert(Event ev) {
        if (ev == null) {
            throw new IllegalArgumentException
                ("Cannot insert a null event into the event stream.");
        }
        if (ev.m_next != null) {
            throw new IllegalArgumentException
                ("Cannot insert a linked event into the event stream.");
        }

        Event tmp = m_next;
        link(this, ev);
        link(ev, tmp);
    }

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
