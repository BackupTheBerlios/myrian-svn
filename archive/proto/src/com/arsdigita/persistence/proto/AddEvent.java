package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;

/**
 * AddEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2003/01/31 $
 **/

public class AddEvent extends PropertyEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/AddEvent.java#4 $ by $Author: rhs $, $DateTime: 2003/01/31 12:34:37 $";

    AddEvent(Session ssn, OID oid, Property prop, Object arg) {
        super(ssn, oid, prop, arg);
    }

    public void dispatch(Switch sw) {
        sw.onAdd(this);
    }

    public String getName() { return "add"; }

}
