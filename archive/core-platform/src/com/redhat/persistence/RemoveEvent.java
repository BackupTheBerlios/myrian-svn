package com.redhat.persistence;

import com.redhat.persistence.metadata.Property;
import java.io.*;

/**
 * RemoveEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class RemoveEvent extends PropertyEvent {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/RemoveEvent.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

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
