package com.arsdigita.persistence.proto;

import java.io.*;

/**
 * DeleteEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #7 $ $Date: 2003/02/19 $
 **/

public class DeleteEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/DeleteEvent.java#7 $ by $Author: ashah $, $DateTime: 2003/02/19 15:49:06 $";

    DeleteEvent(Session ssn, Object obj) {
        super(ssn, obj);
    }

    public void dispatch(Switch sw) {
        sw.onDelete(this);
    }

    void activate() {
        super.activate();
        getObjectData().setViolationCount(-1);
        getObjectData().setState(ObjectData.SENILE);
    }

    void sync() {
        super.sync();
        getObjectData().setState(ObjectData.DEAD);
    }

    String getName() { return "delete"; }

}
