package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * CreateEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/06 $
 **/

public class CreateEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/CreateEvent.java#2 $ by $Author: rhs $, $DateTime: 2002/12/06 11:46:27 $";

    protected CreateEvent(Session ssn, OID oid) {
        super(ssn, oid);
    }

    void sync() {
        super.sync();
        getSession().addObjectData(getObjectData());
    }

    String getName() { return "create"; }

}
