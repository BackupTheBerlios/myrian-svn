package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.*;
import java.util.*;
import java.io.*;

/**
 * PropertyData
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #13 $ $Date: 2003/03/27 $
 **/

class PropertyData {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PropertyData.java#13 $ by $Author: rhs $, $DateTime: 2003/03/27 15:13:02 $";

    final private ObjectData m_odata;
    final private Property m_prop;
    private Object m_value;
    final private List m_dependentEvents = new LinkedList();

    public PropertyData(ObjectData odata, Property prop, Object value) {
        m_odata = odata;
        m_prop = prop;
        m_value = value;

        m_odata.addPropertyData(m_prop, this);
    }

    public Session getSession() {
        return m_odata.getSession();
    }

    private Object getObject() { return m_odata.getObject(); }

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
            PropertyEvent ev = getSession().getEventStream().getLastEvent
                (getObject(), getProperty());
            if (ev == null) { return m_value; }

            return ev.getArgument();
        } else {
            return m_value;
        }
    }

    public boolean isFlushed() {
        if (m_prop.isCollection()) {
            Collection evs = getSession().getEventStream().getCurrentEvents
                (getObject(), getProperty());
            return evs.size() == 0;
        } else {
            return getSession().getEventStream().getLastEvent
                (getObject(), getProperty()) == null;
        }
    }

    void addNotNullDependent(Event ev) {
        getSession().addViolation(this);
        m_dependentEvents.add(ev);
    }

    Iterator getDependentEvents() { return m_dependentEvents.iterator(); }

    void transferNotNullDependentEvents(Event ev) {
        for (Iterator it = m_dependentEvents.iterator(); it.hasNext(); ) {
            ev.addDependent((Event) it.next());
        }

        m_dependentEvents.clear();
        getSession().removeViolation(this);
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
        if (m_prop.isCollection()) {
	    boolean first = true;
            for (Iterator it = getSession().getEventStream().
                     getCurrentEvents(getObject(), getProperty()).iterator();
                 it.hasNext(); ) {
		if (first) {
		    first = false;
		    out.println("    Current Property Events:");
		}
                ((Event) it.next()).dump(out);
            }
        } else {
            Event ev = getSession().getEventStream().getLastEvent
                (getObject(), getProperty());
            if (ev != null) {
		out.println("    Current Property Events:");
		ev.dump(out);
	    }
        }
    }

    public String toString() {
	return "<pdata " + getObject() + "." + getProperty() + ">";
    }

}
