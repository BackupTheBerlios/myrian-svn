package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;

/**
 * SetEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/01/31 $
 **/

public class SetEvent extends PropertyEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/SetEvent.java#5 $ by $Author: rhs $, $DateTime: 2003/01/31 12:34:37 $";

    SetEvent(Session ssn, OID oid, Property prop, Object arg) {
        super(ssn, oid, prop, arg);
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
