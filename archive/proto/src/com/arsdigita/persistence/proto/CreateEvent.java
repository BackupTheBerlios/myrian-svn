package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.ObjectType;
import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;
import java.util.*;

/**
 * CreateEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2003/02/19 $
 **/

public class CreateEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/CreateEvent.java#8 $ by $Author: ashah $, $DateTime: 2003/02/19 15:49:06 $";

    CreateEvent(Session ssn, Object obj) {
        super(ssn, obj);
    }

    public void dispatch(Switch sw) {
        sw.onCreate(this);
    }

    void activate() {
        super.activate();

        getObjectData().invalidatePropertyData();

        if (getObjectData().getViolationCount() != -1) {
            throw new IllegalStateException();
        }

        ObjectType type = getSession().getObjectType(getObject());

        int i = 0;
        for (Iterator it = type.getProperties().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            if (!prop.isNullable()) { i++; }
        }

        getObjectData().setViolationCount(i);
    }

    void sync() {
        super.sync();
        getSession().addObjectData(getObjectData());
        getObjectData().setState(ObjectData.AGILE);
    }

    String getName() { return "create"; }
}
