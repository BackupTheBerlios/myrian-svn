package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.OID;
import java.io.*;

/**
 * PropertyEvent
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public abstract class PropertyEvent extends Event {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/PropertyEvent.java#1 $ by $Author: rhs $, $DateTime: 2002/11/27 17:41:53 $";

    private Property m_prop;
    private Object m_arg;

    protected PropertyEvent(Session ssn, OID oid, Property prop, Object arg) {
        super(ssn, oid);
        m_prop = prop;
        m_arg = arg;
    }

    public Property getProperty() {
        return m_prop;
    }

    public Object getArgument() {
        return m_arg;
    }

    void dump(PrintWriter out) {
        out.print("        ");
        out.print(getName());
        out.print("(");
        out.print(m_arg);
        out.println(")");
    }

}
