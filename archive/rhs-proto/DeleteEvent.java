package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * DeleteEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public class DeleteEvent extends ObjectEvent {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/DeleteEvent.java#1 $ by $Author: rhs $, $DateTime: 2002/11/27 17:41:53 $";

    protected DeleteEvent(Session ssn, OID oid) {
        super(ssn, oid);
    }

    String getName() { return "delete"; }

}
