package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * SetEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/04 $
 **/

public class SetEvent extends PropertyEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/SetEvent.java#2 $ by $Author: rhs $, $DateTime: 2002/12/04 19:18:22 $";

    protected SetEvent(Session ssn, OID oid, Property prop, Object arg) {
        super(ssn, oid, prop, arg);
    }

    void sync() {
        PropertyData pd = getPropertyData();
        pd.setValue(getArgument());
        super.sync();
    }

    public String getName() { return "set"; }

}
