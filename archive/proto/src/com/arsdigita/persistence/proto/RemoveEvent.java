package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;

/**
 * RemoveEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #7 $ $Date: 2003/02/28 $
 **/

public class RemoveEvent extends PropertyEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/RemoveEvent.java#7 $ by $Author: ashah $, $DateTime: 2003/02/28 13:50:14 $";

    RemoveEvent(Session ssn, Object obj, Property prop, Object arg) {
        this(ssn, obj, prop, arg, null);
    }

    RemoveEvent(Session ssn, Object obj, Property prop, Object arg,
                PropertyEvent origin) {
        super(ssn, obj, prop, arg, origin);
        if (arg == null) { throw new IllegalArgumentException(toString()); }
    }

    public void dispatch(Switch sw) {
        sw.onRemove(this);
    }

    public String getName() { return "remove"; }

}
