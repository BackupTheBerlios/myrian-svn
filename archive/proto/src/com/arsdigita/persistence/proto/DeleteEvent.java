package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * DeleteEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/04 $
 **/

public class DeleteEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/DeleteEvent.java#2 $ by $Author: rhs $, $DateTime: 2002/12/04 19:18:22 $";

    protected DeleteEvent(Session ssn, OID oid) {
        super(ssn, oid);
    }

    void sync() {
        super.sync();
        getSession().removeObjectData(getOID());
    }

    String getName() { return "delete"; }

}
