package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * Event
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/11/27 $
 **/

public abstract class Event {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/Event.java#2 $ by $Author: rhs $, $DateTime: 2002/11/27 17:41:53 $";

    private Session m_ssn;
    private OID m_oid;

    Event m_next;

    protected Event(Session ssn, OID oid) {
        m_ssn = ssn;
        m_oid = oid;

        if (m_ssn.m_head == null) {
            m_ssn.m_head = this;
        }

        if (m_ssn.m_tail == null) {
            m_ssn.m_tail = this;
        } else {
            m_ssn.m_tail.m_next = this;
            m_ssn.m_tail = this;
        }
    }

    public Session getSession() {
        return m_ssn;
    }

    public OID getOID() {
        return m_oid;
    }

    abstract void dump(PrintWriter out);

    abstract String getName();

}
