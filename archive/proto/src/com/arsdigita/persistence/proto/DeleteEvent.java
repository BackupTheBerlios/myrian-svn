package com.arsdigita.persistence.proto;

import java.io.*;

/**
 * DeleteEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2003/02/12 $
 **/

public class DeleteEvent extends ObjectEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/DeleteEvent.java#6 $ by $Author: rhs $, $DateTime: 2003/02/12 14:21:42 $";

    DeleteEvent(Session ssn, Object obj) {
        super(ssn, obj);
    }

    public void dispatch(Switch sw) {
        sw.onDelete(this);
    }

    void sync() {
        super.sync();
        getSession().removeObjectData(getObject());
    }

    String getName() { return "delete"; }

}
