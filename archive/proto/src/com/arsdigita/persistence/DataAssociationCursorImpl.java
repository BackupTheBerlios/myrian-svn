package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.proto.PersistentCollection;

/**
 * DataAssociationCursorImpl
 *
 * @author Archit Shah &lt;ashah@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/01/11 $
 **/

class DataAssociationCursorImpl extends DataCollectionImpl
    implements DataAssociationCursor {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataAssociationCursorImpl.java#4 $ by $Author: rhs $, $DateTime: 2003/01/11 09:31:47 $";

    private DataAssociationImpl m_assn;

    DataAssociationCursorImpl(Session ssn, DataObject data, Property prop) {
        super(ssn, (PersistentCollection) ssn.getProtoSession().get(data.getOID().getProtoOID(), C.prop(prop)));
    }

    protected final void setAssociation(DataAssociationImpl assn) {
        m_assn = assn;
    }

    public DataAssociation getDataAssociation() {
        return m_assn;
    }

    public DataObject getLink() {
        throw new Error("not implemented");
    }

    public Object getLinkProperty(String prop) {
        throw new Error("not implemented");
    }

    public void remove() {
        m_assn.remove(getDataObject());
    }
}
