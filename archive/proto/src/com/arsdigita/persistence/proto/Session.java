package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

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
 * @version $Revision: #51 $ $Date: 2003/03/31 $
 **/

public class Session {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Session.java#51 $ by $Author: vadim $, $DateTime: 2003/03/31 15:13:10 $";

    private static final Logger LOG = Logger.getLogger(Session.class);

    private static final PersistentObjectSource POS =
        new PersistentObjectSource();

    private final Engine m_engine;
    private final QuerySource m_qs;

    private HashMap m_odata = new HashMap();

    private Set m_visiting = new HashSet();

    private EventStream m_events = new EventStream();
    private boolean m_eventsChanged = false;

    private Set m_violations = new HashSet();

    private final Set m_afterActivate = new HashSet();
    private final Set m_beforeFlush = new HashSet();
    private final Set m_afterFlush = new HashSet();

    private Event m_beforeFlushMarker = null;

    public Session(Engine engine, QuerySource source) {
        m_engine = engine;
        m_qs = source;
    }

    EventStream getEventStream() { return m_events; }

    private void cleanUp() {
        if (m_visiting.size() > 0) {
            LOG.warn("m_visiting not empty");
        }
        m_visiting.clear();
    }

    private RuntimeException handleException(RuntimeException re) {
        if (re instanceof ProtoException) {
            ProtoException pe = (ProtoException) re;
            if (pe.isInternal()) {
                if (LOG.isDebugEnabled()) { setLevel(0); }
                return new UncheckedWrapperException
                    ("internal persistence exception", pe);
            } else {
                pe.setInternal(true);
            }
        }

        return re;
    }

    public void create(Object obj) {
        LinkedList pending = new LinkedList();
        int l = LOG.isDebugEnabled() ? getLevel() : 0;
        try {
            createInternal(obj, pending);
            activate(pending);
        } catch (RuntimeException re) {
            if (LOG.isDebugEnabled()) { setLevel(l); }
            throw handleException(re);
        } finally {
            cleanUp();
        }
    }

    private void createInternal(Object obj, List pending) {
        if (LOG.isDebugEnabled()) {
            trace("create", new Object[] {obj});
        }

        ObjectData od = getObjectData(obj);
        if (od == null) {
            od = new ObjectData(this, obj, od.INFANTILE);
        } else if (!od.isDeleted()) {
            od.dump();
            throw new IllegalArgumentException
                ("Object already exists: " + obj);
        } else {
            od.setState(od.INFANTILE);
        }

        getAdapter(obj).setSession(obj, this);

        addEvent(new CreateEvent(this, obj), pending);

        PropertyMap props = getAdapter(obj).getProperties(obj);
        for (Iterator it = props.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            setInternal(obj, (Property) me.getKey(), me.getValue(), pending);
        }

        if (LOG.isDebugEnabled()) {
            untrace("create");
        }
    }

    public boolean delete(Object obj) {
        List pending = new LinkedList();
        int l = LOG.isDebugEnabled() ? getLevel() : 0;
        try {
            boolean result = deleteInternal(obj, pending);
            activate(pending);
            return result;
        } catch (RuntimeException re) {
            if (LOG.isDebugEnabled()) { setLevel(l); }
            throw handleException(re);
        } finally {
            cleanUp();
        }
    }

    private boolean deleteInternal(Object obj, List pending) {
        if (LOG.isDebugEnabled()) {
            trace("delete", new Object[] {obj});
        }

        boolean result;

        ObjectData od = fetchObjectData(obj);

        if (od == null) {
            result = false;
        } else if (m_visiting.contains(od)) {
            result = false;
        } else {
            m_visiting.add(od);

            ObjectType type = getObjectType(obj);

            for (Iterator it = type.getRoles().iterator();
                 it.hasNext(); ) {
                Role role = (Role) it.next();
                if (role.isCollection()) {
                    clearInternal(obj, role, pending);
                }
            }

            for (Iterator it = type.getRoles().iterator();
                 it.hasNext(); ) {
                Role role = (Role) it.next();

                if (!role.isCollection() && get(obj, role) != null) {
                    setInternal(obj, role, null, pending);
                }
            }

            m_visiting.remove(od);

            addEvent(new DeleteEvent(this, obj), pending);
            result = true;
        }

        if (LOG.isDebugEnabled()) {
            untrace("delete", result);
        }

        return result;
    }

