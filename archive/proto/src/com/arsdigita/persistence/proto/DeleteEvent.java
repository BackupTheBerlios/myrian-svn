package com.arsdigita.persistence.proto;

import java.io.*;

/**
 * DeleteEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/01/31 $
 **/

public class DeleteEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/DeleteEvent.java#5 $ by $Author: rhs $, $DateTime: 2003/01/31 12:34:37 $";

    DeleteEvent(Session ssn, OID oid) {
        super(ssn, oid);
    }

    public void dispatch(Switch sw) {
        sw.onDelete(this);
    }

    void sync() {
        super.sync();
        getSession().removeObjectData(getOID());
    }

    String getName() { return "delete"; }

}
