package com.redhat.persistence;

import com.redhat.persistence.metadata.Property;
import java.io.*;

/**
 * AddEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class AddEvent extends PropertyEvent {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/AddEvent.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

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