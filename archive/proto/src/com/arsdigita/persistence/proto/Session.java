package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;
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
 * @version $Revision: #21 $ $Date: 2003/02/10 $
 **/

public class Session {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Session.java#21 $ by $Author: ashah $, $DateTime: 2003/02/10 15:36:01 $";

    private static final Logger LOG = Logger.getLogger(Session.class);

    private static final PersistentObjectSource POS =
        new PersistentObjectSource();

    private final Engine m_engine = Engine.getInstance(this);
    private HashMap m_odata = new HashMap();

    // These are kept up to date by code in Event.java
    LinkedList m_evs = new LinkedList();

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
            od = new ObjectData(this, POS.getPersistentObject(this, oid),
                                od.NUBILE);
        } else if (!od.isDeleted()) {
            throw new IllegalArgumentException("OID already exists: " + oid);
        } else {
            od.setState(od.NUBILE);
        }

        // This will have problems if there was a preexisting oid of a
        // different type. Also if the OID already existed and was modified
        // the new object will pick up all the changes made to the old one.
        // Not sure what to do about this except perhaps disallow it at some
        // point.
        addEvent(new CreateEvent(this, oid), od);

        for (Iterator it = oid.getObjectMap().getKeyProperties().iterator();
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

        if (od == null) {
            result = false;
        } else if (od.isSenile()) {
            result = false;
        } else {
            od.setState(od.SENILE);

            ObjectType type = oid.getObjectType();
            Collection keys = type.getKeyProperties();

            for (Iterator it = type.getRoles().iterator();
                 it.hasNext(); ) {
                Role role = (Role) it.next();
                if (role.isCollection()) {
                    clear(oid, role);
                }
            }

            for (Iterator it = type.getProperties().iterator();
                 it.hasNext(); ) {
                Role role = (Role) it.next();

                if (!role.isCollection() && !keys.contains(role)) {
                    set(oid, role, null);
                }
            }

            addEvent(new DeleteEvent(this, oid));
            result = true;
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
     * Cascades delete from container to the containee. The containee
     * will not be deleted in all cases.
     **/
    private void cascadeDelete(OID container, PersistentObject containee) {
        ObjectData containerOD = fetchObjectData(container);
        boolean me = false;
        if (!containerOD.isSenile()) {
            me = true;
            containerOD.setState(ObjectData.SENILE);
        }

        delete(containee.getOID());

        if (me) { containerOD.setState(ObjectData.SENILE); }
    }

    /**
     * Update the reverse role of an old target object. After updating a
     * reversible role, either set or remove, the reverse role for the old
     * target needs to be set to null.
     *
     * @param sourceOID the source OID of a role that has been updated
     * @param role the role that has been updated
     * @param target the old target of a role that has been updated
     **/
    private void reverseUpdateOld(OID sourceOID, Role role,
                                  PersistentObject target) {
        Role rev = role.getReverse();
        
        PersistentObject source = retrieve(sourceOID);
        if (rev.isCollection()) {
            addEvent(new RemoveEvent(this, target.getOID(), rev, source));
        } else if (rev.isNullable()) {
            addEvent(new SetEvent(this, target.getOID(), rev, null));
        } else if (!fetchObjectData(target.getOID()).isSenile() &&
                   !isDeleted(target.getOID())) {
            throw new IllegalStateException("can't set 1..1 to null");
        }
    }

    /**
     * Update the reverse role of an new target object. After updating a
     * reversible role, either set or add, the reverse role for the new target
     * needs to be set to the new source. In addition, the new targets' old
     * source needs to be updated so its role target is null.
     *
     * @param sourceOID the source OID of a role that has been updated
     * @param role the role that has been updated
     * @param targetObj the new target of a role that has been updated
     **/
    private void reverseUpdateNew(OID sourceOID, Role role,
                                  PersistentObject targetObj) {
        PersistentObject source = retrieve(sourceOID);
        OID target = targetObj.getOID();
        Role rev = role.getReverse();
        if (rev.isCollection()) {
            addEvent(new AddEvent(this, target, rev, source));
        } else {
            PersistentObject old = (PersistentObject) get(target, rev);
            if (old != null) {
                if (role.isNullable()) {
                    addEvent(new SetEvent(this, old.getOID(), role, null));
                } else if (!fetchObjectData(old.getOID()).isSenile() &&
                           !isDeleted(old.getOID())) {
                    throw new IllegalStateException("can't set 1..1 to null");
                }
            }

            addEvent(new SetEvent(this, target, rev, source));
        }
    }


    /**
     * Sets a property of an object to a specified value.
     *
     * @param oid The OID of the object to be mutated.
     * @param prop The Property to mutate.
     * @param value The new value for the Property.
     **/

    public void set(final OID oid, Property prop, final Object value) {
        if (LOG.isDebugEnabled()) {
            trace("set", new Object[] {oid, prop.getName(), value});
        }

        prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    Object old = null;
                    if (role.isComponent() || role.isReversable()) {
                        old = get(oid, role);
                    }

                    addEvent(new SetEvent(Session.this, oid, role, value));

                    if (role.isComponent()) {
                        PersistentObject po = (PersistentObject) old;
                        if (po != null) {
                            cascadeDelete(oid, po);
                        }
                    }

                    if (role.isReversable()) {
                        if (old != null) {
                            reverseUpdateOld
                                (oid, role, (PersistentObject) old);
                        }

                        if (value != null) {
                            reverseUpdateNew
                                (oid, role, (PersistentObject) value);
                        }
                    }
                }

                public void onAlias(Alias alias) {
                    set(oid, alias.getTarget(), value);
                }

                public void onLink(Link link) {
                    throw new Error("not implemented");
                }
            });

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

    public Object get(final OID oid, Property prop) {
        if (LOG.isDebugEnabled()) {
            trace("get", new Object[] {oid, prop.getName()});
        }

        final Object[] result = {null};

        prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    PropertyData pd = fetchPropertyData(oid, role);
                    result[0] = pd.get();
                }

                public void onAlias(Alias alias) {
                    result[0] = get(oid, alias.getTarget());
                }

                public void onLink(Link link) {
                    throw new Error("Not implemented yet");
                }
            });

        if (LOG.isDebugEnabled()) {
            untrace("get", result[0]);
        }

        return result[0];
    }

    Object get(OID start, Path path) {
        if (path.getParent() == null) {
            return get(start,
                       start.getObjectType().getProperty(path.getName()));
        } else {
            Object value = get(start, path.getParent());
            if (value instanceof PersistentObject) {
                OID oid = ((PersistentObject) value).getOID();
                return get(oid,
                           oid.getObjectType().getProperty(path.getName()));
            } else {
                throw new IllegalArgumentException
                    ("Path refers to attribute of opaque type: " + path);
            }
        }
    }

