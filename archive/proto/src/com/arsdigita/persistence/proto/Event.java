package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * Event
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/04 $
 **/

public abstract class Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Event.java#2 $ by $Author: rhs $, $DateTime: 2002/12/04 19:18:22 $";

    private Session m_ssn;
    private OID m_oid;
    private Event m_next;

    protected Event(Session ssn, OID oid) {
        m_ssn = ssn;
        m_oid = oid;
    }

    public Session getSession() {
        return m_ssn;
    }

    public OID getOID() {
        return m_oid;
    }

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

}
