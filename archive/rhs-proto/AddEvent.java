package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * AddEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public class AddEvent extends PropertyEvent {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/AddEvent.java#1 $ by $Author: rhs $, $DateTime: 2002/11/27 17:41:53 $";

    protected AddEvent(Session ssn, OID oid, Property prop, Object arg) {
        super(ssn, oid, prop, arg);
    }

    public String getName() { return "add"; }

}
