package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.Property;
import com.redhat.persistence.ProtoException;

/**
 * DataAssociationImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #12 $ $Date: 2003/08/04 $
 **/

class DataAssociationImpl extends DataAssociationCursorImpl
    implements DataAssociation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataAssociationImpl.java#12 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private com.redhat.persistence.Session m_pssn;
    private DataObject m_data;
    private Property m_prop;
    private com.redhat.persistence.metadata.Property m_pprop;

    DataAssociationImpl(Session ssn, DataObject data, Property prop) {
        super(ssn, data, prop);
        setAssociation(this);
        m_pssn = ssn.getProtoSession();
        m_data = data;
        m_prop = prop;
        m_pprop = C.prop(prop);
    }

    public DataObject add(DataObject obj) {
        try {
            return (DataObject) m_pssn.add(m_data, m_pprop, obj);
        } catch (ProtoException pe) {
            throw PersistenceException.newInstance(pe);
        }
    }

    public void clear() {
        m_pssn.clear(m_data, m_pprop);
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
        try {
            m_pssn.remove(m_data, m_pprop, obj);
        } catch (ProtoException pe) {
            throw PersistenceException.newInstance(pe);
        }
    }

    public void remove(OID oid) {
        remove(getSession().retrieve(oid));
    }

    public boolean isModified() {
        return !m_pssn.isFlushed(m_data, m_pprop);
    }
}
