package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;

/**
 * AddEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #7 $ $Date: 2003/02/28 $
 **/

public class AddEvent extends PropertyEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/AddEvent.java#7 $ by $Author: ashah $, $DateTime: 2003/02/28 13:50:14 $";

    AddEvent(Session ssn, Object obj, Property prop, Object arg) {
        this(ssn, obj, prop, arg, null);
    }

    AddEvent(Session ssn, Object obj, Property prop, Object arg,
             PropertyEvent origin) {
        super(ssn, obj, prop, arg, origin);
        if (arg == null) { throw new IllegalArgumentException(toString()); }
    }

    public void dispatch(Switch sw) {
        sw.onAdd(this);
    }

    public String getName() { return "add"; }

}
