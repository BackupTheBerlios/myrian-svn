package com.redhat.persistence;

import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import java.io.*;
import java.util.*;

/**
 * CreateEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class CreateEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/CreateEvent.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    CreateEvent(Session ssn, Object obj) {
        super(ssn, obj);
    }

    public void dispatch(Switch sw) {
        sw.onCreate(this);
    }

    void activate() {
        super.activate();

        getObjectData().setState(ObjectData.INFANTILE);

        // set up new dependencies
        ObjectType type = getSession().getObjectType(getObject());

        for (Iterator it = type.getProperties().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            if (!prop.isNullable()) {
                PropertyData pd =
                    getSession().fetchPropertyData(getObject(), prop);
                pd.addNotNullDependent(this);
            }
        }
    }

    void sync() {
        super.sync();
        getSession().addObjectData(getObjectData());
        getObjectData().setState(ObjectData.AGILE);
    }

    String getName() { return "create"; }
}
