package com.arsdigita.persistence.proto;

import java.io.*;

/**
 * ObjectEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2003/02/14 $
 **/

public abstract class ObjectEvent extends Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ObjectEvent.java#8 $ by $Author: ashah $, $DateTime: 2003/02/14 01:21:43 $";

    private ObjectData m_odata;

    ObjectEvent(Session ssn, Object obj) {
        super(ssn, obj);

        log();
    }

    void setObjectData(ObjectData odata) {
        m_odata = odata;
    }

    ObjectData getObjectData() {
        return m_odata;
    }

    void sync() {
        m_odata.removeEvent(this);
    }

    void dump(PrintWriter out) {
        out.print("        ");
        out.println(getName());
    }

    public String toString() {
        return getName() + " " + getObject();
    }

}
