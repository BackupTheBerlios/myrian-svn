package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.proto.AddEvent;
import com.arsdigita.persistence.proto.CreateEvent;
import com.arsdigita.persistence.proto.DeleteEvent;
import com.arsdigita.persistence.proto.Event;
import com.arsdigita.persistence.proto.ProtoException;
import com.arsdigita.persistence.proto.RemoveEvent;
import com.arsdigita.persistence.proto.Session;
import com.arsdigita.persistence.proto.SetEvent;
import java.util.*;

/**
 * DataObjectImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #11 $ $Date: 2003/03/14 $
 **/

class DataObjectImpl implements DataObject {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataObjectImpl.java#11 $ by $Author: ashah $, $DateTime: 2003/03/14 15:57:20 $";

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

        public boolean isFiring(Event event) {
            return m_firing.contains(event.getClass());
        }

        public void setFiring(Event event) {
            m_firing.add(event.getClass());
        }

        public void clearFiring(Event event) {
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
        try {
            Property prop = getObjectType().getProperty(property);
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
        } catch (ProtoException pe) {
            throw new PersistenceException(pe);
        }

        if (!m_ssn.isFlushed(this)) {
            throw new PersistenceException("all events not flushed");
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

    void fireObserver(Event event, final boolean before,
                      boolean swallowReentrence) {
        ObserverEntry old = m_firing;
        try {
            for (Iterator it = m_observers.iterator(); it.hasNext(); ) {
                ObserverEntry entry = (ObserverEntry) it.next();
                final DataObserver observer = entry.getObserver();
                if (entry.isFiring(event)) {
                    if (!swallowReentrence) {
                        throw new PersistenceException
                            ("Loop detected while firing a DataObserver. " +
                             "This probably resulted from calling the save " +
                             "method of a data object from within a " +
                             "beforeSave observer registered on that " +
                             "same data object, or the analogous situation " +
                             "with delete and beforeDelete/afterDelete.");
                    }
                } else {
                    try {
                        entry.setFiring(event);
                        m_firing = entry;
                        event.dispatch(new Event.Switch() {
                                public void onCreate(CreateEvent e) {
                                    if (before) {
                                        observer.beforeSave
                                            (DataObjectImpl.this);
                                    } else {
                                        observer.afterSave
                                            (DataObjectImpl.this);
                                    }
                                }

                                public void onDelete(DeleteEvent e) {
                                    if (before) {
                                        observer.beforeDelete
                                            (DataObjectImpl.this);
                                    } else {
                                        observer.afterDelete
                                            (DataObjectImpl.this);
                                    }
                                }

                                public void onSet(SetEvent e) {
                                    String prop = e.getProperty().getName();
                                    observer.set
                                        (DataObjectImpl.this, prop, get(prop),
                                         e.getArgument());
                                }

                                public void onAdd(AddEvent e) {
                                    observer.add
                                        (DataObjectImpl.this,
                                         e.getProperty().getName(),
                                         (DataObject) e.getArgument());
                                }

                                public void onRemove(RemoveEvent e) {
                                    observer.remove
                                        (DataObjectImpl.this,
                                         e.getProperty().getName(),
                                         (DataObject) e.getArgument());
                                }
                            });
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
