package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import java.util.*;

/**
 * DataObjectImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/02/12 $
 **/

class DataObjectImpl implements DataObject {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataObjectImpl.java#4 $ by $Author: rhs $, $DateTime: 2003/02/12 14:21:42 $";

    private Session m_ssn;
    private OID m_oid = null;
    private HashMap m_temp = null;
    private ObjectType m_type = null;

    DataObjectImpl(Session ssn, ObjectType type) {
        m_ssn = ssn;
        m_temp = new HashMap();
        m_type = type;
    }

    DataObjectImpl(Session ssn, OID oid) {
        m_ssn = ssn;
        m_oid = oid;
    }

    private com.arsdigita.persistence.proto.metadata.Property convert(String property) {
        return C.prop(getObjectType().getProperty(property));
    }

    public Session getSession() {
        return m_ssn;
    }

    public ObjectType getObjectType() {
        if (m_type != null) {
            return m_type;
        } else {
            return C.fromType(m_ssn.getProtoSession().getObjectType(this));
        }
    }

    public OID getOID() {
        return m_oid;
    }

    public Object get(String property) {
        Property prop = getObjectType().getProperty(property);
        if (prop.isCollection()) {
            return new DataAssociationImpl(m_ssn, this, prop);
        }

        if (m_temp == null) {
            return m_ssn.getProtoSession().get(this, convert(property));
        } else {
            return m_temp.get(property);
        }
    }

    public void set(String property, Object value) {
        if (m_temp != null) {
            m_temp.put(property, value);

            OID oid = new OID(m_type);
            for (Iterator it = m_type.getKeyProperties(); it.hasNext(); ) {
                Property prop = (Property) it.next();
                Object val = m_temp.get(prop.getName());
                if (val == null) {
                    return;
                } else {
                    oid.set(prop.getName(), val);
                }
            }
            m_ssn.getProtoSession().create(this);
            m_temp = null;
            m_type = null;
        } else {
            m_ssn.getProtoSession().set(this, convert(property), value);
        }
    }

    public boolean isNew() {
        if (m_temp == null) {
            return m_ssn.getProtoSession().isNew(this);
        } else {
            return true;
        }
    }

    public boolean isDeleted() {
        if (m_temp == null) {
            return m_ssn.getProtoSession().isDeleted(this);
        } else {
            return false;
        }
    }

    public boolean isDisconnected() {
        throw new Error("not implemented");
    }

    public void disconnect() {
        throw new Error("not implemented");
    }

    public boolean isModified() {
        if (m_temp == null) {
            return m_ssn.getProtoSession().isModified(this);
        } else {
            return true;
        }
    }

    public boolean isPropertyModified(String name) {
        if (m_temp == null) {
            return m_ssn.getProtoSession().isModified(this, convert(name));
        } else {
            return true;
        }
    }

    public boolean isValid() {
        throw new Error("not implemented");
    }

    public void delete() {
        if (m_temp == null) {
            m_ssn.getProtoSession().delete(this);
        } else {
            throw new Error("deleting a new object");
        }
    }

    public void specialize(String subtypeName) {
        throw new Error("not implemented");
    }

    public void specialize(ObjectType subtype) {
        throw new Error("not implemented");
    }

    public void save() {
        if (m_temp == null) {
            m_ssn.getProtoSession().flush();
        } else {
            throw new Error("cannot save without key properties");
        }
    }

    public void addObserver(DataObserver observer) {
        throw new Error("not implemented");
    }

    public DataHandler setDataHandler(DataHandler handler) {
        throw new Error("not implemented");
    }

}
