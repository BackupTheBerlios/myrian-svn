package com.arsdigita.persistence.proto;

import java.io.*;

/**
 * ObjectEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/01/02 $
 **/

public abstract class ObjectEvent extends Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ObjectEvent.java#5 $ by $Author: rhs $, $DateTime: 2003/01/02 15:38:03 $";

    private ObjectData m_odata;

    ObjectEvent(Session ssn, OID oid) {
        super(ssn, oid);
        m_odata = ssn.getObjectData(oid);

        log();
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
        return getName() + " " + getOID();
    }

}
