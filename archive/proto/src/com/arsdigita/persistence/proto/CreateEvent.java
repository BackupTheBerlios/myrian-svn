package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * CreateEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/12/10 $
 **/

public class CreateEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/CreateEvent.java#3 $ by $Author: rhs $, $DateTime: 2002/12/10 15:09:40 $";

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
