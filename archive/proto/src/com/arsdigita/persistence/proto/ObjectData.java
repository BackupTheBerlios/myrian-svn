package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.OID;
import java.util.*;
import java.io.*;

/**
 * ObjectData
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/12/02 $
 **/

class ObjectData {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ObjectData.java#1 $ by $Author: rhs $, $DateTime: 2002/12/02 12:04:21 $";

    private Session m_ssn;
    private PersistentObject m_object;

    HashMap m_pdata = new HashMap();
    ArrayList m_events = new ArrayList();

    public ObjectData(Session ssn, PersistentObject object) {
        m_ssn = ssn;
        m_object = object;

        m_ssn.m_odata.put(m_object.getOID(), this);
    }

    public Session getSession() {
        return m_ssn;
    }

    public PersistentObject getPersistentObject() {
        return m_object;
    }

    public OID getOID() {
        if (m_object == null) {
            return null;
        } else {
            return m_object.getOID();
        }
    }

    public PropertyData getPropertyData(Property prop) {
        return (PropertyData) m_pdata.get(prop);
    }

    public boolean hasPropertyData(Property prop) {
        return m_pdata.containsKey(prop);
    }

    public void addEvent(ObjectEvent ev) {
        m_events.add(ev);
    }

    public boolean isNew() {
        for (int i = 0; i < m_events.size(); i++) {
            Event ev = (Event) m_events.get(i);
            if (ev instanceof CreateEvent) {
                return true;
            }
        }

        return false;
    }

    public boolean isDeleted() {
        for (int i = m_events.size() - 1; i >= 0; i--) {
            Event ev = (Event) m_events.get(i);
            if (ev instanceof DeleteEvent) {
                return true;
            } else if (ev instanceof CreateEvent) {
                return false;
            }
        }

        return false;
    }

    void dump(PrintWriter out) {
        out.print(getOID());
        out.println(":");
        for (Iterator it = m_pdata.values().iterator(); it.hasNext(); ) {
            PropertyData pdata = (PropertyData) it.next();
            pdata.dump(out);
        }

        out.println("    Object Events:");
        for (Iterator it = m_events.iterator(); it.hasNext(); ) {
            Event ev = (Event) it.next();
            ev.dump(out);
        }
    }

}
