package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.*;
import java.util.*;
import java.io.*;

/**
 * PropertyData
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2003/02/13 $
 **/

class PropertyData {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PropertyData.java#6 $ by $Author: ashah $, $DateTime: 2003/02/13 15:47:05 $";

    private ObjectData m_odata;
    private Property m_prop;
    private Object m_value;
    private ArrayList m_events = new ArrayList();

    public PropertyData(ObjectData odata, Property prop, Object value) {
        m_odata = odata;
        m_prop = prop;
        m_value = value;

        m_odata.m_pdata.put(m_prop, this);
    }

    public Session getSession() {
        return m_odata.getSession();
    }

    public Property getProperty() {
        return m_prop;
    }

    public void setValue(Object value) {
        m_value = value;
    }

    public Object getValue() {
        return m_value;
    }

    public Object get() {
        if (!m_prop.isCollection()) {
            for (int i = m_events.size() - 1; i >= 0; i--) {
                PropertyEvent ev = (PropertyEvent) m_events.get(i);
                if (ev instanceof SetEvent) {
                    return ev.getArgument();
                }
            }
        }

        return m_value;
    }

    public void addEvent(PropertyEvent ev) {
        ev.setPropertyData(this);
        m_events.add(ev);
    }

    public void removeEvent(PropertyEvent ev) {
        m_events.remove(ev);
    }

    public List getEvents() {
        return m_events;
    }

    public boolean isModified() {
        return m_events.size() > 0;
    }

    void dump() {
        PrintWriter pw = new PrintWriter(System.out);
        dump(pw);
        pw.flush();
    }

    void dump(PrintWriter out) {
        out.print("    ");
        out.print(m_prop.getName());
        out.print(" = ");
        out.println(m_value);
        out.println("    Property Events:");
        for (Iterator it = m_events.iterator(); it.hasNext(); ) {
            ((Event) it.next()).dump(out);
        }
    }

}
