/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence;

import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import java.io.PrintWriter;

/**
 * PropertyEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public abstract class PropertyEvent extends Event {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/PropertyEvent.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    final private Property m_prop;
    final private Object m_arg;
    private PropertyData m_pdata;
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
        if (getArgument() == null) { return null; }
        return getSession().getObjectData(getArgument());
    }

    void prepare() {
        PropertyData pd =
            getSession().fetchPropertyData(getObject(), getProperty());
        if (pd == null) { throw new IllegalStateException(this.toString()); }
        setPropertyData(pd);
    }

    void activate() {
        // WAW
        PropertyEvent prev = getSession().getEventStream().
            getLastEvent(this);
        if (prev != null) { prev.addDependent(this); }

        // connect event to session data
        getSession().getEventStream().add(this);

        // update object data state
        if (getObjectData().isNubile()) {
            getObjectData().setState(ObjectData.AGILE);
        }

        // object existence
        ObjectData od = getObjectData();
        if (od.isInfantile()) {
            CreateEvent ce = (CreateEvent)
                getSession().getEventStream().getLastEvent(getObject());
            ce.addDependent(this);
        }

        // arg existence
        ObjectData arg = getArgumentObjectData();
        if (arg != null) {
            if (arg.isInfantile()) {
                CreateEvent ce = (CreateEvent) getSession().getEventStream()
                    .getLastEvent(getArgument());
                ce.addDependent(this);
            }
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
        return getName() + " " + getObject() + "." + getProperty().getName() +
            " " + getArgument();
    }
}