    public Object retrieve(ObjectType obj, Object id) {
        PersistentCollection pc = retrieve(m_qs.getQuery(obj, id));
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
    private void cascadeDelete(Object container, Object containee,
                               List pending) {
        ObjectData containerOD = fetchObjectData(container);
        boolean me = false;

        if (!m_visiting.contains(containerOD)) {
            me = true;
            m_visiting.add(containerOD);
        }

        deleteInternal(containee, pending);

        if (me) { m_visiting.remove(containerOD); }
    }

    /**
     * Update the reverse role of an old target object. After updating a
     * reversible role, either set or remove, the reverse role for the old
     * target needs to be set to null.
     *
     * @param event the event causing this update
     * @param target the old target of a role that has been updated
     **/
    private void reverseUpdateOld(PropertyEvent event, Object target,
                                  List pending) {
        Object source = event.getObject();
        Role role = (Role) event.getProperty();
        Role rev = role.getReverse();

        if (rev.isCollection()) {
            addEvent(new RemoveEvent(this, target, rev, source, event),
                     pending);
        } else {
            addEvent(new SetEvent(this, target, rev, null, event),
                     pending);
        }
    }

    /**
     * Update the reverse role of a new target object. After updating a
     * reversible role, either set or add, the reverse role for the new target
     * needs to be set to the new source. In addition, the new target's old
     * source needs to be updated so its role target is null.
     **/
    private void reverseUpdateNew(PropertyEvent event, List pending) {
        Object source = event.getObject();
        Role role = (Role) event.getProperty();
        Object target = event.getArgument();
        Role rev = role.getReverse();
        if (rev.isCollection()) {
            addEvent(new AddEvent(this, target, rev, source, event), pending);
        } else {
            Object old = get(target, rev);
            if (old != null) {
                if (role.isCollection()) {
                    addEvent(new RemoveEvent(this, old, role, target, event),
                             pending);
                } else {
                    addEvent(new SetEvent(this, old, role, null, event),
                             pending);
                }
            }

            addEvent(new SetEvent(this, target, rev, source, event), pending);
        }
    }

    public void set(final Object obj, Property prop, final Object value) {
        List pending = new LinkedList();
        int l = LOG.isDebugEnabled() ? getLevel() : 0;
        try {
            setInternal(obj, prop, value, pending);
            activate(pending);
        } catch (RuntimeException re) {
            if (LOG.isDebugEnabled()) { setLevel(l); }
            throw handleException(re);
        } finally {
            cleanUp();
        }
    }

    private void setInternal(final Object obj, Property prop,
                             final Object value, final List pending) {
        if (LOG.isDebugEnabled()) {
            trace("set", new Object[] {obj, prop.getName(), value});
        }

        prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    Object old = null;
                    if (role.isComponent() || role.isReversable()) {
                        old = get(obj, role);
                    }

                    PropertyEvent ev;

                    try {
                        ev = new SetEvent(Session.this, obj, role, value);
                    } catch (TypeException te) {
                        te.setInternal(false);
                        throw te;
                    }

                    if (role.isReversable()) {
                        if (old != null) { reverseUpdateOld(ev, old, pending);}
                        if (value != null) { reverseUpdateNew(ev, pending); }
                    }

                    addEvent(ev, pending);

                    if (role.isComponent()) {
                        if (old != null) { cascadeDelete(obj, old, pending); }
                    }
                }

                public void onAlias(Alias alias) {
                    setInternal(obj, alias.getTarget(), value, pending);
                }

                public void onLink(Link link) {
                    throw new UnsupportedOperationException
			("cannot set links");
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

        if (!getObjectType(obj).hasKey()) {
            return getProperties(obj).get(prop);
        }

        prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    PropertyData pd = fetchPropertyData(obj, role);
                    result[0] = pd.get();
                }

                public void onAlias(Alias alias) {
                    result[0] = get(obj, alias.getTarget());
                }

                public void onLink(Link link) {
		    result[0] = retrieve(getQuery(obj, link));
                }
            });

        if (LOG.isDebugEnabled()) {
            untrace("get", result[0]);
        }

        return result[0];
    }


    private Query getQuery(Object obj, Link link) {
	return getQuery(obj, link, null);
    }

    private Query getQuery(Object obj, Link link, Object value) {
	// XXX: Should probably be replaced with a proper query
	Query q = m_qs.getQuery(link.getTo().getType());
	Parameter from = new Parameter
	    (link.getFrom().getType(), Path.get("__from__"));
	q.getSignature().addParameter(from);
	q.set(from, obj);
	Source s = new Source(link.getLinkType(), Path.get("link"));
	q.getSignature().addSource(s);

	q.getSignature().addPath("link");

	for (Iterator it = link.getLinkType().getProperties().iterator();
	     it.hasNext(); ) {
	    Property prop = (Property) it.next();
	    if (Signature.isAttribute(prop)) {
		q.getSignature().addPath(Path.add("link", prop.getName()));
	    }
	}

	Parameter to = null;
	if (value != null) {
	    to = new Parameter
		(link.getTo().getType(), Path.get("__to__"));
	    q.getSignature().addParameter(to);
	    q.set(to, value);
	}

	return new Query
	    (q, new AndFilter
	     (new EqualsFilter
	      (Path.add("link", link.getFrom().getName()),
	       from.getPath()),
	      new EqualsFilter
	      (Path.add("link", link.getTo().getName()),
	       to == null ? null : to.getPath())));
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
        List pending = new LinkedList();
        int l = LOG.isDebugEnabled() ? getLevel() : 0;
        try {
            Object result = addInternal(obj, prop, value, pending);
            activate(pending);
            return result;
        } catch (RuntimeException re) {
            if (LOG.isDebugEnabled()) { setLevel(l); }
            throw handleException(re);
        } finally {
            cleanUp();
        }
    }

    private Object addInternal(final Object obj, Property prop,
                               final Object value, final List pending) {
        if (LOG.isDebugEnabled()) {
            trace("add", new Object[] {obj, prop.getName(), value});
        }

	final Object[] result = { null };

        prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    PropertyEvent ev;
                    try {
                        ev = new AddEvent(Session.this, obj, role, value);
                    } catch (TypeException te) {
                        te.setInternal(false);
                        throw te;
                    }

                    if (role.isReversable()) { reverseUpdateNew(ev, pending); }
                    addEvent(ev, pending);
                }

                public void onAlias(Alias alias) {
                    addInternal(obj, alias.getTarget(), value, pending);
                }

                public void onLink(Link link) {
                    PropertyMap pmap = new PropertyMap(link.getLinkType());
		    pmap.put(link.getFrom(), obj);
		    pmap.put(link.getTo(), value);
		    result[0] = getObject(pmap);
		    createInternal(result[0], pending);
                }
            });

        if (LOG.isDebugEnabled()) {
            untrace("add", result[0]);
        }

        return result[0];
    }

    public void remove(final Object obj, Property prop, final Object value) {
        List pending = new LinkedList();
        int l = LOG.isDebugEnabled() ? getLevel() : 0;
        try {
            removeInternal(obj, prop, value, pending);
            activate(pending);
        } catch (RuntimeException re) {
            if (LOG.isDebugEnabled()) { setLevel(l); }
            throw handleException(re);
        } finally {
            cleanUp();
        }
    }


    private void removeInternal(final Object obj, Property prop,
                                final Object value, final List pending) {
        if (LOG.isDebugEnabled()) {
            trace("remove", new Object[] {obj, prop.getName(), value});
        }

        prop.dispatch(new Property.Switch() {
                public void onRole(Role role) {
                    PropertyEvent ev;
                    try {
                        ev = new RemoveEvent(Session.this, obj, role, value);
                    } catch (TypeException te) {
                        te.setInternal(false);
                        throw te;
                    }

                    addEvent(ev, pending);

                    if (role.isReversable()) {
                        reverseUpdateOld(ev, value, pending);
                    }

                    if (role.isComponent()) {
                        cascadeDelete(obj, value, pending);
                    }
                }

                public void onAlias(Alias alias) {
                    removeInternal(obj, alias.getTarget(), value, pending);
                }

                public void onLink(Link link) {
		    Query q = getQuery(obj, link, value);
		    Cursor c = retrieve(q).getDataSet().getCursor();
		    while (c.next()) {
			deleteInternal(c.get(), pending);
		    }
                }
            });

        if (LOG.isDebugEnabled()) {
            untrace("remove");
        }
    }


    public void clear(Object obj, Property prop) {
        List pending = new LinkedList();
        int l = LOG.isDebugEnabled() ? getLevel() : 0;
        try {
            clearInternal(obj, prop, pending);
            activate(pending);
        } catch (RuntimeException re) {
            if (LOG.isDebugEnabled()) { setLevel(l); }
            throw handleException(re);
        } finally {
            cleanUp();
        }
    }

    private void clearInternal(Object obj, Property prop, List pending) {
       if (LOG.isDebugEnabled()) {
            trace("clear", new Object[] {obj, prop.getName()});
        }

        PersistentCollection pc =
            (PersistentCollection) get(obj, prop);
        Cursor c = pc.getDataSet().getCursor();
        while (c.next()) {
            removeInternal(obj, prop, c.get(), pending);
        }

        if (LOG.isDebugEnabled()) {
            untrace("clear");
        }
    }

    public static Object getObject(PropertyMap pmap) {
	Adapter ad = Adapter.getAdapter(pmap.getObjectType());
	return ad.getObject(pmap.getObjectType(), pmap);
    }

    public static ObjectType getObjectType(Object obj) {
        return getAdapter(obj).getObjectType(obj);
    }

    private static Adapter getAdapter(Object obj) {
        return Adapter.getAdapter(obj.getClass());
    }

    public static ObjectMap getObjectMap(Object obj) {
        return Root.getRoot().getObjectMap(getAdapter(obj).getObjectType(obj));
    }

    public static PropertyMap getProperties(Object obj) {
        return getAdapter(obj).getProperties(obj);
    }

    /**
     * @return true iff this session has outstanding events
     **/
    public boolean isFlushed() {
        return m_events.size() == 0;
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

    public boolean isFlushed(Object obj) {
        if (!hasObjectData(obj)) {
            return true;
        }

        ObjectData od = getObjectData(obj);
        return od.isFlushed();
    }

    public void assertFlushed(Object obj) {
        if (!isFlushed(obj)) {
            if (LOG.isInfoEnabled()) {
                LOG.info("nulled not null property datas: " + m_violations);
            }
            throw new ProtoException("all events not flushed for: " + obj,
                                     false);
        }
    }

    public boolean isFlushed(Object obj, Property prop) {
        if (!hasObjectData(obj)) {
            return true;
        }

        PropertyData pd = getObjectData(obj).getPropertyData(prop);
        if (pd == null) {
            return true;
        }

        return pd.isFlushed();
    }

    public boolean isPersisted(Object obj) {
        return hasObjectData(obj) && !getObjectData(obj).isInfantile();
    }

    private static final Integer s_zero = new Integer(0);

    private void process(Collection processors, Collection events) {
        for (Iterator it = processors.iterator(); it.hasNext(); ) {
            EventProcessor ep = (EventProcessor) it.next();

            for (Iterator it2 = events.iterator(); it2.hasNext(); ) {
                Event ev = (Event) it2.next();
                ep.write(ev);
            }

            ep.flush();
        }
    }

    private void computeFlushability() {
        for (Iterator it = m_events.iterator(); it.hasNext(); ) {
            ((Event) it.next()).m_flushable = true;
        }

        // find unflushable events
        // the source of unflushability is not null properties that
        // have not been set
        List queue = new LinkedList();
        for (Iterator pds = m_violations.iterator(); pds.hasNext(); ) {
            PropertyData pd = (PropertyData) pds.next();
            for (Iterator evs = pd.getDependentEvents(); evs.hasNext(); ) {
                queue.add(evs.next());
            }
        }

        // recursively mark reachable events as unflushable
        while (queue.size() != 0) {
            Event ev = (Event) queue.remove(0);
            if (ev.m_flushable) {
                ev.m_flushable = false;
                for (Iterator evs = ev.getDependentEvents(); evs.hasNext(); ) {
                    queue.add(evs.next());
                }
            }
        }
    }

    /**
     * Performs all operations queued up by the session. This is automatically
     * called when necessary in order to insure that queries performed by the
     * datastore are consistent with the contents of the in memory data cache.
     **/
    public void flush() {
        int l = LOG.isDebugEnabled() ? getLevel() : 0;
        try {
            flushInternal();
        } catch (RuntimeException re) {
            if (LOG.isDebugEnabled()) { setLevel(l); }
            throw handleException(re);
        }
    }

    public void flushAll() {
        flush();
        if (!isFlushed()) {
            throw new IllegalStateException("XXX");
        }
    }

    private void flushInternal() {
        if (LOG.isDebugEnabled()) {
            trace("flush", new Object[] {});
        }

        boolean flushed = m_beforeFlushMarker != null;
        List events = m_events.getEvents();
        for (int i = 0; i < events.size(); i++) {
            Event ev = (Event) events.get(i);
            if (flushed) {
                if (m_beforeFlushMarker.equals(ev)) { flushed = false; }
                continue;
            }

            m_beforeFlushMarker = ev;

            for (Iterator eps = m_beforeFlush.iterator(); eps.hasNext(); ) {
                EventProcessor ep = (EventProcessor) eps.next();
                ep.write(ev);
            }
        }

        for (Iterator it = m_beforeFlush.iterator(); it.hasNext(); ) {
            EventProcessor ep = (EventProcessor) it.next();
            ep.flush();
        }

        List written = new LinkedList();

        Event prev = null;
        for (Iterator it = m_events.iterator(); it.hasNext(); ) {
            Event ev = (Event) it.next();
            if (ev.m_flushable) {
                m_engine.write(ev);
                written.add(ev);
                it.remove();
                if (ev.equals(m_beforeFlushMarker)) {
                    m_beforeFlushMarker = prev;
                }
            } else {
                prev = ev;
            }
        }

        m_engine.flush();

        for (Iterator it = written.iterator(); it.hasNext(); ) {
            Event ev = (Event) it.next();
            m_eventsChanged = true;
            ev.sync();
        }

        computeFlushability();

        process(m_afterFlush, written);

        if (LOG.isInfoEnabled()) {
            if (m_events.size() > 0) {
                LOG.info("unflushed: " + m_events.size());
            }
        }

        if (LOG.isDebugEnabled()) {
            untrace("flush");
        }
    }


    /**
     * Renders all changes made within the transaction permanent and ends the
     * transaction.
     **/

    public void commit() {
        flushAll();

        m_odata.clear();
        m_events.clear();
        m_visiting.clear();
        m_violations.clear();
        commitEventProcessors();
        m_engine.commit();
        m_beforeFlushMarker = null;
        if (LOG.isDebugEnabled()) { setLevel(0); }
    }

    private void commitEventProcessors() {
        for (Iterator eps = m_beforeFlush.iterator(); eps.hasNext(); ) {
            EventProcessor ep = (EventProcessor) eps.next();
            ep.commit();
        }
        for (Iterator eps = m_afterFlush.iterator(); eps.hasNext(); ) {
            EventProcessor ep = (EventProcessor) eps.next();
            ep.commit();
        }
    }

    /**
     * Reverts all changes made within the transaction and ends the
     * transaction.
     **/

    public void rollback() {
        // should properly roll back java state
        m_odata.clear();
        m_events.clear();
        m_visiting.clear();
        m_violations.clear();
        rollbackEventProcessors();
        m_engine.rollback();
        m_beforeFlushMarker = null;
        if (LOG.isDebugEnabled()) { setLevel(0); }
    }

    private void rollbackEventProcessors() {
        for (Iterator eps = m_beforeFlush.iterator(); eps.hasNext(); ) {
            EventProcessor ep = (EventProcessor) eps.next();
            ep.rollback();
        }
        for (Iterator eps = m_afterFlush.iterator(); eps.hasNext(); ) {
            EventProcessor ep = (EventProcessor) eps.next();
            ep.rollback();
        }
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
            od = new ObjectData(this, obj, od.NUBILE);
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

    private void addEvent(Event ev, List pending) {
        ev.inject();
        pending.add(ev);
    }

    private void activate(List pending) {
        for (Iterator it = pending.iterator(); it.hasNext(); ) {
            Event ev = (Event) it.next();
            m_eventsChanged = true;
            ev.activate();
        }

        List activated = new ArrayList();
        activated.addAll(pending);
        pending.clear();

        computeFlushability();
        // XXX: m_visiting. size() should be 0. but we can't check right now
        // because exceptions don't clear m_visiting on the way out of
        // every top level method
        m_visiting.clear();

        process(m_afterActivate, activated);
    }

    static Object getSessionKey(Object obj) {
        Adapter ad = getAdapter(obj);
        return ad.getSessionKey(obj);
    }

    Object getObject(Object key) {
        if (m_odata.containsKey(key)) {
            return ((ObjectData) m_odata.get(key)).getObject();
        } else {
            return null;
        }
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
            RecordSet rs = m_engine.execute(m_qs.getQuery(obj));
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

    PropertyData fetchPropertyData(Object obj, Property prop) {
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
                 (this, new DataSet(this, m_qs.getQuery(obj, prop))));
        } else if (od.isNew()){
            pd = new PropertyData(od, prop, null);
        } else {
            RecordSet rs = m_engine.execute(m_qs.getQuery(obj, prop));
            boolean found = false;
            while (rs.next()) {
                found = true;
                rs.load(this);
            }

            if (!found) {
                throw new IllegalStateException
                    ("Query failed to return any results");
            }

            pd = od.getPropertyData(prop);
            if (pd == null) {
                throw new IllegalStateException
                    ("Query failed to retrieve property");
            }
        }

        return pd;
    }

    void addViolation(PropertyData pd) { m_violations.add(pd); }

    void removeViolation(PropertyData pd) { m_violations.remove(pd); }

    private void check(EventProcessor ep) {
        if (ep == null) {
            throw new IllegalArgumentException("null event processor");
        }
    }

    public void addAfterActivate(EventProcessor ep) {
        check(ep);
        m_afterActivate.add(ep);
    }

    public void addBeforeFlush(EventProcessor ep) {
        check(ep);
        m_beforeFlush.add(ep);
    }

    public void addAfterFlush(EventProcessor ep) {
        check(ep);
        m_afterFlush.add(ep);
    }

    void dump() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        dump(pw);
        pw.flush();
        LOG.debug(sw.toString());
    }

    void dump(PrintWriter out) {
        for (Iterator it = m_odata.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();

            Object key = me.getKey();
            out.print(key);
            out.println(":");

            ObjectData od = (ObjectData) me.getValue();
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
        if (level < 0) { level = 0; }
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
