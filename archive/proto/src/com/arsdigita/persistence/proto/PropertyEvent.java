package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * PropertyEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/12/06 $
 **/

public abstract class PropertyEvent extends Event {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PropertyEvent.java#3 $ by $Author: rhs $, $DateTime: 2002/12/06 11:46:27 $";

    private Property m_prop;
    private Object m_arg;
    private PropertyData m_pdata;

    protected PropertyEvent(Session ssn, OID oid, Property prop, Object arg) {
        super(ssn, oid);
        m_prop = prop;
        m_arg = arg;
        m_pdata = ssn.getPropertyData(oid, prop);

        log();
    }

    public Property getProperty() {
        return m_prop;
    }

    public Object getArgument() {
        return m_arg;
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
        return getName() + " " + getOID() + "." + getProperty().getName() +
            " " + getArgument();
    }

}
