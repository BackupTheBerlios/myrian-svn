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
 * @version $Revision: #24 $ $Date: 2003/02/12 $
 **/

public class Session {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Session.java#24 $ by $Author: rhs $, $DateTime: 2003/02/12 17:18:17 $";

    private static final Logger LOG = Logger.getLogger(Session.class);

    private static final PersistentObjectSource POS =
        new PersistentObjectSource();

    private final Engine m_engine = Engine.getInstance(this);
    private HashMap m_odata = new HashMap();

    // These are kept up to date by code in Event.java
    LinkedList m_events = new LinkedList();

    public void create(Object obj) {
        if (LOG.isDebugEnabled()) {
            trace("create", new Object[] {obj});
        }

        ObjectData od = getObjectData(obj);
        if (od == null) {
            od = new ObjectData(this, obj, od.NUBILE);
        } else if (!od.isDeleted()) {
            throw new IllegalArgumentException
                ("Object already exists: " + obj);
        } else {
            od.setState(od.NUBILE);
        }

        // This will have problems if there was a preexisting object with the
        // same identity, but different type. Also if the object already
        // existed and was modified the new object will pick up all the
        // changes made to the old one. Not sure what to do about this except
        // perhaps disallow it at some point.
        addEvent(new CreateEvent(this, obj), od);

        if (LOG.isDebugEnabled()) {
            untrace("create");
        }
    }


    public boolean delete(Object obj) {
        if (LOG.isDebugEnabled()) {
            trace("delete", new Object[] {obj});
        }

        boolean result;

        ObjectData od = fetchObjectData(obj);

        if (od == null) {
            result = false;
        } else if (od.isSenile()) {
            result = false;
        } else {
            od.setState(od.SENILE);

            ObjectType type = getObjectType(obj);
            Collection keys = type.getKeyProperties();

            for (Iterator it = type.getRoles().iterator();
                 it.hasNext(); ) {
                Role role = (Role) it.next();
                if (role.isCollection()) {
                    clear(obj, role);
                }
            }

            for (Iterator it = type.getProperties().iterator();
                 it.hasNext(); ) {
                Role role = (Role) it.next();

                if (!role.isCollection() && !keys.contains(role)) {
                    set(obj, role, null);
                }
            }

            addEvent(new DeleteEvent(this, obj));
            result = true;
        }

        if (LOG.isDebugEnabled()) {
            untrace("delete", result);
        }

        return result;
    }


    public Object retrieve(ObjectType obj, Object id) {
        PersistentCollection pc = retrieve(getQuery(obj, id));
        Cursor c = pc.getDataSet().getCursor();
        if (c.next()) {
            Object result = c.get();
            if (c.next()) {
                throw new IllegalStateException
                    ("query returned more than one row");
            }
            return result;
        } else {
            return null;
        }
    }


    public PersistentCollection retrieve(Query query) {
        return POS.getPersistentCollection(this, new DataSet(this, query));
    }


    /**
     * Cascades delete from container to the containee. The containee
     * will not be deleted in all cases.
     **/
    private void cascadeDelete(Object container, Object containee) {
        ObjectData containerOD = fetchObjectData(container);
        boolean me = false;
        if (!containerOD.isSenile()) {
            me = true;
            containerOD.setState(ObjectData.SENILE);
        }

        delete(containee);

        if (me) { containerOD.setState(ObjectData.SENILE); }
    }

    /**
     * Update the reverse role of an old target object. After updating a
     * reversible role, either set or remove, the reverse role for the old
     * target needs to be set to null.
     *
     * @param source the source of a role that has been updated
     * @param role the role that has been updated
     * @param target the old target of a role that has been updated
     **/
    private void reverseUpdateOld(Object source, Role role, Object target) {
        Role rev = role.getReverse();

        if (rev.isCollection()) {
            addEvent(new RemoveEvent(this, target, rev, source));
        } else if (rev.isNullable()) {
            addEvent(new SetEvent(this, target, rev, null));
        } else if (!fetchObjectData(target).isSenile() && !isDeleted(target)) {
            throw new IllegalStateException("can't set 1..1 to null");
        }
    }

    /**
     * Update the reverse role of an new target object. After updating a
     * reversible role, either set or add, the reverse role for the new target
     * needs to be set to the new source. In addition, the new targets' old
     * source needs to be updated so its role target is null.
     *
     * @param source the source of a role that has been updated
     * @param role the role that has been updated
     * @param targetObj the new target of a role that has been updated
     **/
    private void reverseUpdateNew(Object source, Role role, Object target) {
        Role rev = role.getReverse();
        if (rev.isCollection()) {
            addEvent(new AddEvent(this, target, rev, source));
        } else {
            Object old = get(target, rev);
            if (old != null) {
                if (role.isNullable()) {
                    addEvent(new SetEvent(this, old, role, null));
                } else if (!fetchObjectData(old).isSenile() &&
                           !isDeleted(old)) {
                    throw new IllegalStateException("can't set 1..1 to null");
                }
            }

            addEvent(new SetEvent(this, target, rev, source));
        }
    }


