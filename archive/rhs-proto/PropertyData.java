package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.OID;
import java.util.*;
import java.io.*;

/**
 * PropertyData
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/11/27 $
 **/

class PropertyData {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/PropertyData.java#2 $ by $Author: rhs $, $DateTime: 2002/11/27 17:41:53 $";

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

    public OID getOID() {
        return m_odata.getOID();
    }

    public Property getProperty() {
        return m_prop;
    }

    public void setValue(Object value) {
        m_value = value;
    }

    public Object getValue() {
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
        m_events.add(ev);
    }

    public List getEvents() {
        return m_events;
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
