package com.arsdigita.persistence.proto;

import java.io.*;

/**
 * ObjectEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2003/01/10 $
 **/

public abstract class ObjectEvent extends Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ObjectEvent.java#6 $ by $Author: rhs $, $DateTime: 2003/01/10 18:29:26 $";

    private ObjectData m_odata;

    ObjectEvent(Session ssn, OID oid) {
        super(ssn, oid);

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
        return getName() + " " + getOID();
    }

}
