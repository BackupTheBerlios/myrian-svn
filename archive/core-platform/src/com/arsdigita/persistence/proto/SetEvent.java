package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;

/**
 * SetEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/07/07 $
 **/

public class SetEvent extends PropertyEvent {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/SetEvent.java#2 $ by $Author: vadim $, $DateTime: 2003/07/07 12:16:50 $";

    private Object m_oldValue;

    SetEvent(Session ssn, Object obj, Property prop, Object arg) {
        super(ssn, obj, prop, arg);
    }

    SetEvent(Session ssn, Object obj, Property prop, Object arg,
             PropertyEvent origin) {
        super(ssn, obj, prop, arg, origin);
    }

    public void dispatch(Switch sw) {
        sw.onSet(this);
    }

    void activate() {
        PropertyEvent pe = getSession().getEventStream().getLastEvent(this);
        if (pe != null) {
            m_oldValue = pe.getArgument();
        } else {
            m_oldValue = getSession().get(getObject(), getProperty());
        }

        super.activate();

        // PD dependencies
        if (getArgument() != null) {
            getPropertyData().transferNotNullDependentEvents(this);
        } else if (!getProperty().isNullable()) {
            getPropertyData().addNotNullDependent(this);
        }
    }

    void sync() {
        super.sync();
        PropertyData pd = getPropertyData();
        pd.setValue(getArgument());
    }

    public Object getPreviousValue() { return m_oldValue; }

    public String getName() { return "set"; }

}
