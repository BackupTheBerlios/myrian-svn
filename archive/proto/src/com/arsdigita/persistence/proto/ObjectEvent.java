package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * ObjectEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2002/12/10 $
 **/

public abstract class ObjectEvent extends Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ObjectEvent.java#4 $ by $Author: rhs $, $DateTime: 2002/12/10 15:09:40 $";

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
