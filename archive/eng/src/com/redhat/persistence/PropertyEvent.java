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

import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import java.io.PrintWriter;

/**
 * PropertyEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2004/08/30 $
 **/

public abstract class PropertyEvent extends Event {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/PropertyEvent.java#4 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    final private Property m_prop;
    final private Object m_arg;
    private PropertyData m_pdata;
    private ObjectData m_argodata;
    private PropertyEvent m_origin;

    PropertyEvent(Session ssn, Object obj, Property prop, Object arg) {
        this(ssn, obj, prop, arg, null);
    }

    PropertyEvent(Session ssn, Object obj, Property prop, Object arg,
                  PropertyEvent origin) {
        super(ssn, obj);
        m_prop = prop;
        m_arg = arg;
        m_origin = origin;

        if (arg != null) {
            ObjectType expected = prop.getType();
            ObjectType actual = getSession().getObjectType(arg);
            if (!actual.isSubtypeOf(expected)) {
                throw new TypeException
                    (ProtoException.VALUE, expected, actual, arg);
            }
        }

        if (origin != null) {
            origin.addDependent(this);
            this.addDependent(origin);
        }
    }

    public Property getProperty() {
        return m_prop;
    }

    public Object getArgument() {
        return m_arg;
    }

    void setPropertyData(PropertyData pdata) {
        m_pdata = pdata;
    }

    PropertyData getPropertyData() {
        return m_pdata;
    }

    ObjectData getObjectData() {
        if (m_pdata == null) { return null; }
        return m_pdata.getObjectData();
    }

    ObjectData getArgumentObjectData() {
        return m_argodata;
    }

    void prepare() {
        PropertyData pd =
            getSession().fetchPropertyData(getObject(), getProperty());
        if (pd == null) { throw new IllegalStateException(this.toString()); }
        setPropertyData(pd);
        m_argodata = getArgument() == null ?
            null : getSession().getObjectData(getArgument());
    }

    void activate() {
        Session ssn = getSession();

        // WAW
        PropertyEvent prev = ssn.getEventStream().getLastEvent(this);
        if (prev != null) { prev.addDependent(this); }

        // connect event to session data
        ssn.getEventStream().add(this);

        // update object data state
        if (getObjectData().isNubile()) {
            getObjectData().setState(ObjectData.AGILE);
        }

        // object existence
        ObjectData od = getObjectData();
        if (od.isInfantile()) {
            CreateEvent ce =
                (CreateEvent) ssn.getEventStream().getLastEvent(getObject());
            ce.addDependent(this);
        }

        // arg existence
        ObjectData aodata = getArgumentObjectData();
        Object arg = getArgument();
        if (aodata != null) {
            if (aodata.isInfantile()) {
                CreateEvent ce =
                    (CreateEvent) ssn.getEventStream().getLastEvent(arg);
                ce.addDependent(this);
            }
        }

        // nested object violations
        if (ssn.hasSessionKey(arg)) {
            ssn.removeViolation(aodata);
        }
    }

    void sync() {
        getSession().getEventStream().remove(this);
    }

    void dump(PrintWriter out) {
        out.print("        ");
        out.print(getName());
        out.print("(");
        out.print(m_arg);
        out.println(")");
    }

    public String toString() {
        return getName() + " " + getSession().str(getObject()) + "." +
            getProperty().getName() + " " + getSession().str(getArgument());
    }
}
