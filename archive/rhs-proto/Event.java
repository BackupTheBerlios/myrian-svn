package com.arsdigita.persistence.proto;

import com.arsdigita.util.Assert;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import java.io.*;

/**
 * Event
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

abstract class Event {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/Event.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

    private Session m_ssn;
    private OID m_oid;

    Event m_next;

    protected Event(Session ssn, OID oid) {
        m_ssn = ssn;
        m_oid = oid;

        if (m_ssn.m_head == null) {
            m_ssn.m_head = this;
        }

        if (m_ssn.m_tail == null) {
            m_ssn.m_tail = this;
        } else {
            m_ssn.m_tail.m_next = this;
            m_ssn.m_tail = this;
        }
    }

    public Session getSession() {
        return m_ssn;
    }

    public OID getOID() {
        return m_oid;
    }

    abstract void dump(PrintWriter out);

    abstract String getName();

}

abstract class ObjectEvent extends Event {

    private ObjectData m_odata;

    protected ObjectEvent(ObjectData odata) {
        super(odata.getSession(), validate(odata).getOID());
        m_odata = odata;

        m_odata.m_events.add(this);
    }

    private static final ObjectData validate(ObjectData odata) {
        Assert.assertNotNull(odata.getOID());
        return odata;
    }

    public ObjectData getObjectData() {
        return m_odata;
    }

    void dump(PrintWriter out) {
        out.print("        ");
        out.println(getName());
    }

}

class CreateEvent extends ObjectEvent {

    protected CreateEvent(ObjectData odata) {
        super(odata);
    }

    String getName() { return "create"; }

}

class DeleteEvent extends ObjectEvent {

    protected DeleteEvent(ObjectData odata) {
        super(odata);
    }

    String getName() { return "delete"; }

}

abstract class PropertyEvent extends Event {

    private PropertyData m_pdata;
    private Object m_arg;

    protected PropertyEvent(PropertyData pdata, Object arg) {
        super(pdata.getSession(), pdata.getOID());
        m_pdata = pdata;
        m_arg = arg;

        m_pdata.m_events.add(this);
    }

    public PropertyData getPropertyData() {
        return m_pdata;
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

class SetEvent extends PropertyEvent {

    protected SetEvent(PropertyData pdata, Object arg) {
        super(pdata, arg);
    }

    public String getName() { return "set"; }

}

class AddEvent extends PropertyEvent {

    protected AddEvent(PropertyData pdata, Object arg) {
        super(pdata, arg);
    }

    public String getName() { return "add"; }

}

class RemoveEvent extends PropertyEvent {

    protected RemoveEvent(PropertyData pdata, Object arg) {
        super(pdata, arg);
    }

    public String getName() { return "remove"; }

}
