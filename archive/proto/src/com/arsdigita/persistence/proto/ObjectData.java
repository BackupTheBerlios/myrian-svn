package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.*;
import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;

/**
 * ObjectData
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #13 $ $Date: 2003/02/27 $
 **/

class ObjectData {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ObjectData.java#13 $ by $Author: ashah $, $DateTime: 2003/02/27 21:02:33 $";

    private static final Logger LOG = Logger.getLogger(ObjectData.class);

    private final Session m_ssn;
    private final Object m_object;
    private final LinkedList m_events = new LinkedList();

    static class State {
        private String m_name;

        private State(String name) { m_name = name; }

        public String toString() { return m_name; }
    };

    public static final State INFANTILE = new State("infantile");
    public static final State NUBILE = new State("nubile");
    public static final State AGILE = new State("agile");
    public static final State SENILE = new State("senile");
    public static final State DEAD = new State("dead");

    private boolean m_startedNew = false;

    private State m_state;

    private HashMap m_pdata = new HashMap();

    public ObjectData(Session ssn, Object object, State state) {
        m_ssn = ssn;
        m_object = object;
        setState(state);

        m_ssn.addObjectData(this);
    }

    public Session getSession() {
        return m_ssn;
    }

    public Object getObject() {
        return m_object;
    }

    public void addPropertyData(Property p, PropertyData pd) {
        m_pdata.put(p, pd);
    }

    public PropertyData getPropertyData(Property prop) {
        return (PropertyData) m_pdata.get(prop);
    }

    public boolean hasPropertyData(Property prop) {
        return m_pdata.containsKey(prop);
    }

    void invalidatePropertyData() {
        for (Iterator it = m_pdata.values().iterator(); it.hasNext(); ) {
            ((PropertyData) it.next()).invalidate();
        }
    }

    void addEvent(ObjectEvent ev) {
        m_events.add(ev);
    }

    ObjectEvent getCurrentEvent() {
        if (m_events.size() == 0) { return null; }
        return (ObjectEvent) m_events.getLast();
    }

    void removeEvent(ObjectEvent ev) {
        m_events.remove(ev);
    }

    public boolean isNew() { return m_startedNew; }

    public boolean isDeleted() { return isDead() || isSenile(); }

    public boolean isModified() { return !isNubile(); }

    public boolean isInfantile() { return m_state.equals(INFANTILE); }

    public boolean isNubile() { return m_state.equals(NUBILE); }

    public boolean isAgile() { return m_state.equals(AGILE); }

    public boolean isSenile() { return m_state.equals(SENILE); }

    public boolean isDead() { return m_state.equals(DEAD); }

    void setState(State state) {
        if (state.equals(INFANTILE)) {
            m_startedNew = true;
        }

        m_state = state;
    }

    public State getState() { return m_state; }

    void dump() {
        PrintWriter pw = new PrintWriter(System.out);
        dump(pw);
        pw.flush();
    }

    void dump(PrintWriter out) {
        out.println("    object = " + getObject());
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
