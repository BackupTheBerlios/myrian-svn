/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.redhat.persistence;

import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * ObjectData
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #12 $ $Date: 2004/09/28 $
 **/

class ObjectData implements Violation {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/ObjectData.java#12 $ by $Author: ashah $, $DateTime: 2004/09/28 10:52:16 $";

    private static final Logger LOG = Logger.getLogger(ObjectData.class);

    private final Session m_ssn;
    private WeakReference m_object;
    private ObjectData m_container;
    private ObjectMap m_map;
    private Object m_key = null;

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
    public static final State UNKNOWN = new State("unknown");
    public static final State NONE = new State("none");

    private boolean m_startedNew = false;

    private State m_state;

    private HashMap m_pdata = new HashMap();

    public ObjectData(Session ssn, Object object, State state) {
        m_ssn = ssn;
        m_container = null;
        m_map = null;
        setObject(object);
        setState(state);
    }

    public Session getSession() {
        return m_ssn;
    }

    public Object getKey() {
        return m_key;
    }

    void setKey(Object key) {
        m_key = key;
    }

    public Object getObject() {
        Object obj = m_object.get();
        if (obj == null) {
            ObjectType type = getObjectMap().getObjectType();
            PropertyMap pmap = new PropertyMap(type);
            Collection keys = getObjectMap().getKeyProperties();
            for (Iterator it = m_pdata.values().iterator(); it.hasNext(); ) {
                PropertyData pdata = (PropertyData) it.next();
                if (keys.contains(pdata.getProperty())) {
                    pmap.put(pdata.getProperty(), pdata.getValue());
                }
            }

            Session ssn = getSession();
            Adapter ad = ssn.getRoot().getAdapter(type);
            obj = ad.getObject(type, pmap, ssn);
            setObject(obj);
        }

        return obj;
    }

    void setObject(Object object) {
        m_object = new WeakReference(object);
        m_ssn.addObjectData(this);
    }

    public Object getContainer() {
        if (m_container == null) { return null; }
        return m_container.getObject();
    }

    void setContainer(Object container) {
        if (container == null) {
            throw new NullPointerException("container");
        }

        m_container = getSession().getObjectData(container);

        if (m_container == null) {
            throw new IllegalStateException("no objectdata for container");
        }
    }

    public ObjectMap getObjectMap() {
        return m_map;
    }

    void setObjectMap(ObjectMap map) {
        m_map = map;
    }

    public PropertyMap getProperties() {
        PropertyMap result = new PropertyMap(getObjectMap().getObjectType());
        List keys = getObjectMap().getKeyProperties();
        for (int i = 0; i < keys.size(); i++) {
            Property p = (Property) keys.get(i);
            PropertyData pd = getPropertyData(p);
            // for new objects not synced to store getValue returns null
            if (isLoaded() && isInfantile()) {
                result.put(p, pd.get());
            } else {
                result.put(p, pd.getValue());
            }
        }
        return result;
    }

    void clear() {
        if (m_map == null) { return; }
        Collection keys = m_map.getKeyProperties();
        for (Iterator it = m_pdata.values().iterator(); it.hasNext(); ) {
            PropertyData pdata = (PropertyData) it.next();
            if (!keys.contains(pdata.getProperty())) {
                it.remove();
            }
        }

        if (m_pdata.size() != keys.size()) {
            throw new IllegalStateException();
        }

        setState(UNKNOWN);
        m_startedNew = false;
    }

    public void addPropertyData(Property p, PropertyData pd) {
        check();
        m_pdata.put(p, pd);
    }

    public PropertyData getPropertyData(Property prop) {
        return (PropertyData) m_pdata.get(prop);
    }

    public boolean hasPropertyData(Property prop) {
        return m_pdata.containsKey(prop);
    }

    public boolean isNew() { check(); return m_startedNew; }

    public boolean isDeleted() { check(); return isDead() || isSenile(); }

    public boolean isModified() { check(); return !isNubile() && !isNot(); }

    public boolean isInfantile() { check(); return m_state.equals(INFANTILE); }

    public boolean isNubile() { check(); return m_state.equals(NUBILE); }

    public boolean isAgile() { check(); return m_state.equals(AGILE); }

    public boolean isSenile() { check(); return m_state.equals(SENILE); }

    public boolean isDead() { check(); return m_state.equals(DEAD); }

    public boolean isNot() { check(); return m_state.equals(NONE); }

    private void check() {
        if (!isLoaded()) {
            if (getSession().retrieve(getProperties()) == null) {
                setState(NONE);
            }
        }
    }

    public boolean isLoaded() { return !m_state.equals(UNKNOWN); }

    public boolean isFlushed() {
        if (!isLoaded()) {
            return false;
        }

        if (getSession().getEventStream().getLastEvent(getObject()) != null) {
            return false;
        }

        for (Iterator it = m_pdata.values().iterator(); it.hasNext(); ) {
            PropertyData pdata = (PropertyData) it.next();
            if (!pdata.isFlushed()) { return false; }
        }

        return true;
    }

    void setState(State state) {
        if (state.equals(INFANTILE)) {
            m_startedNew = true;
        }

        m_state = state;

        if (!m_state.equals(NUBILE) && !m_state.equals(UNKNOWN)) {
            getSession().addModified(getObject());
        }
    }

    public Collection getDependentEvents() {
        return Collections.singletonList
            (getSession().getEventStream().getLastEvent(getObject()));
    }

    public String getViolationMessage() {
        return getObject() + " must be assigned to a container";
    }

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

        Event ev = getSession().getEventStream().getLastEvent(getObject());
        if (ev != null) {
            out.println("    Current Object Event:");
            ev.dump(out);
        }
    }

}
