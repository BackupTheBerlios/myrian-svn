package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.*;
import java.util.*;
import java.io.*;

import org.apache.log4j.Logger;

/**
 * A Session object provides the primary means for client Java code to
 * interact with the persistence layer. This code is either Java code using
 * the persistence layer to implement object persistence, or Java code working
 * with persistent objects.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2002/12/10 $
 **/

public class Session {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Session.java#5 $ by $Author: rhs $, $DateTime: 2002/12/10 15:09:40 $";

    private static final Logger LOG = Logger.getLogger(Session.class);

    private static final PersistentObjectSource POS =
        new PersistentObjectSource();

    private final Engine m_engine = Engine.getInstance(this);
    private HashMap m_odata = new HashMap();

    // These are kept up to date by code in Event.java
    Event m_head = null;
    Event m_tail = null;


    /**
     * Creates an object with the given OID.
     *
     * @param oid The OID of the object to create.
     *
     * @return The newly created object.
     **/

    public PersistentObject create(OID oid) {
        if (LOG.isDebugEnabled()) {
            trace("create", new Object[] {oid});
        }

        ObjectData od = getObjectData(oid);
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
        addEvent(new CreateEvent(this, oid), od);

        for (Iterator it = oid.getObjectType().getKeyProperties();
             it.hasNext(); ) {
            Property prop = (Property) it.next();
            set(oid, prop, oid.get(prop.getName()));
        }

        PersistentObject result = od.getPersistentObject();

        if (LOG.isDebugEnabled()) {
            untrace("create", result);
        }

        return result;
    }


    /**
     * Deletes the object with the given OID.
     *
     * @param oid The OID of the object to delete.
     *
     * @return True if an object was deleted, false otherwise.
     **/

    public boolean delete(OID oid) {
        if (LOG.isDebugEnabled()) {
            trace("delete", new Object[] {oid});
        }

        boolean result;

        ObjectData od = fetchObjectData(oid);

        if (od.isVisiting()) {
            result = false;
        } else {
            od.setVisiting(true);
            if (od == null) {
                result = false;
            } else {
                ObjectType type = oid.getObjectType();
                for (Iterator it = type.getProperties(); it.hasNext(); ) {
                    Property prop = (Property) it.next();
                    if (prop.isRole()) {
                        if (prop.isCollection()) {
                            clear(oid, prop);
                        } else {
                            set(oid, prop, null);
                        }
                    }
                }
                addEvent(new DeleteEvent(this, oid), od);
                result = true;
            }
            od.setVisiting(false);
        }

        if (LOG.isDebugEnabled()) {
            untrace("delete", result);
        }

        return result;
    }


    /**
     * Retrieves the object with the given OID.
     *
     * @param oid The OID of the object to retrieve.
     *
     * @return The retrieved object.
     **/

    public PersistentObject retrieve(OID oid) {
        if (LOG.isDebugEnabled()) {
            trace("retrieve", new Object[] {oid});
        }

        ObjectData od = fetchObjectData(oid);

        PersistentObject result;
        if (od == null) {
            result = null;
        } else {
            result = od.getPersistentObject();
        }

        if (LOG.isDebugEnabled()) {
            untrace("retrieve", result);
        }

        return result;
    }


    /**
     * Returns a PersistentCollection that corresponds to the given Query.
     *
     * @param query A query object that specifies which objects are to be
     *              included in the collection and which properties are to be
     *              preloaded.
     *
     * @return A PersistentCollection that corresponds to the given Query.
     **/

    public PersistentCollection retrieve(Query query) {
        return POS.getPersistentCollection(this, new DataSet(this, query));
    }


    /**
     * Sets a property of an object to a specified value.
     *
     * @param oid The OID of the object to be mutated.
     * @param prop The Property to mutate.
     * @param value The new value for the Property.
     **/

    public void set(OID oid, Property prop, Object value) {
        if (LOG.isDebugEnabled()) {
            trace("set", new Object[] {oid, prop.getName(), value});
        }

        // Instead of storing the old value and removing it we could simply do
        // a delete of the component and count on the code in delete to add
        // the proper remove. Not sure what makes the most sense yet.

        Object old = null;
        if (prop.isRole() && prop.isComponent() ||
            prop.getAssociatedProperty() != null) {
            old = get(oid, prop);
        }

        PropertyData pd = fetchPropertyData(oid, prop);
        addEvent(new SetEvent(this, oid, prop, value), pd);

        if (prop.isRole() && prop.isComponent()) {
            PersistentObject po = (PersistentObject) old;
            if (po != null) {
                delete(po.getOID());
            }
        } else if (prop.getAssociatedProperty() != null) {
            Property ass = prop.getAssociatedProperty();
            PersistentObject oldpo = (PersistentObject) old;
            PersistentObject po = (PersistentObject) value;
            PersistentObject me = retrieve(oid);
            if (ass.isCollection()) {
                if (oldpo != null) {
                    addEvent(new RemoveEvent(this, oldpo.getOID(), ass,
                                                me));
                }
                if (po != null) {
                    addEvent(new AddEvent(this, po.getOID(), ass, me));
                }
            } else {
                throw new IllegalStateException
                    ("This case is just fucked up. " +
                     "I'll wait till it happens to deal with it.");
            }
        }

        if (LOG.isDebugEnabled()) {
            untrace("set");
        }
    }

