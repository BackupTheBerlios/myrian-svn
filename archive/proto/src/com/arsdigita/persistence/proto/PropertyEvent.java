package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;
import java.util.*;

/**
 * PropertyEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #11 $ $Date: 2003/02/27 $
 **/

public abstract class PropertyEvent extends Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PropertyEvent.java#11 $ by $Author: ashah $, $DateTime: 2003/02/27 21:02:33 $";

    final private Property m_prop;
    final private Object m_arg;
    private PropertyData m_pdata;
    private PropertyEvent m_origin;

    PropertyEvent(Session ssn, Object obj, Property prop, Object arg) {
        this(ssn, obj, prop, arg, null);
    }

    PropertyEvent(Session ssn, Object obj, Property prop, Object arg,
                  PropertyEvent origin) {
        super(ssn, obj);
        m_prop = prop;
        m_arg = arg;
        m_origin = origin;

        if (origin != null) {
            origin.addDependent(this);
            this.addDependent(origin);
        }

        log();
    }

    public Property getProperty() {
        return m_prop;
    }

    public Object getArgument() {
        return m_arg;
    }

    void setPropertyData(PropertyData pdata) {
        m_pdata = pdata;
    }

    PropertyData getPropertyData() {
        return m_pdata;
    }

    ObjectData getObjectData() {
        return getSession().getObjectData(getObject());
    }

    ObjectData getArgumentObjectData() {
        if (getArgument() == null) { return null; }
        return getSession().getObjectData(getArgument());
    }

    void inject() {
        PropertyData pd =
            getSession().fetchPropertyData(getObject(), getProperty());
        if (pd == null) { throw new IllegalStateException(this.toString()); }
        setPropertyData(pd);
    }

    void activate() {
        // WAW
        PropertyEvent prev = getPropertyData().getCurrentEvent(this);
        if (prev != null) { prev.addDependent(this); }

        // connect event to session data
        getPropertyData().addEvent(this);

        // object existence
        ObjectData od = getObjectData();
        if (od.isInfantile()) {
            CreateEvent ce = (CreateEvent) od.getCurrentEvent();
            ce.addDependent(this);
        }

        // arg existence
        ObjectData arg = getArgumentObjectData();
        if (arg != null) {
            if (arg.isInfantile()) {
                CreateEvent ce = (CreateEvent) arg.getCurrentEvent();
                ce.addDependent(this);
            }
        }
    }

    void sync() {
        m_pdata.removeEvent(this);
    }

    void dump(PrintWriter out) {
        out.print("        ");
        out.print(getName());
        out.print("(");
        out.print(m_arg);
        out.println(")");
    }

    public String toString() {
        return getName() + " " + getObject() + "." + getProperty().getName() +
            " " + getArgument();
    }
}
