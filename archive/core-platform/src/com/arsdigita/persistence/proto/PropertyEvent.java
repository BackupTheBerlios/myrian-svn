package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.ObjectType;
import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;
import java.util.*;

/**
 * PropertyEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public abstract class PropertyEvent extends Event {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/PropertyEvent.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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

        if (arg != null) {
            ObjectType expected = prop.getType();
            ObjectType actual = getSession().getObjectType(arg);
            if (!actual.isSubtypeOf(expected)) {
                throw new TypeException
                    (ProtoException.VALUE, expected, actual, arg);
            }
        }

        if (origin != null) {
            origin.addDependent(this);
            this.addDependent(origin);
        }
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
        if (m_pdata == null) { return null; }
        return m_pdata.getObjectData();
    }

    ObjectData getArgumentObjectData() {
        if (getArgument() == null) { return null; }
        return getSession().getObjectData(getArgument());
    }

    void prepare() {
        PropertyData pd =
            getSession().fetchPropertyData(getObject(), getProperty());
        if (pd == null) { throw new IllegalStateException(this.toString()); }
        setPropertyData(pd);
    }

    void activate() {
        // WAW
        PropertyEvent prev = getSession().getEventStream().
            getLastEvent(this);
        if (prev != null) { prev.addDependent(this); }

        // connect event to session data
        getSession().getEventStream().add(this);

        // update object data state
        if (getObjectData().isNubile()) {
            getObjectData().setState(ObjectData.AGILE);
        }

        // object existence
        ObjectData od = getObjectData();
        if (od.isInfantile()) {
            CreateEvent ce = (CreateEvent)
                getSession().getEventStream().getLastEvent(getObject());
            ce.addDependent(this);
        }

        // arg existence
        ObjectData arg = getArgumentObjectData();
        if (arg != null) {
            if (arg.isInfantile()) {
                CreateEvent ce = (CreateEvent) getSession().getEventStream()
                    .getLastEvent(getArgument());
                ce.addDependent(this);
            }
        }
    }

    void sync() {
        getSession().getEventStream().remove(this);
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
