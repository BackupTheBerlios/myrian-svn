/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence;

import com.redhat.persistence.metadata.Property;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * PropertyData
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

class PropertyData implements Violation {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/PropertyData.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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

    ObjectData getObjectData() {
        return m_odata;
    }

    public Session getSession() {
        return m_odata.getSession();
    }

    private Object getObject() { return m_odata.getObject(); }

    public Property getProperty() {
        return m_prop;
    }

    public void setValue(Object value) {
        if (getProperty().isCollection()) {
            throw new IllegalStateException
                ("setting value of collections is not allowed. "
                 + "property: " + m_prop + " value: " + value);
        }
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

    public Collection getDependentEvents() { return m_dependentEvents; }

    public String getViolationMessage() {
        return getObjectData().getObject() + "." +
            getProperty().getName() + " is null";
    }

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
