package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.proto.ProtoException;
import com.arsdigita.persistence.proto.Session;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * DataObjectImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/06/16 $
 **/

class DataObjectImpl implements DataObject {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataObjectImpl.java#6 $ by $Author: ashah $, $DateTime: 2003/06/16 17:03:22 $";

    final static Logger s_log = Logger.getLogger(DataObjectImpl.class);

    private Session m_ssn;
    private OID m_oid;
    private Set m_observers = new HashSet();
    private Map m_disconnect = null;
    private boolean m_manualDisconnect = false;
    private Throwable m_invalidStack = null;
    private boolean m_valid = true;
    // originating transaction has terminated
    private boolean m_transactionDone = false;

    private final class ObserverEntry {

        private DataObserver m_observer;
        private Set m_firing = new HashSet();
        private Map m_waiting = new HashMap();

        private ObserverEntry(DataObserver observer) {
            m_observer = observer;
        }

        public DataObserver getObserver() {
            return m_observer;
        }

        public boolean isFiring(Class cls) {
            return m_firing.contains(cls);
        }

        public boolean isFiring(DataEvent event) {
            return isFiring(event.getClass());
        }

        public boolean isWaiting(DataEvent event) {
            for (Iterator it = m_waiting.entrySet().iterator();
                 it.hasNext(); ) {
                Map.Entry me = (Map.Entry) it.next();
                DataEvent value = (DataEvent) me.getValue();
                if (value.getClass().equals(event.getClass())) {
                    return true;
                }
            }

            return false;
        }

        public void setFiring(DataEvent event) {
            // unschedule event from others
            for (Iterator it = m_waiting.entrySet().iterator();
                 it.hasNext(); ) {
                Map.Entry me = (Map.Entry) it.next();
                DataEvent value = (DataEvent) me.getValue();
                if (value.getClass().equals(event.getClass())) {
                    m_waiting.remove(me.getKey());
                    break;
                }
            }

            m_firing.add(event.getClass());
            DataObjectImpl.this.m_firing = ObserverEntry.this;
        }

        public void scheduleEvent(Class cls, DataEvent event) {
            if (!isFiring(event)) { m_waiting.put(cls, event); }
        }

        public DataEvent clearFiring(DataEvent event) {
            m_firing.remove(event.getClass());
            DataObjectImpl.this.m_firing = null;
            return (DataEvent) m_waiting.remove(event.getClass());
        }

        public int hashCode() {
            return m_observer.hashCode();
        }

        public boolean equals(Object other) {
            if (other instanceof ObserverEntry && other != null) {
                return m_observer.equals(((ObserverEntry) other).m_observer);
            } else {
                return super.equals(other);
            }
        }

        public String toString() {
            return "Observer: " + m_observer;
        }

    }

    DataObjectImpl(ObjectType type) {
        m_oid = new OID(type);
    }

    DataObjectImpl(OID oid) {
        m_oid = oid;
    }

    void setSession(Session ssn) {
        m_ssn = ssn;
    }

    private com.arsdigita.persistence.proto.metadata.Property convert(String property) {
        return C.prop(getObjectType().getProperty(property));
    }

    public com.arsdigita.persistence.Session getSession() {
        return com.arsdigita.persistence.Session.getSessionFromProto(m_ssn);
    }

    public ObjectType getObjectType() {
        validate();
        return m_oid.getObjectType();
    }

    public OID getOID() {
        return m_oid;
    }

    public Object get(String property) {
        validate();
        Property prop = getObjectType().getProperty(property);
        if (prop == null) {
            throw new PersistenceException
                ("no such property: " + property + " for " + this);
        }
        if (prop.isCollection()) {
            if (isDisconnected()) {
                return new DataAssociationImpl
                    (SessionManager.getSession(), this, prop);
            } else {
                return new DataAssociationImpl(getSession(), this, prop);
            }
        }

        if (prop.isKeyProperty()) {
            return m_oid.get(property);
        } else if (m_oid.isInitialized()) {
            if (isDisconnected()) {
                doDisconnect();

                Object obj = m_disconnect.get(prop);

                if (m_disconnect.containsKey(prop)) {
                    if (!(obj instanceof DataObjectImpl)
                        || (((DataObjectImpl) obj).isValid())) {
                        return obj;
                    }
                }

                obj = SessionManager.getSession().
                    getProtoSession().get(this, convert(property));

                if (obj instanceof DataObjectImpl) {
                    DataObjectImpl dobj = (DataObjectImpl) obj;
                    dobj.disconnect();
                    if (!dobj.isValid()) {
                        throw new IllegalStateException
                            ("got invalid data object from session: " + obj);
                    }
                }
                m_disconnect.put(prop, obj);
                return obj;
            } else {
                return m_ssn.get(this, convert(property));
            }
        } else {
            return null;
        }
    }

