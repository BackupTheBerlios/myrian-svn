package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * DeleteEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/12/10 $
 **/

public class DeleteEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/DeleteEvent.java#3 $ by $Author: rhs $, $DateTime: 2002/12/10 15:09:40 $";

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