/*    private OID getLink(OID oid, Property prop, Object value) {
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
        }*/


    public PersistentObject add(final OID oid, Property prop,
                                final Object value) {
        if (LOG.isDebugEnabled()) {
            trace("add", new Object[] {oid, prop.getName(), value});
        }

        prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    addEvent(new AddEvent(Session.this, oid, role, value));

                    if (role.isReversable()) {
                        reverseUpdateNew(oid, role, (PersistentObject) value);
                    }
                }

                public void onAlias(Alias alias) {
                    add(oid, alias.getTarget(), value);
                }

                public void onLink(Link link) {
                    // should deal with link attributes here
                    throw new Error("not implemented");
                }
            });

        if (LOG.isDebugEnabled()) {
            untrace("add", null);
        }

        return null;
    }

    public void remove(final OID oid, Property prop, final Object value) {
        if (LOG.isDebugEnabled()) {
            trace("remove", new Object[] {oid, prop.getName(), value});
        }

        prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    addEvent(new RemoveEvent(Session.this, oid, role, value));

                    if (role.isComponent()) {
                        cascadeDelete(oid, (PersistentObject) value);
                    } else if (role.isReversable()) {
                        reverseUpdateOld
                            (oid, role, (PersistentObject) value);
                    }
                }

                public void onAlias(Alias alias) {
                    remove(oid, alias.getTarget(), value);
                }

                public void onLink(Link link) {
                    throw new Error("not implemented");
                }
            });

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


    public boolean isNew(OID oid) {
        return hasObjectData(oid) && getObjectData(oid).isNew();
    }


    public boolean isDeleted(OID oid) {
        return hasObjectData(oid) && getObjectData(oid).isDeleted();
    }


    public boolean isModified(OID oid) {
        return hasObjectData(oid) && getObjectData(oid).isModified();
    }


    public boolean isModified(OID oid, Property prop) {
        return hasObjectData(oid) &&
            getObjectData(oid).getPropertyData(prop).isModified();
    }


    /**
     * Performs all operations queued up by the session. This is automatically
     * called when necessary in order to insure that queries performed by the
     * datastore are consistent with the contents of the in memory data cache.
     **/
    public void flush() { flush(true, true, true); }

    void flushNubileAgile() { flush(true, true, false); }

    private void flush(boolean nubile, boolean agile, boolean senile) {
        if (LOG.isDebugEnabled()) {
            trace("flush", new Object[] {
                nubile ? Boolean.TRUE : Boolean.FALSE,
                agile ? Boolean.TRUE : Boolean.FALSE,
                senile ? Boolean.TRUE : Boolean.FALSE});
        }

        LinkedList written = new LinkedList();
        LinkedList deferred = new LinkedList();

        for (ListIterator li = m_evs.listIterator(0); li.hasNext(); ) {
            Event ev = (Event) li.next();
            ObjectData od = getObjectData(ev.getOID());

            if ((nubile && od.isNubile()) ||
                (agile && od.isAgile()) ||
                (senile && od.isSenile())) {
                m_engine.write(ev);
                written.add(ev);
            } else {
                deferred.add(ev);
            }
        }

        m_engine.flush();

        for (ListIterator li = written.listIterator(0); li.hasNext(); ) {
            Event ev = (Event) li.next();
            ev.sync();
        }

        m_evs = deferred;

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
        m_evs = new LinkedList();
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
            od = new ObjectData(this, POS.getPersistentObject(this, oid),
                                od.AGILE);
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

    private void appendEvent(Event ev) {
        m_evs.add(ev);
    }

    private void addEvent(ObjectEvent ev) {
        addEvent(ev, fetchObjectData(ev.getOID()));
    }

    private void addEvent(ObjectEvent ev, ObjectData od) {
        ev.setObjectData(od);
        appendEvent(ev);
        od.addEvent(ev);
    }

    private void addEvent(PropertyEvent ev) {
        PropertyData pd = fetchPropertyData(ev.getOID(), ev.getProperty());
        ev.setPropertyData(pd);
        appendEvent(ev);
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
            } else {
                do {
                    rs.load(this);
                } while (rs.next());
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
                 (this, new DataSet(this, getRetrieveQuery(oid, prop))));
        } else if (od.isNew()){
            pd = new PropertyData(od, prop, null);
        } else {
            RecordSet rs = m_engine.execute(getRetrieveQuery(oid, prop));
            OID value = null;
            while (rs.next()) {
                value = rs.load(this);
            }
            pd = od.getPropertyData(prop);
            if (pd == null) {
                throw new IllegalStateException
                    ("Query failed to retrieve property");
            }
        }

        return pd;
    }


    private Signature getRetrieveSignature(ObjectType type) {
        Signature result = new Signature(type);
        result.addDefaultProperties();
        return result;
    }

    private Query getRetrieveQuery(OID oid) {
        ObjectType type = oid.getObjectType();
        Signature sig = getRetrieveSignature(type);
        Parameter start = new Parameter(type, Path.get("__start__"));
        sig.addParameter(start);
        Query q = new Query
            (sig, new EqualsFilter(Path.get("__start__"), null));
        q.set(start, POS.getPersistentObject(this, oid));
        return q;
    }

    private Query getRetrieveQuery(OID oid, Property prop) {
        if (prop.isCollection()) {
            ObjectType type = prop.getType();
            Signature sig = getRetrieveSignature(type);
            Parameter start = new Parameter(prop.getContainer(),
                                            Path.get("__start__"));
            sig.addParameter(start);

            // should filter to associated object(s)
            // should deal with one way associations
            Filter f = new ContainsFilter
                (Path.get("__start__." + prop.getName()), null);
            Query q = new Query(sig, f);
            q.set(start, POS.getPersistentObject(this, oid));
            return q;
        } else {
            ObjectType type = oid.getObjectType();
            Signature sig = new Signature(type);
            sig.addPath(prop.getName());
            sig.addDefaultProperties(Path.get(prop.getName()));
            Parameter start = new Parameter(type,
                                            Path.get("__start__"));
            sig.addParameter(start);
            Query q = new Query
                (sig, new EqualsFilter(Path.get("__start__"), null));
            q.set(start, POS.getPersistentObject(this, oid));
            return q;
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
            LOG.debug(msg.toString());

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
            LOG.debug(buf.toString());
        }
    }

}
