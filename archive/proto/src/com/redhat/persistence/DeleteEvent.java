package com.redhat.persistence;

import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import java.io.*;
import java.util.*;

/**
 * DeleteEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class DeleteEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/DeleteEvent.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    DeleteEvent(Session ssn, Object obj) {
        super(ssn, obj);
    }

    public void dispatch(Switch sw) {
        sw.onDelete(this);
    }

    void activate() {
        super.activate();
        ObjectType type = getSession().getObjectType(getObject());

        for (Iterator it = type.getProperties().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            PropertyData pd = getObjectData().getPropertyData(prop);
            if  (pd == null) { continue; }

            if (!prop.isNullable()) {
                pd.transferNotNullDependentEvents(this);
            }

            if (prop.isCollection()) {
                for (Iterator evs = getSession().getEventStream().
                         getCurrentEvents(getObject(), prop).iterator();
                     evs.hasNext(); ) {
                    ((PropertyEvent) evs.next()).addDependent(this);
                }
            } else {
                Event ev = getSession().getEventStream().getLastEvent
                    (getObject(), prop);
                if (ev != null) { ev.addDependent(this); }
            }
        }

        getObjectData().setState(ObjectData.SENILE);
    }

    void sync() {
        super.sync();
        getObjectData().setState(ObjectData.DEAD);
    }

    String getName() { return "delete"; }

}
