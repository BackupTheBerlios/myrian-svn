package com.arsdigita.persistence.proto;

import java.io.*;

/**
 * ObjectEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #12 $ $Date: 2003/04/01 $
 **/

public abstract class ObjectEvent extends Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ObjectEvent.java#12 $ by $Author: ashah $, $DateTime: 2003/04/01 18:05:32 $";

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
