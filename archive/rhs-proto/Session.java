package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.*;
import java.util.*;
import java.io.*;

/**
 * Session
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

public class Session {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/Session.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

    private static final PersistentObjectSource POS =
        new PersistentObjectSource();

    HashMap m_odata = new HashMap();
    Event m_head = null;
    Event m_tail = null;

    private boolean hasObjectData(OID oid) {
        return m_odata.containsKey(oid);
    }

    private ObjectData getObjectData(OID oid) {
        if (!hasObjectData(oid)) {
            // should fetch object from db
        }

        ObjectData od = (ObjectData) m_odata.get(oid);
        if (od != null && od.isDeleted()) { return null; }
        return od;
    }

    private PropertyData getPropertyData(OID oid, Property prop) {
        ObjectData od = getObjectData(oid);
        if (od == null) {
            throw new IllegalArgumentException("No such oid: " + oid);
        }

        PropertyData pd;

        if (od.hasPropertyData(prop)) {
            pd = od.getPropertyData(prop);
        } else if (prop.isCollection()) {
            pd = new PropertyData(od, prop, null);
            pd.setValue(POS.getPersistentCollection(this,
                                                    new PropertyDataSet(pd)));
        } else if (od.isNew()){
            pd = new PropertyData(od, prop, null);
        } else {
            // should fetch property from db
            pd = new PropertyData(od, prop, null);
        }

        return pd;
    }

    public PersistentObject create(OID oid) {
        ObjectData od = (ObjectData) m_odata.get(oid);
        if (od == null) {
            od = new ObjectData(this, POS.getPersistentObject(this, oid));
        } else if (!od.isDeleted()) {
            throw new IllegalArgumentException("OID already exists: " + oid);
        }

        // This will have problems if there was a preexisting oid of a
        // different type. Also if the OID already existed and was modified
        // the new object will pick up all the changes made to the old one.
        // Not sure what to do about this except perhaps disallow it at some
        // point.
        new CreateEvent(od);

        return od.getPersistentObject();
    }

    public boolean delete(OID oid) {
        ObjectData od = getObjectData(oid);
        if (od == null) {
            return false;
        } else {
            new DeleteEvent(od);
            return true;
        }
    }

    public PersistentObject retrieve(OID oid) {
        ObjectData od = getObjectData(oid);
        if (od == null) {
            return null;
        } else {
            return od.getPersistentObject();
        }
    }

    public DataCollection retrieve(Query query) {
        // should fetch objects from db
        throw new Error("Not implemented.");
    }

    public void set(OID oid, Property prop, Object value) {
        PropertyData pd = getPropertyData(oid, prop);
        new SetEvent(pd, value);
    }

    public Object get(OID oid, Property prop) {
        PropertyData pd = getPropertyData(oid, prop);
        return pd.getValue();
    }

    private OID getLink(OID oid, Property prop, Object value) {
        ObjectType lt = (ObjectType) prop.getLinkType();

        if (lt == null) {
            return null;
        } else {
            OID link = new OID(lt);

            for (Iterator it = lt.getKeyProperties(); it.hasNext(); ) {
                Property key = (Property) it.next();
                if (key.getName().equals(prop.getName())) {
                    link.set(key.getName(), value);
                } else {
                    link.set(key.getName(), oid);
                }
            }

            return link;
        }
    }

    public PersistentObject add(OID oid, Property prop, Object value) {
        PropertyData pd = getPropertyData(oid, prop);
        new AddEvent(pd, value);
        // should deal with link attributes here
        return null;
    }

    public void remove(OID oid, Property prop, Object value) {
        PropertyData pd = getPropertyData(oid, prop);
        new RemoveEvent(pd, value);
    }

    public void flush() {
        // should write event stream out to db
        throw new Error("Not implemented.");
    }

    void dump(PrintWriter out) {
        for (Iterator it = m_odata.values().iterator(); it.hasNext(); ) {
            ObjectData od = (ObjectData) it.next();
            od.dump(out);
        }
    }

}
