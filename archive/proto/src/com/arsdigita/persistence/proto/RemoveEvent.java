package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;

/**
 * RemoveEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2003/01/31 $
 **/

public class RemoveEvent extends PropertyEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/RemoveEvent.java#4 $ by $Author: rhs $, $DateTime: 2003/01/31 12:34:37 $";

    RemoveEvent(Session ssn, OID oid, Property prop, Object arg) {
        super(ssn, oid, prop, arg);
    }

    public void dispatch(Switch sw) {
        sw.onRemove(this);
    }

    public String getName() { return "remove"; }

}
