package com.redhat.persistence;

import java.io.*;

/**
 * ObjectEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public abstract class ObjectEvent extends Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/ObjectEvent.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

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
        return getName() + " " + getObject();
    }

}
