package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.ObjectType;
import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;
import java.util.*;

/**
 * DeleteEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2003/02/27 $
 **/

public class DeleteEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/DeleteEvent.java#8 $ by $Author: ashah $, $DateTime: 2003/02/27 21:02:33 $";

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

            for (Iterator evs = pd.getCurrentEvents(); evs.hasNext(); ) {
                ((PropertyEvent) evs.next()).addDependent(this);
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
