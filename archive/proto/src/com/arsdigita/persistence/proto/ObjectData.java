package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.*;
import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;

/**
 * ObjectData
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #12 $ $Date: 2003/02/19 $
 **/

class ObjectData {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ObjectData.java#12 $ by $Author: ashah $, $DateTime: 2003/02/19 20:50:58 $";

    private static final Logger LOG = Logger.getLogger(ObjectData.class);

    private final Session m_ssn;
    private final Object m_object;
    private final ArrayList m_events = new ArrayList();

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

    private int m_violationCount = -1;

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

    int getViolationCount() { return m_violationCount; }

    void setViolationCount(int i) { m_violationCount = i; }

    boolean isFlushable() {
        if (getState().equals(DEAD)) {
            throw new IllegalStateException();
        } else if (getState().equals(SENILE)) {
            return true;
        } else if (m_violationCount == -1) {
            throw new IllegalStateException(m_events.toString());
        } else if (m_violationCount == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void addEvent(ObjectEvent ev) {
        m_events.add(ev);
    }

    void removeEvent(ObjectEvent ev) {
        m_events.remove(ev);
    }

    public boolean isNew() { return m_startedNew; }

    public boolean isDeleted() {
        if (isDead() || isSenile()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isModified() {
        if (isNubile()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isInfantile() { return m_state.equals(INFANTILE); }

    public boolean isNubile() { return m_state.equals(NUBILE); }

    public boolean isAgile() { return m_state.equals(AGILE); }

    public boolean isSenile() { return m_state.equals(SENILE); }

    public boolean isDead() { return m_state.equals(DEAD); }

    public void setState(State state) {
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
