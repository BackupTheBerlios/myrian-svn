package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;

/**
 * SetEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2003/01/02 $
 **/

public class SetEvent extends PropertyEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/SetEvent.java#4 $ by $Author: rhs $, $DateTime: 2003/01/02 15:38:03 $";

    SetEvent(Session ssn, OID oid, Property prop, Object arg) {
        super(ssn, oid, prop, arg);
    }

    void fire(EventHandler ev) {
        ev.onSet(this);
    }

    void sync() {
        PropertyData pd = getPropertyData();
        pd.setValue(getArgument());
        super.sync();
    }

    public String getName() { return "set"; }

}