    private void doDisconnect() {
        if (m_disconnect != null) { return; }
        m_disconnect = new HashMap();
        if (isDeleted()) { return; }
        if (!m_manualDisconnect) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("autodisconnect: " + getOID(), new Throwable());
            }
        }

        com.arsdigita.persistence.proto.Session ssn =
            SessionManager.getSession().getProtoSession();

        for (Iterator it = getObjectType().getProperties();
             it.hasNext(); ) {
            Property p = (Property) it.next();
            if (!p.isCollection()
                && !p.isKeyProperty()
                && p.getType().isSimple()) {
                m_disconnect.put(p, ssn.get(this, C.prop(p)));
            }
        }
    }

    public void set(String property, Object value) {
        validate(true);
        // all entry points for empty strings need to be converted to null
        if ("".equals(value)) { value = null; }
        try {
            Property prop = getObjectType().getProperty(property);
            if (prop == null) {
                throw new PersistenceException
                    ("no such property: " + property + " for " + this);
            }
            if (prop.isKeyProperty()) {
                m_oid.set(property, value);
                if (m_oid.isInitialized()) {
                    m_ssn.create(this);
                }
            } else {
                m_ssn.set(this, convert(property), value);
            }
        } catch (ProtoException pe) {
            throw new PersistenceException(pe);
        }
    }

    public boolean isNew() {
        validate();
        // handle calls to isNew before key is set
        return !m_oid.isInitialized() ||
            (m_ssn.isNew(this) && !m_ssn.isPersisted(this));
    }

    public boolean isDeleted() {
        validate();
        return m_ssn.isDeleted(this);
    }

    public boolean isDisconnected() {
        return m_manualDisconnect || m_transactionDone || !isValid();
    }

    void invalidate(boolean connectedOnly, boolean error) {
        if (error || (!connectedOnly && m_ssn.isModified(this))) {
            m_valid = false;
            if (s_log.isDebugEnabled()) {
                m_invalidStack = new Throwable();
            }
        } else if (connectedOnly && m_manualDisconnect) {
            doDisconnect();
        }

        m_transactionDone = true;
    }

    public void disconnect() {
        if (!m_oid.isInitialized()) {
            throw new PersistenceException
                ("can't disconnect uninitialized: " + this);
        }

        m_manualDisconnect = true;
        m_ssn.releaseObject(this);
    }

    public boolean isModified() {
        validate();
        return !m_ssn.isFlushed(this);
    }

    public boolean isPropertyModified(String name) {
        validate();
        return !m_ssn.isFlushed(this, convert(name));
    }

    public boolean isValid() {
        return m_valid;
    }

    private void validate() {
        validate(false);
    }

    private void validate(boolean write) {
        if (!isValid()) {
            if (s_log.isDebugEnabled()) {
                s_log.debug
                    ("invalid data object invalidated at: ", m_invalidStack);
            }
            throw new PersistenceException("invalid data object: " + this);
        }

        if (write && isDisconnected()) {
            throw new PersistenceException
                ("can not write to disconnected data object: " + this);
        }

        if (!isDisconnected()
            && !m_transactionDone
            && m_ssn != null
            && m_ssn != SessionManager.getSession().getProtoSession()) {
            throw new PersistenceException
                ("This data object: (" + this + ") is being accessed from "
                 + "another thread before its originating transaction has "
                 + "terminated.");
        }
    }

    public void delete() {
        validate(true);
        try {
            m_ssn.delete(this);
            m_ssn.flush();
            m_ssn.assertFlushed(this);
        } catch (ProtoException pe) {
            throw new PersistenceException(pe);
        }
    }

    public void specialize(String subtypeName) {
        validate();
        ObjectType subtype =
            MetadataRoot.getMetadataRoot().getObjectType(subtypeName);

        if (subtype == null) {
            throw new PersistenceException("No such type: " + subtypeName);
        }

        specialize(subtype);
    }

    public void specialize(ObjectType subtype) {
        validate();
        m_oid.specialize(subtype);
    }

    public void save() {
        validate(true);
        try {
            if (m_ssn.isDeleted(this)) {
                throw new PersistenceException("can't save a deleted object");
            }

            getSession().m_beforeFP.fireNow(new BeforeSaveEvent(this));

            if (!m_ssn.isFlushed(this)) {
                m_ssn.flush();
                assertFlushed();
            } else {
                // with no changes on the object fire after save directly
                getSession().m_afterFP.fireNow(new AfterSaveEvent(this));
            }
        } catch (ProtoException pe) {
            throw new PersistenceException(pe);
        }
    }

    private void assertFlushed() {
        // m_ssn.assertFlushed(this) doesn't work because of '~' properties
        for (Iterator it = getObjectType().getProperties();
             it.hasNext(); ) {
            Property p = (Property) it.next();
            if (!m_ssn.isFlushed(this, C.prop(p))) {
                // use m_ssn to generate the exception
                m_ssn.assertFlushed(this);
            }
        }
    }

    public void addObserver(DataObserver observer) {
        validate();
        if (observer == null) {
            throw new IllegalArgumentException("Can't add a null observer.");
        }
        ObserverEntry entry = new ObserverEntry(observer);
        if (!m_observers.contains(entry)) {
            if (m_firing != null) {
                throw new IllegalStateException
                    ("Can't add a new observer from within another " +
                     "observer.\n" +
                     "Trying to add: " + observer + "\n" +
                     "Currently firing: " + m_firing + "\n" +
                     "Current observers: " + m_observers);
            }
            m_observers.add(entry);
        }
    }

    private ObserverEntry m_firing = null;

    void scheduleObserver(DataEvent event) {
        for (Iterator it = m_observers.iterator(); it.hasNext(); ) {
            ObserverEntry entry = (ObserverEntry) it.next();
            DataObserver observer = entry.getObserver();
            if (event instanceof AfterEvent) {
                entry.scheduleEvent(((AfterEvent) event).getBefore(), event);
            }
        }
    }

    void fireObserver(DataEvent event) {
        for (Iterator it = m_observers.iterator(); it.hasNext(); ) {
            ObserverEntry entry = (ObserverEntry) it.next();
            final DataObserver observer = entry.getObserver();
            if (entry.isFiring(event)) {
                if (s_log.isDebugEnabled()) { s_log.debug("isFiring: " + event); }
                continue;
            }

            if (event instanceof AfterEvent) {
                AfterEvent ae = (AfterEvent) event;
                if (entry.isFiring(ae.getBefore())) {
                    entry.scheduleEvent(ae.getBefore(), event);
                    continue;
                }
            } else if (event instanceof BeforeEvent) {
                BeforeEvent be = (BeforeEvent) event;
                if (entry.isFiring(be.getAfter())) {
                    entry.scheduleEvent(be.getAfter(), event);
                    continue;
                }
            }

            try {
                // after events never delay firing
                if (event instanceof BeforeEvent) {
                    DataEvent waiting = entry.clearFiring(event);
                    if (waiting != null) { fireObserver(waiting); }
                }

                entry.setFiring(event);

                event.invoke(observer);

                DataEvent waiting = entry.clearFiring(event);
                if (waiting != null) { fireObserver(waiting); }
            } finally {
                entry.clearFiring(event);
            }
        }
    }

    public DataHandler setDataHandler(DataHandler handler) {
        // throw new Error("not implemented");
        return null;
    }

    public boolean equals(Object o) {
        if (o instanceof DataObject) {
            return m_oid.equals(((DataObject) o).getOID());
        }

        return false;
    }

    public int hashCode() {
        return m_oid.hashCode();
    }

    public String toString() {
        return m_oid.toString();
    }

}