    /**
     * Fetches the property value for the given OID.
     *
     * @param oid The OID of the object being accessed.
     * @param prop The Property to access.
     *
     * @return The value of the property.
     **/

    public Object get(OID oid, Property prop) {
        if (LOG.isDebugEnabled()) {
            trace("get", new Object[] {oid, prop.getName()});
        }

        PropertyData pd = fetchPropertyData(oid, prop);
        Object result = pd.get();

        if (LOG.isDebugEnabled()) {
            untrace("get", result);
        }

        return result;
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
        if (LOG.isDebugEnabled()) {
            trace("add", new Object[] {oid, prop.getName(), value});
        }

        // should deal with link attributes here
        PropertyData pd = fetchPropertyData(oid, prop);
        addEvent(new AddEvent(this, oid, prop, value), pd);
        if (prop.getAssociatedProperty() != null) {
            PersistentObject me = retrieve(oid);
            Property ass = prop.getAssociatedProperty();
            PersistentObject po = (PersistentObject) value;
            if (ass.isCollection()) {
                addEvent(new AddEvent(this, po.getOID(), ass, me));
            } else {
                PersistentObject old =
                    (PersistentObject) get(po.getOID(), ass);
                if (old != null) {
                    addEvent(new RemoveEvent(this, old.getOID(), ass, po));
                }
                addEvent(new SetEvent(this, po.getOID(), ass, me));
            }
        }

        if (LOG.isDebugEnabled()) {
            untrace("add", null);
        }

        return null;
    }

    public void remove(OID oid, Property prop, Object value) {
        if (LOG.isDebugEnabled()) {
            trace("remove", new Object[] {oid, prop.getName(), value});
        }

        PropertyData pd = fetchPropertyData(oid, prop);
        addEvent(new RemoveEvent(this, oid, prop, value), pd);

        if (prop.isRole() && prop.isComponent()) {
            PersistentObject po = (PersistentObject) value;
            if (po != null) {
                delete(po.getOID());
            }
        } else if (prop.getAssociatedProperty() != null) {
            Property ass = (Property) prop.getAssociatedProperty();
            PersistentObject me = retrieve(oid);
            PersistentObject po = (PersistentObject) value;
            if (ass.isCollection()) {
                addEvent(new RemoveEvent(this, po.getOID(), ass, me));
            } else {
                addEvent(new SetEvent(this, po.getOID(), ass, null));
            }
        }

        if (LOG.isDebugEnabled()) {
            untrace("remove");
        }
    }


    /**
     * Removes all elements from the given property.
     *
     * @param oid The OID of the object containing the property being cleared.
     * @param prop The property being cleared.
     **/

    public void clear(OID oid, Property prop) {
        if (LOG.isDebugEnabled()) {
            trace("clear", new Object[] {oid, prop.getName()});
        }

        PersistentCollection pc =
            (PersistentCollection) get(oid, prop);
        Cursor c = pc.getDataSet().getCursor();
        while (c.next()) {
            remove(oid, prop, c.get());
        }

        if (LOG.isDebugEnabled()) {
            untrace("clear");
        }
    }


    /**
     * Performs all operations queued up by the session. This is automatically
     * called when necessary in order to insure that queries performed by the
     * datastore are consistent with the contents of the in memory data cache.
     **/

    public void flush() {
        if (LOG.isDebugEnabled()) {
            trace("flush", new Object[0]);
        }

        for (Event ev = m_head; ev != null; ev = ev.getNext()) {
            ev.fire(m_engine.getEventHandler());
        }

        m_engine.flush();

        for (Event ev = m_head; ev != null; ev = ev.getNext()) {
            ev.sync();
        }

        m_head = null;
        m_tail = null;

        if (LOG.isDebugEnabled()) {
            untrace("flush");
        }
    }


    /**
     * Renders all changes made within the transaction permanent and ends the
     * transaction.
     **/

    public void commit() {
        flush();
        m_engine.commit();
    }


    /**
     * Reverts all changes made within the transaction and ends the
     * transaction. 
     **/

    public void rollback() {
        // should properly roll back java state
        m_odata.clear();
        m_head = null;
        m_tail = null;
        m_engine.rollback();
    }

    Engine getEngine() {
        return m_engine;
    }

    void load(OID oid, Property prop, Object value) {
        if (LOG.isDebugEnabled()) {
            trace("load", new Object[] {oid, prop.getName(), value});
        }

        ObjectData od = getObjectData(oid);
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

        if (LOG.isDebugEnabled()) {
            untrace("load");
        }
    }

    private void addEvent(Event ev) {
        if (m_head == null) {
            m_head = ev;
            m_tail = ev;
        } else {
            m_tail.insert(ev);
        }
    }

    private void addEvent(ObjectEvent ev, ObjectData od) {
        this.addEvent(ev);
        od.addEvent(ev);
    }

