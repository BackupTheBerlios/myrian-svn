package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.proto.Session;
import java.util.*;

/**
 * DataObjectImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2003/02/26 $
 **/

class DataObjectImpl implements DataObject {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataObjectImpl.java#7 $ by $Author: rhs $, $DateTime: 2003/02/26 12:01:31 $";

    private Session m_ssn;
    private OID m_oid;

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
        } else {
            return m_ssn.get(this, convert(property));
        }
    }

    public void set(String property, Object value) {
        Property prop = getObjectType().getProperty(property);
        if (prop.isKeyProperty()) {
            m_oid.set(property, value);
            if (m_oid.isInitialized()) {
                m_ssn.create(this);
            }
        } else {
            m_ssn.set(this, convert(property), value);
        }
    }

    public boolean isNew() {
        // handle calls to isNew before key is set
        return !m_oid.isInitialized() || m_ssn.isNew(this);
    }

    public boolean isDeleted() {
        return m_ssn.isDeleted(this);
    }

    public boolean isDisconnected() {
        throw new Error("not implemented");
    }

    public void disconnect() {
        throw new Error("not implemented");
    }

    public boolean isModified() {
        return m_ssn.isModified(this);
    }

    public boolean isPropertyModified(String name) {
        return m_ssn.isModified(this, convert(name));
    }

    public boolean isValid() {
        throw new Error("not implemented");
    }

    public void delete() {
        m_ssn.delete(this);
    }

    public void specialize(String subtypeName) {
        throw new Error("not implemented");
    }

    public void specialize(ObjectType subtype) {
        throw new Error("not implemented");
    }

    public void save() {
        m_ssn.flush();
    }

    public void addObserver(DataObserver observer) {
        throw new Error("not implemented");
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
