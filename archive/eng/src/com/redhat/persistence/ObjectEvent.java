/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence;

import java.io.PrintWriter;

/**
 * ObjectEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/05 $
 **/

public abstract class ObjectEvent extends Event {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/ObjectEvent.java#2 $ by $Author: rhs $, $DateTime: 2004/08/05 12:04:47 $";

    private ObjectData m_odata;

    ObjectEvent(Session ssn, Object obj) {
        super(ssn, obj);
    }

    void setObjectData(ObjectData odata) {
        m_odata = odata;
    }

    ObjectData getObjectData() {
        return m_odata;
    }

    void prepare() {
        ObjectData od = getSession().getObjectData(getObject());
        if (od == null) { throw new IllegalStateException(toString()); }
        setObjectData(od);
    }

    void activate() {
        ObjectEvent prev =
            getSession().getEventStream().getLastEvent(getObject());
        if (prev != null) { prev.addDependent(this); }
        getSession().getEventStream().add(this);
    }

    void sync() {
        getSession().getEventStream().remove(this);
    }

    void dump(PrintWriter out) {
        out.print("        ");
        out.println(getName());
    }

    public String toString() {
        return getName() + " " + getSession().str(getObject());
    }

}
