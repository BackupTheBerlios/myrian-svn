package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.*;
import java.util.*;
import java.io.*;

/**
 * ObjectData
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2003/02/17 $
 **/

class ObjectData {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ObjectData.java#10 $ by $Author: rhs $, $DateTime: 2003/02/17 20:13:29 $";

    private Session m_ssn;
    private Object m_object;
    private ArrayList m_events = new ArrayList();

    public static class State {
        private String m_name;

        private State(String name) { m_name = name; }

        public String toString() { return m_name; }
    };

    public static final State INFANTILE = new State("infantile");
    public static final State NUBILE = new State("nubile");
    public static final State AGILE = new State("agile");
    public static final State SENILE = new State("senile");
    public static final State DEAD = new State("dead");

    private State m_state;

    HashMap m_pdata = new HashMap();

    public ObjectData(Session ssn, Object object, State state) {
        m_ssn = ssn;
        m_object = object;
        m_state = state;

        m_ssn.addObjectData(this);
    }

    public Session getSession() {
        return m_ssn;
    }

    public Object getObject() {
        return m_object;
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

    void removeEvent(ObjectEvent ev) {
        m_events.remove(ev);
    }

    public boolean isNew() {
        boolean evNew = false;
        
        for (int i = 0; i < m_events.size(); i++) {
            Event ev = (Event) m_events.get(i);
            if (ev instanceof CreateEvent) {
                evNew = true;
                break;
            }
        }

        if (evNew && isAgile()) {
            throw new IllegalStateException("events and state out of sync");
        }

        return evNew;
    }

    public boolean isDeleted() {
        boolean evDel = false;
        for (int i = m_events.size() - 1; i >= 0; i--) {
            Event ev = (Event) m_events.get(i);
            if (ev instanceof DeleteEvent) {
                evDel = true;
                break;
            } else if (ev instanceof CreateEvent) {
                evDel = false;
                break;
            }
        }

        // del iff senile
        // if ((evDel && !isSenile()) || (!evDel && isSenile())) {
        // throw new IllegalStateException("events and state out of sync");
        // }

        return evDel;
    }

    public boolean isModified() {
        boolean evMod = false;

        if (m_events.size() > 0) {
            evMod = true;
        } else {
            for (Iterator it = m_pdata.values().iterator(); it.hasNext(); ) {
                PropertyData pdata = (PropertyData) it.next();
                if (pdata.isModified()) {
                    evMod = true;
                    break;
                }
            }
        }

        // nubile -> modified
        // senile -> modified
        if ((isNubile() || isSenile()) && !evMod) {
            throw new IllegalStateException("events and state out of sync");
        }

        return evMod;
    }

    public boolean isInfantile() { return m_state.equals(INFANTILE); }

    public boolean isNubile() { return m_state.equals(NUBILE); }

    public boolean isAgile() { return m_state.equals(AGILE); }

    public boolean isSenile() { return m_state.equals(SENILE); }

    public boolean isDead() { return m_state.equals(DEAD); }

    public void setState(State state) { m_state = state; }

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
