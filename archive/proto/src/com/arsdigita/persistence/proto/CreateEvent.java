package com.arsdigita.persistence.proto;

import java.io.*;

/**
 * CreateEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2003/01/02 $
 **/

public class CreateEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/CreateEvent.java#4 $ by $Author: rhs $, $DateTime: 2003/01/02 15:38:03 $";

    CreateEvent(Session ssn, OID oid) {
        super(ssn, oid);
    }

    void fire(EventHandler ev) {
        ev.onCreate(this);
    }

    void sync() {
        super.sync();
        getSession().addObjectData(getObjectData());
    }

    String getName() { return "create"; }

}
