package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.proto.PersistentObject;
import java.util.*;

/**
 * DataObjectImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/10 $
 **/

class DataObjectImpl implements DataObject {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataObjectImpl.java#2 $ by $Author: rhs $, $DateTime: 2003/01/10 09:31:34 $";

    static final Object wrap(Session ssn, Object obj) {
        if (obj instanceof PersistentObject) {
            return new DataObjectImpl(ssn, (PersistentObject) obj);
        } else {
            return obj;
        }
    }

    static final Object unwrap(Object obj) {
        if (obj instanceof DataObjectImpl) {
            return ((DataObjectImpl) obj).m_po;
        } else {
            return obj;
        }
    }

    private Session m_ssn;
    private PersistentObject m_po = null;
    private HashMap m_temp = null;
    private ObjectType m_type = null;

    DataObjectImpl(Session ssn, ObjectType type) {
        m_ssn = ssn;
        m_temp = new HashMap();
        m_type = type;
    }

    DataObjectImpl(Session ssn, PersistentObject po) {
        m_ssn = ssn;
        m_po = po;
    }

    public PersistentObject getPersistentObject() {
        return m_po;
    }

    private com.arsdigita.persistence.proto.metadata.Property convert(String property) {
        return C.prop(getObjectType().getProperty(property));
    }

    public Session getSession() {
        return m_ssn;
    }

    public ObjectType getObjectType() {
        if (m_po == null) {
            return m_type;
        } else {
            return C.fromType(m_po.getOID().getObjectType());
        }
    }

    public OID getOID() {
        if (m_po == null) {
            return new OID(m_type);
        } else {
            return new OID(m_po.getOID());
        }
    }

    public Object get(String property) {
        if (m_po == null) {
            return m_temp.get(property);
        } else {
            return wrap(m_ssn, m_po.getSession().get(m_po.getOID(), convert(property)));
        }
    }

    public void set(String property, Object value) {
        if (m_po == null) {
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
            m_temp = null;
            m_type = null;
            m_po = m_ssn.getProtoSession().create(oid.getProtoOID());
        } else {
            m_po.getSession().set(m_po.getOID(), convert(property),
                                  unwrap(value));
        }
    }

    public boolean isNew() {
        if (m_po == null) {
            return true;
        } else {
            return m_po.getSession().isNew(m_po.getOID());
        }
    }

    public boolean isDeleted() {
        if (m_po == null) {
            return false;
        } else {
            return m_po.getSession().isDeleted(m_po.getOID());
        }
    }

    public boolean isDisconnected() {
        throw new Error("not implemented");
    }

    public void disconnect() {
        throw new Error("not implemented");
    }

    public boolean isModified() {
        if (m_po == null) {
            return true;
        } else {
            return m_po.getSession().isModified(m_po.getOID());
        }
    }

    public boolean isPropertyModified(String name) {
        if (m_po == null) {
            return true;
        } else {
            return m_po.getSession().isModified(m_po.getOID(), convert(name));
        }
    }

    public boolean isValid() {
        throw new Error("not implemented");
    }

    public void delete() {
        if (m_po == null) {
            throw new Error("deleting a new object");
        } else {
            m_po.getSession().delete(m_po.getOID());
        }
    }

    public void specialize(String subtypeName) {
        throw new Error("not implemented");
    }

    public void specialize(ObjectType subtype) {
        throw new Error("not implemented");
    }

    public void save() {
        if (m_po == null) {
            throw new Error("cannot save without key properties");
        } else {
            m_po.getSession().flush();
        }
    }

    public void addObserver(DataObserver observer) {
        throw new Error("not implemented");
    }

    public DataHandler setDataHandler(DataHandler handler) {
        throw new Error("not implemented");
    }

}
