package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.ObjectType;
import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;
import java.util.*;

/**
 * CreateEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #11 $ $Date: 2003/03/07 $
 **/

public class CreateEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/CreateEvent.java#11 $ by $Author: ashah $, $DateTime: 2003/03/07 13:27:00 $";

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
