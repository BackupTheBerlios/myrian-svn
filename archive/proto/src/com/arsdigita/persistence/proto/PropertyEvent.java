package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;

/**
 * PropertyEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2003/02/19 $
 **/

public abstract class PropertyEvent extends Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PropertyEvent.java#10 $ by $Author: ashah $, $DateTime: 2003/02/19 15:49:06 $";

    private Property m_prop;
    private Object m_arg;
    private PropertyData m_pdata;

    PropertyEvent(Session ssn, Object obj, Property prop, Object arg) {
        super(ssn, obj);
        m_prop = prop;
        m_arg = arg;

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

    void inject() {
        PropertyData pd =
            getSession().fetchPropertyData(getObject(), getProperty());
        if (pd == null) { throw new IllegalStateException(this.toString()); }
        setPropertyData(pd);
    }

    void activate() {
        getPropertyData().addEvent(this);

        ObjectData od = getObjectData();
        if (od.getViolationCount() < 0) {
            od.setViolationCount(0);
        }

        if (od.getState().equals(od.NUBILE)) { od.setState(od.AGILE); }
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
