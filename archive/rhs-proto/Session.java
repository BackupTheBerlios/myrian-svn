package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.*;
import java.util.*;
import java.io.*;

/**
 * Session
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/11/27 $
 **/

public class Session {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/Session.java#2 $ by $Author: rhs $, $DateTime: 2002/11/27 17:41:53 $";

    private static final PersistentObjectSource POS =
        new PersistentObjectSource();

    final PersistenceEngine ENGINE =
        new com.arsdigita.persistence.proto.engine.Engine(this);
    private final EventSource ES = ENGINE.getEventSource();

    HashMap m_odata = new HashMap();
    Event m_head = null;
    Event m_tail = null;

    private boolean hasObjectData(OID oid) {
        return m_odata.containsKey(oid);
    }

    private ObjectData getObjectData(OID oid) {
        if (!hasObjectData(oid)) {
            Cursor c = ENGINE.execute(getRetrieveQuery(oid));
            // Cache non existent objects
            if (!c.next()) {
                m_odata.put(oid, null);
            }
            while (c.next()) {
                // XXX: This is a bit odd, but Cursor loads data into the
                // session automagically. Maybe that should change.
            }
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
            pd = new PropertyData
                (od, prop, POS.getPersistentCollection
                 (this, new DataSet (this, getRetrieveQuery(oid, prop))));
        } else if (od.isNew()){
            pd = new PropertyData(od, prop, null);
        } else {
            // should fetch property from db
            Cursor c = ENGINE.execute(getRetrieveQuery(oid, prop));
            while (c.next()) {
                // XXX: See above XXX
            }
            pd = od.getPropertyData(prop);
        }

        return pd;
    }

    void load(OID oid, Property prop, Object value) {
        ObjectData od = (ObjectData) m_odata.get(oid);
        if (od == null) {
            // We may need to change the signature of this to read in enough
            // data to allow type negotiation to happen properly without
            // requiring another db hit.
            od = new ObjectData(this, POS.getPersistentObject(this, oid));
        }

        PropertyData pd = od.getPropertyData(prop);
        if (pd == null) {
            pd = new PropertyData(od, prop, value);
        } else {
            pd.setValue(value);
        }
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
        od.addEvent(ES.getCreate(this, oid));

        for (Iterator it = oid.getObjectType().getKeyProperties();
             it.hasNext(); ) {
            Property prop = (Property) it.next();
            set(oid, prop, oid.get(prop.getName()));
        }

        return od.getPersistentObject();
    }

    public boolean delete(OID oid) {
        ObjectData od = getObjectData(oid);
        if (od == null) {
            return false;
        } else {
            od.addEvent(ES.getDelete(this, oid));
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

    public PersistentCollection retrieve(Query query) {
        // should fetch objects from db
        throw new Error("Not implemented.");
    }

    public void set(OID oid, Property prop, Object value) {
        PropertyData pd = getPropertyData(oid, prop);
        pd.addEvent(ES.getSet(this, oid, prop, value));
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
        pd.addEvent(ES.getAdd(this, oid, prop, value));
        // should deal with link attributes here
        return null;
    }

    public void remove(OID oid, Property prop, Object value) {
        PropertyData pd = getPropertyData(oid, prop);
        pd.addEvent(ES.getRemove(this, oid, prop, value));
    }

    public void flush() {
        // should expand event stream
        // should invalidate or update session data
        for (Event ev = m_head; ev != null; ev = ev.m_next) {
            ENGINE.write(ev);
        }

        ENGINE.flush();

        m_odata.clear();

        m_head = null;
        m_tail = null;
    }

    void dump(PrintWriter out) {
        for (Iterator it = m_odata.values().iterator(); it.hasNext(); ) {
            ObjectData od = (ObjectData) it.next();
            od.dump(out);
        }
    }

    private Signature getRetrieveSignature(ObjectType type) {
        Signature result = new Signature(type);
        for (Iterator it = type.getProperties(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            if (prop.isAttribute()) {
                result.addPath(prop.getName());
            }
        }
        // should add aggressively loaded properties

        return result;
    }

    private Query getRetrieveQuery(OID oid) {
        ObjectType type = oid.getObjectType();
        Signature sig = getRetrieveSignature(type);
        // should filter to oid
        return new Query(sig, null);
    }

    private Query getRetrieveQuery(OID oid, Property prop) {
        if (prop.isAttribute()) {
            ObjectType type = oid.getObjectType();
            Signature sig = new Signature(type);
            sig.addPath(prop.getName());
            // should filter to oid
            return new Query(sig, null);
        } else {
            ObjectType type = (ObjectType) prop.getType();
            Signature sig = getRetrieveSignature(type);
            // should filter to associated object(s)
            return new Query(sig, null);
        }
    }

}
