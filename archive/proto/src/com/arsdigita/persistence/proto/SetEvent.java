package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;

/**
 * SetEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2003/02/12 $
 **/

public class SetEvent extends PropertyEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/SetEvent.java#6 $ by $Author: rhs $, $DateTime: 2003/02/12 14:21:42 $";

    SetEvent(Session ssn, Object obj, Property prop, Object arg) {
        super(ssn, obj, prop, arg);
    }

    public void dispatch(Switch sw) {
        sw.onSet(this);
    }

    void sync() {
        PropertyData pd = getPropertyData();
        pd.setValue(getArgument());
        super.sync();
    }

    public String getName() { return "set"; }

}
