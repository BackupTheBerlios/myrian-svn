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
 * @version $Revision: #14 $ $Date: 2003/03/28 $
 **/

class DataObjectImpl implements DataObject {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataObjectImpl.java#14 $ by $Author: ashah $, $DateTime: 2003/03/28 15:46:14 $";

    private final static Logger s_log = Logger.getLogger(DataObjectImpl.class);

    private Session m_ssn;
    private OID m_oid;
    private Set m_observers = new HashSet();
    private boolean m_isDisconnected = false;

    private static final class ObserverEntry {

        private DataObserver m_observer;
        private Set m_firing = new HashSet();

        ObserverEntry(DataObserver observer) {
            m_observer = observer;
        }

        public DataObserver getObserver() {
            return m_observer;
        }

        public boolean isFiring(DataEvent event) {
            return m_firing.contains(event.getClass());
        }

        public void setFiring(DataEvent event) {
            m_firing.add(event.getClass());
        }

        public void clearFiring(DataEvent event) {
            m_firing.remove(event.getClass());
        }

        public int hashCode() {
            return m_observer.hashCode();
        }

        public boolean equals(Object other) {
            if (other instanceof ObserverEntry) {
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
        return SessionManager.getSession();
    }

    public ObjectType getObjectType() {
        return m_oid.getObjectType();
    }

    public OID getOID() {
        return m_oid;
    }

    public Object get(String property) {
        Property prop = getObjectType().getProperty(property);
        if (prop.isCollection()) {
            return new DataAssociationImpl(getSession(), this, prop);
        }

        if (prop.isKeyProperty()) {
            return m_oid.get(property);
        } else if (m_oid.isInitialized()) {
            return m_ssn.get(this, convert(property));
        } else {
            return null;
        }
    }

    public void set(String property, Object value) {
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
        // handle calls to isNew before key is set
        return !m_oid.isInitialized() ||
            (m_ssn.isNew(this) && !m_ssn.isPersisted(this));
    }

    public boolean isDeleted() {
        return m_ssn.isDeleted(this);
    }

    public boolean isDisconnected() {
        // throw new Error("not implemented");
        return m_isDisconnected;
    }

    public void disconnect() {
        // throw new Error("not implemented");
        m_isDisconnected = true;
    }

    public boolean isModified() {
        return !m_ssn.isFlushed(this);
    }

    public boolean isPropertyModified(String name) {
        return !m_ssn.isFlushed(this, convert(name));
    }

    public boolean isValid() {
        throw new Error("not implemented");
    }

    public void delete() {
        try {
            m_ssn.delete(this);
        } catch (ProtoException pe) {
            throw new PersistenceException(pe);
        }
    }

    public void specialize(String subtypeName) {
        ObjectType subtype =
            MetadataRoot.getMetadataRoot().getObjectType(subtypeName);

        if (subtype == null) {
            throw new PersistenceException("No such type: " + subtypeName);
        }

        specialize(subtype);
    }

    public void specialize(ObjectType subtype) {
        m_oid.specialize(subtype);
    }

    public void save() {
        try {
            m_ssn.flush();
            m_ssn.assertFlushed(this);
        } catch (ProtoException pe) {
            throw new PersistenceException(pe);
        }
    }

    public void addObserver(DataObserver observer) {
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

    void fireObserver(DataEvent event) {
        ObserverEntry old = m_firing;
        try {
            for (Iterator it = m_observers.iterator(); it.hasNext(); ) {
                ObserverEntry entry = (ObserverEntry) it.next();
                final DataObserver observer = entry.getObserver();
                if (entry.isFiring(event)) {
//                     if (event instanceof BeforeSaveEvent
//                         || event instanceof BeforeDeleteEvent
//                         || event instanceof AfterDeleteEvent) {
                    if (false) {
                        throw new PersistenceException
                            ("Loop detected while firing a DataObserver. " +
                             "This probably resulted from calling the save " +
                             "method of a data object from within a " +
                             "beforeSave observer registered on that " +
                             "same data object, or the analogous situation " +
                             "with delete and beforeDelete/afterDelete. " +
                             entry  + " " + event);
                    }
                } else {
                    try {
                        entry.setFiring(event);
                        m_firing = entry;
                        s_log.debug(event);
                        event.invoke(observer);
                    } finally {
                        entry.clearFiring(event);
                    }
                }
            }
        } finally {
            m_firing = old;
        }
    }

    public DataHandler setDataHandler(DataHandler handler) {
        throw new Error("not implemented");
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
