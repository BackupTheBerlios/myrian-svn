package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.Property;
import java.io.*;

/**
 * PropertyEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2003/02/13 $
 **/

public abstract class PropertyEvent extends Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PropertyEvent.java#8 $ by $Author: ashah $, $DateTime: 2003/02/13 15:47:05 $";

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
