package com.arsdigita.persistence.proto;

import java.io.*;

/**
 * DeleteEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2003/01/02 $
 **/

public class DeleteEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/DeleteEvent.java#4 $ by $Author: rhs $, $DateTime: 2003/01/02 15:38:03 $";

    DeleteEvent(Session ssn, OID oid) {
        super(ssn, oid);
    }

    void fire(EventHandler ev) {
        ev.onDelete(this);
    }

    void sync() {
        super.sync();
        getSession().removeObjectData(getOID());
    }

    String getName() { return "delete"; }

}