    private void addEvent(PropertyEvent ev, PropertyData pd) {
        this.addEvent(ev);
        pd.addEvent(ev);
    }

    boolean hasObjectData(OID oid) {
        return m_odata.containsKey(oid);
    }

    ObjectData getObjectData(OID oid) {
        return (ObjectData) m_odata.get(oid);
    }

    void removeObjectData(OID oid) {
        m_odata.remove(oid);
    }

    void addObjectData(ObjectData odata) {
        m_odata.put(odata.getOID(), odata);
    }

    PropertyData getPropertyData(OID oid, Property prop) {
        ObjectData od = getObjectData(oid);
        if (od != null) {
            return od.getPropertyData(prop);
        } else {
            return null;
        }
    }

    private ObjectData fetchObjectData(OID oid) {
        if (!hasObjectData(oid)) {
            RecordSet rs = m_engine.execute(getRetrieveQuery(oid));
            // Cache non-existent objects
            if (!rs.next()) {
                m_odata.put(oid, null);
            }
            while (rs.next()) {
                rs.load(this);
            }
        }

        ObjectData od = getObjectData(oid);
        if (od != null && od.isDeleted()) {
            return null;
        } else {
            return od;
        }
    }

    private PropertyData fetchPropertyData(OID oid, Property prop) {
        ObjectData od = fetchObjectData(oid);
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
            RecordSet rs = m_engine.execute(getRetrieveQuery(oid, prop));
            OID value = null;
            while (rs.next()) {
                value = rs.load(this);
            }
            if (value == null) {
                pd = new PropertyData(od, prop, null);
            } else {
                pd = new PropertyData(od, prop, retrieve(value));
            }
        }

        return pd;
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

    private Filter getOIDFilter(OID oid) {
        Filter f = null;
        for (Iterator it = oid.getObjectType().getKeyProperties();
             it.hasNext(); ) {
            Property prop = (Property) it.next();
            Filter eq = m_engine.getEquals(Path.getInstance(prop.getName()),
                                           oid.get(prop.getName()));
            if (f == null) {
                f = eq;
            } else {
                f = m_engine.getAnd(eq, f);
            }
        }

        return f;
    }

    private Query getRetrieveQuery(OID oid) {
        ObjectType type = oid.getObjectType();
        Signature sig = getRetrieveSignature(type);
        return new Query(sig, getOIDFilter(oid));
    }

    private Query getRetrieveQuery(OID oid, Property prop) {
        if (prop.isAttribute()) {
            ObjectType type = oid.getObjectType();
            Signature sig = new Signature(type);
            sig.addPath(prop.getName());
            return new Query(sig, getOIDFilter(oid));
        } else {
            ObjectType type = (ObjectType) prop.getType();
            Signature sig = getRetrieveSignature(type);
            // should filter to associated object(s)
            // should deal with one way associations
            Filter f = null;
            Property ass = prop.getAssociatedProperty();
            if (ass != null) {
                if (ass.isCollection()) {
                    f = m_engine.getContains(Path.getInstance(ass.getName()),
                                             oid);
                } else {
                    f = m_engine.getEquals(Path.getInstance(ass.getName()),
                                           oid);
                }
            }
            return new Query(sig, f);
        }
    }

    void dump() {
        PrintWriter pw = new PrintWriter(System.out);
        dump(pw);
        pw.flush();
    }

    void dump(PrintWriter out) {
        for (Iterator it = m_odata.values().iterator(); it.hasNext(); ) {
            ObjectData od = (ObjectData) it.next();
            od.dump(out);
        }
    }

    private static final ThreadLocal LEVEL = new ThreadLocal() {
            protected Object initialValue() {
                return new Integer(0);
            }
        };

    static final int getLevel() {
        Integer level = (Integer) LEVEL.get();
        return level.intValue();
    }

    static final void setLevel(int level) {
        LEVEL.set(new Integer(level));
    }

    private static final void trace(String method, Object[] args) {
        if (LOG.isDebugEnabled()) {
            StringBuffer msg = new StringBuffer();
            int level = getLevel();
            for (int i = 0; i < level; i++) {
                msg.append("  ");
            }
            msg.append(method);
            msg.append("(");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    msg.append(", ");
                }
                msg.append(args[i]);
            }
            msg.append(") {");
            if (level == 0) {
                LOG.debug(msg.toString());
            }

            setLevel(level + 1);
        }
    }

    private static final void untrace(String method) {
        untrace(method, null);
    }

    private static final void untrace(String method, boolean result) {
        untrace(method, result ? Boolean.TRUE : Boolean.FALSE);
    }

    private static final void untrace(String method, Object result) {
        if (LOG.isDebugEnabled()) {
            untrace(method, " -> " + result);
        }
    }

    private static final void untrace(String method, String msg) {
        if (LOG.isDebugEnabled()) {
            int level = getLevel();
            setLevel(level - 1);

            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < level - 1; i++) {
                buf.append("  ");
            }
            buf.append("} (");
            buf.append(method);
            buf.append(")");
            if (msg != null) {
                buf.append(msg);
            }
            if (level == 1) {
                LOG.debug(buf.toString());
            }
        }
    }

}
