package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;

/**
 * SetEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #7 $ $Date: 2003/02/19 $
 **/

public class SetEvent extends PropertyEvent {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/SetEvent.java#7 $ by $Author: ashah $, $DateTime: 2003/02/19 15:49:06 $";

    SetEvent(Session ssn, Object obj, Property prop, Object arg) {
        super(ssn, obj, prop, arg);
    }

    public void dispatch(Switch sw) {
        sw.onSet(this);
    }

    void activate() {
        Property prop = getProperty();
        final boolean required = !prop.isNullable();
        Object old = null;
        if (required) { old = getSession().get(getObject(), prop); }

        super.activate();

        if (required) {
            ObjectData od = getObjectData();
            int i = od.getViolationCount();
            if (old == null && getArgument() != null) {
                od.setViolationCount(--i);
                if (i == 0) { getObjectData().setState(ObjectData.AGILE); }
                if (i == -1) { throw new IllegalStateException(); }
            } else if (old != null && getArgument() == null) {
                od.setViolationCount(i + 1);
            }
        }
    }

    void sync() {
        super.sync();
        PropertyData pd = getPropertyData();
        pd.setValue(getArgument());
    }

    public String getName() { return "set"; }

}
