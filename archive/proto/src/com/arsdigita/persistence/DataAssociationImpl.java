package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.Property;

/**
 * DataAssociationImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/01/10 $
 **/

class DataAssociationImpl extends DataCollectionImpl
    implements DataAssociation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataAssociationImpl.java#3 $ by $Author: rhs $, $DateTime: 2003/01/10 10:27:20 $";

    private com.arsdigita.persistence.proto.Session m_pssn;
    private DataObject m_data;
    private Property m_prop;
    private com.arsdigita.persistence.proto.metadata.Property m_pprop;

    DataAssociationImpl(Session ssn, DataObject data, Property prop) {
        super(ssn, data.getObjectType());
        m_pssn = ssn.getProtoSession();
        m_data = data;
        m_prop = prop;
        m_pprop = C.prop(prop);
    }

    public DataObject add(DataObject obj) {
        return (DataObject) DataObjectImpl.wrap
            (getSession(), m_pssn.add(m_data.getOID().getProtoOID(), m_pprop,
                                      DataObjectImpl.unwrap(obj)));
    }

    public void clear() {
        m_pssn.clear(m_data.getOID().getProtoOID(), m_pprop);
    }

    public DataCollection getDataCollection() {
        return new DataCollectionImpl(getSession(), getObjectType());
    }

    public DataAssociationCursor getDataAssociationCursor() {
        throw new Error("not implemented");
    }

    public DataAssociationCursor cursor() {
        return getDataAssociationCursor();
    }

    public Object getLinkProperty(String prop) {
        throw new Error("not implemented");
    }

    public void remove(DataObject obj) {
        remove(obj.getOID());
    }

    public void remove(OID oid) {
        m_pssn.remove(m_data.getOID().getProtoOID(), m_pprop,
                      oid.getProtoOID());
    }

    public void remove() {
        remove(getDataObject());
    }

    public boolean isModified() {
        return m_pssn.isModified(m_data.getOID().getProtoOID(), m_pprop);
    }

}
