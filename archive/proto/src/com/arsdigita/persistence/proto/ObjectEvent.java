package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * ObjectEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/04 $
 **/

public abstract class ObjectEvent extends Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ObjectEvent.java#2 $ by $Author: rhs $, $DateTime: 2002/12/04 19:18:22 $";

    private ObjectData m_odata;

    protected ObjectEvent(Session ssn, OID oid) {
        super(ssn, oid);
        m_odata = ssn.getObjectData(oid);
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

}
