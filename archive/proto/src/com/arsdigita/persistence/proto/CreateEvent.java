package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * CreateEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/12/02 $
 **/

public class CreateEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/CreateEvent.java#1 $ by $Author: rhs $, $DateTime: 2002/12/02 12:04:21 $";

    protected CreateEvent(Session ssn, OID oid) {
        super(ssn, oid);
    }

    String getName() { return "create"; }

}
