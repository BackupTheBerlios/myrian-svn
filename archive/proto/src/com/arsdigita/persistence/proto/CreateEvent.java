package com.arsdigita.persistence.proto;

import java.io.*;

/**
 * CreateEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2003/02/10 $
 **/

public class CreateEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/CreateEvent.java#6 $ by $Author: ashah $, $DateTime: 2003/02/10 15:36:01 $";

    CreateEvent(Session ssn, OID oid) {
        super(ssn, oid);
    }

    public void dispatch(Switch sw) {
        sw.onCreate(this);
    }

    void sync() {
        super.sync();
        getSession().addObjectData(getObjectData());
        getObjectData().setState(ObjectData.AGILE);
    }

    String getName() { return "create"; }

}
