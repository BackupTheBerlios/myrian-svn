package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.*;
import java.util.*;
import java.io.*;

/**
 * ObjectData
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/01/10 $
 **/

class ObjectData {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ObjectData.java#5 $ by $Author: ashah $, $DateTime: 2003/01/10 19:25:47 $";

    private Session m_ssn;
    private PersistentObject m_object;
    private ArrayList m_events = new ArrayList();
    private boolean m_isVisiting = false;

    HashMap m_pdata = new HashMap();

    public ObjectData(Session ssn, PersistentObject object) {
        m_ssn = ssn;
        m_object = object;

        m_ssn.addObjectData(this);
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

    public void removeEvent(ObjectEvent ev) {
        m_events.remove(ev);
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

    public boolean isModified() {
        if (m_events.size() > 0) { return true; }

        for (Iterator it = m_pdata.values().iterator(); it.hasNext(); ) {
            PropertyData pdata = (PropertyData) it.next();
            if (pdata.isModified()) { return true; }
        }

        return false;
    }

    public boolean isVisiting() {
        return m_isVisiting;
    }

    public void setVisiting(boolean value) {
        m_isVisiting = value;
    }

    void dump() {
        PrintWriter pw = new PrintWriter(System.out);
        dump(pw);
        pw.flush();
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
