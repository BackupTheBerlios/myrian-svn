package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.*;
import java.util.*;
import java.io.*;

/**
 * PropertyData
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2003/02/19 $
 **/

class PropertyData {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PropertyData.java#8 $ by $Author: ashah $, $DateTime: 2003/02/19 15:49:06 $";

    final private ObjectData m_odata;
    final private Property m_prop;
    private Object m_value;
    final private ArrayList m_events = new ArrayList();
    private PropertyEvent m_current = null;

    public PropertyData(ObjectData odata, Property prop, Object value) {
        m_odata = odata;
        m_prop = prop;
        m_value = value;

        m_odata.addPropertyData(m_prop, this);
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
        if (!m_prop.isCollection() && m_current != null) {
            for (int i = m_events.size() - 1; i >= 0; i--) {
                PropertyEvent ev = (PropertyEvent) m_events.get(i);
                if (ev instanceof SetEvent) {
                    return ev.getArgument();
                }
                if (ev == m_current) { break; }
            }
        }

        return m_value;
    }

    public void addEvent(PropertyEvent ev) {
        if (m_current == null) { m_current = ev; }
        m_events.add(ev);
    }

    public void removeEvent(PropertyEvent ev) {
        if (ev.equals(m_current)) {
            int newIndex = m_events.indexOf(ev) + 1;
            try {
                m_current = (PropertyEvent) m_events.get(newIndex);
            } catch (IndexOutOfBoundsException ex) {
                m_current = null;
            }
        }
        m_events.remove(ev);
    }

    void invalidate() {
        m_current = null;
        if (!m_prop.isCollection()) {
            m_value = null;
        }
    }

    public boolean isModified() {
        if (m_current == null) { return false; }
        return m_events.size() > m_events.indexOf(m_current);
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
