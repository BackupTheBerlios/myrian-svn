package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * SetEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/12/10 $
 **/

public class SetEvent extends PropertyEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/SetEvent.java#3 $ by $Author: rhs $, $DateTime: 2002/12/10 15:09:40 $";

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
