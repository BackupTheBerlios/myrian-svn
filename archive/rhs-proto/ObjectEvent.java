package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * ObjectEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public abstract class ObjectEvent extends Event {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/ObjectEvent.java#1 $ by $Author: rhs $, $DateTime: 2002/11/27 17:41:53 $";

    protected ObjectEvent(Session ssn, OID oid) {
        super(ssn, oid);
    }

    void dump(PrintWriter out) {
        out.print("        ");
        out.println(getName());
    }

}
