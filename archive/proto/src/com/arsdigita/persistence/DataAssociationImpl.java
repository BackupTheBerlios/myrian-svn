package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.Property;

/**
 * DataAssociationImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/01/11 $
 **/

class DataAssociationImpl extends DataAssociationCursorImpl
    implements DataAssociation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataAssociationImpl.java#5 $ by $Author: rhs $, $DateTime: 2003/01/11 09:31:47 $";

    private com.arsdigita.persistence.proto.Session m_pssn;
    private DataObject m_data;
    private Property m_prop;
    private Session m_ssn;
    private com.arsdigita.persistence.proto.metadata.Property m_pprop;

    DataAssociationImpl(Session ssn, DataObject data, Property prop) {
        super(ssn, data, prop);
        setAssociation(this);
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

    public DataCollection getDataCollection() { return cursor(); }

    public DataAssociationCursor getDataAssociationCursor() {
        DataAssociationCursorImpl dac = new DataAssociationCursorImpl(
            getSession(), m_data, m_prop);
        dac.setAssociation(this);
        return dac;
    }

    public DataAssociationCursor cursor() {
        return getDataAssociationCursor();
    }

    public void remove(DataObject obj) {
        remove(obj.getOID());
    }

    public void remove(OID oid) {
        m_pssn.remove(m_data.getOID().getProtoOID(), m_pprop,
                      m_pssn.retrieve(oid.getProtoOID()));
    }

    public boolean isModified() {
        return m_pssn.isModified(m_data.getOID().getProtoOID(), m_pprop);
    }
}