    public void set(final Object obj, Property prop, final Object value) {
        if (LOG.isDebugEnabled()) {
            trace("set", new Object[] {obj, prop.getName(), value});
        }

        prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    Object old = null;
                    if (role.isComponent() || role.isReversable()) {
                        old = get(obj, role);
                    }

                    addEvent(new SetEvent(Session.this, obj, role, value));

                    if (role.isComponent()) {
                        if (old != null) {
                            cascadeDelete(obj, value);
                        }
                    }

                    if (role.isReversable()) {
                        if (old != null) {
                            reverseUpdateOld(obj, role, old);
                        }

                        if (value != null) {
                            reverseUpdateNew(obj, role, value);
                        }
                    }
                }

                public void onAlias(Alias alias) {
                    set(obj, alias.getTarget(), value);
                }

                public void onLink(Link link) {
                    throw new Error("not implemented");
                }
            });

        if (LOG.isDebugEnabled()) {
            untrace("set");
        }
    }


    public Object get(final Object obj, Property prop) {
        if (LOG.isDebugEnabled()) {
            trace("get", new Object[] {obj, prop.getName()});
        }

        final Object[] result = {null};

        prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    // Should probably use seperate subclass of property for
                    // this dispatch.
                    if (getObjectMap(obj).getKeyProperties().contains(role)) {
                        Adapter ad = getAdapter(obj);
                        result[0] = ad.get(obj, role);
                    } else {
                        PropertyData pd = fetchPropertyData(obj, role);
                        result[0] = pd.get();
                    }
                }

                public void onAlias(Alias alias) {
                    result[0] = get(obj, alias.getTarget());
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

    Object get(Object start, Path path) {
        if (path.getParent() == null) {
            return get(start,
                       getObjectType(start).getProperty(path.getName()));
        } else {
            Object value = get(start, path.getParent());
            return get(value,
                       getObjectType(value).getProperty(path.getName()));
        }
    }


    public Object add(final Object obj, Property prop, final Object value) {
        if (LOG.isDebugEnabled()) {
            trace("add", new Object[] {obj, prop.getName(), value});
        }

        prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    addEvent(new AddEvent(Session.this, obj, role, value));

                    if (role.isReversable()) {
                        reverseUpdateNew(obj, role, value);
                    }
                }

                public void onAlias(Alias alias) {
                    add(obj, alias.getTarget(), value);
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

    public void remove(final Object obj, Property prop, final Object value) {
        if (LOG.isDebugEnabled()) {
            trace("remove", new Object[] {obj, prop.getName(), value});
        }

        prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    addEvent(new RemoveEvent(Session.this, obj, role, value));

                    if (role.isComponent()) {
                        cascadeDelete(obj, value);
                    } else if (role.isReversable()) {
                        reverseUpdateOld(obj, role, value);
                    }
                }

                public void onAlias(Alias alias) {
                    remove(obj, alias.getTarget(), value);
                }

                public void onLink(Link link) {
                    throw new Error("not implemented");
                }
            });

        if (LOG.isDebugEnabled()) {
            untrace("remove");
        }
    }


    public void clear(Object obj, Property prop) {
        if (LOG.isDebugEnabled()) {
            trace("clear", new Object[] {obj, prop.getName()});
        }

        PersistentCollection pc =
            (PersistentCollection) get(obj, prop);
        Cursor c = pc.getDataSet().getCursor();
        while (c.next()) {
            remove(obj, prop, c.get());
        }

        if (LOG.isDebugEnabled()) {
            untrace("clear");
        }
    }

    public ObjectType getObjectType(Object obj) {
        return getAdapter(obj).getObjectType(obj);
    }

    private Adapter getAdapter(Object obj) {
        return Adapter.getAdapter(obj.getClass());
    }

    public ObjectMap getObjectMap(Object obj) {
        return Root.getRoot().getObjectMap(getAdapter(obj).getObjectType(obj));
    }

    public boolean isNew(Object obj) {
        return hasObjectData(obj) && getObjectData(obj).isNew();
    }


    public boolean isDeleted(Object obj) {
        return hasObjectData(obj) && getObjectData(obj).isDeleted();
    }


    public boolean isModified(Object obj) {
        return hasObjectData(obj) && getObjectData(obj).isModified();
    }


    public boolean isModified(Object obj, Property prop) {
        return hasObjectData(obj) &&
            getObjectData(obj).getPropertyData(prop).isModified();
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

        for (ListIterator li = m_events.listIterator(0); li.hasNext(); ) {
            Event ev = (Event) li.next();
            ObjectData od = getObjectData(ev.getObject());

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

        m_events = deferred;

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
        m_events = new LinkedList();
        m_engine.rollback();
    }

    Engine getEngine() {
        return m_engine;
    }

    void load(Object obj, Property prop, Object value) {
        if (LOG.isDebugEnabled()) {
            trace("load", new Object[] {obj, prop.getName(), value});
        }

        ObjectData od = getObjectData(obj);
        if (od == null) {
            // We may need to change the signature of this to read in enough
            // data to allow type negotiation to happen properly without
            // requiring another db hit.
            od = new ObjectData(this, obj, od.AGILE);
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
        m_events.add(ev);
    }

    private void addEvent(ObjectEvent ev) {
        addEvent(ev, fetchObjectData(ev.getObject()));
    }

    private void addEvent(ObjectEvent ev, ObjectData od) {
        ev.setObjectData(od);
        appendEvent(ev);
        od.addEvent(ev);
    }

    private void addEvent(PropertyEvent ev) {
        PropertyData pd = fetchPropertyData(ev.getObject(), ev.getProperty());
        ev.setPropertyData(pd);
        appendEvent(ev);
        pd.addEvent(ev);
    }

    private Object getSessionKey(Object obj) {
        Adapter ad = getAdapter(obj);
        return ad.getSessionKey(obj);
    }

    boolean hasObjectData(Object obj) {
        return m_odata.containsKey(getSessionKey(obj));
    }

    ObjectData getObjectData(Object obj) {
        return (ObjectData) m_odata.get(getSessionKey(obj));
    }

    void removeObjectData(Object obj) {
        m_odata.remove(getSessionKey(obj));
    }

    void addObjectData(ObjectData odata) {
        m_odata.put(getSessionKey(odata.getObject()), odata);
    }

    PropertyData getPropertyData(Object obj, Property prop) {
        ObjectData od = getObjectData(obj);
        if (od != null) {
            return od.getPropertyData(prop);
        } else {
            return null;
        }
    }

    private ObjectData fetchObjectData(Object obj) {
        if (!hasObjectData(obj)) {
            RecordSet rs = m_engine.execute(getRetrieveQuery(obj));
            // Cache non-existent objects
            if (!rs.next()) {
                m_odata.put(obj, null);
            } else {
                do {
                    rs.load(this);
                } while (rs.next());
            }
        }

        ObjectData od = getObjectData(obj);
        if (od != null && od.isDeleted()) {
            return null;
        } else {
            return od;
        }
    }

    private PropertyData fetchPropertyData(Object obj, Property prop) {
        ObjectData od = fetchObjectData(obj);
        if (od == null) {
            throw new IllegalArgumentException("No such object: " + obj);
        }

        PropertyData pd;

        if (od.hasPropertyData(prop)) {
            pd = od.getPropertyData(prop);
        } else if (prop.isCollection()) {
            pd = new PropertyData
                (od, prop, POS.getPersistentCollection
                 (this, new DataSet(this, getRetrieveQuery(obj, prop))));
        } else if (od.isNew()){
            pd = new PropertyData(od, prop, null);
        } else {
            RecordSet rs = m_engine.execute(getRetrieveQuery(obj, prop));
            while (rs.next()) {
                rs.load(this);
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

    private Query getQuery(ObjectType type, Object id) {
        Signature sig = getRetrieveSignature(type);
        ObjectMap map = Root.getRoot().getObjectMap(type);
        Collection keys = map.getKeyProperties();
        if (keys.size() != 1) {
            throw new IllegalArgumentException("type has more than one key");
        }
        Property key = (Property) keys.iterator().next();
        Parameter idParam = new Parameter(key.getType(), Path.get("__id__"));
        sig.addParameter(idParam);
        Filter f =
            new EqualsFilter(Path.get(key.getName()), idParam.getPath());
        Query result = new Query(sig, f);
        result.set(idParam, id);
        return result;
    }

    private Query getRetrieveQuery(Object obj) {
        ObjectType type = getObjectType(obj);
        Signature sig = getRetrieveSignature(type);
        Parameter start = new Parameter(type, Path.get("__start__"));
        sig.addParameter(start);
        Query q = new Query
            (sig, new EqualsFilter(Path.get("__start__"), null));
        q.set(start, obj);
        return q;
    }

    private Query getRetrieveQuery(Object obj, Property prop) {
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
            q.set(start, obj);
            return q;
        } else {
            ObjectType type = getObjectType(obj);
            Signature sig = new Signature(type);
            sig.addPath(prop.getName());
            sig.addDefaultProperties(Path.get(prop.getName()));
            Parameter start = new Parameter(type, Path.get("__start__"));
            sig.addParameter(start);
            Query q = new Query
                (sig, new EqualsFilter(Path.get("__start__"), null));
            q.set(start, obj);
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
