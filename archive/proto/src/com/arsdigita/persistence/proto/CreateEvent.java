package com.arsdigita.persistence.proto;

import java.io.*;

/**
 * CreateEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/01/31 $
 **/

public class CreateEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/CreateEvent.java#5 $ by $Author: rhs $, $DateTime: 2003/01/31 12:34:37 $";

    CreateEvent(Session ssn, OID oid) {
        super(ssn, oid);
    }

    public void dispatch(Switch sw) {
        sw.onCreate(this);
    }

    void sync() {
        super.sync();
        getSession().addObjectData(getObjectData());
    }

    String getName() { return "create"; }

}
