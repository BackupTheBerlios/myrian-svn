package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.proto.PersistentCollection;

/**
 * DataAssociationCursorImpl
 *
 * @author Archit Shah &lt;ashah@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/03/27 $
 **/

class DataAssociationCursorImpl extends DataCollectionImpl
    implements DataAssociationCursor {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataAssociationCursorImpl.java#6 $ by $Author: rhs $, $DateTime: 2003/03/27 15:13:02 $";

    private DataAssociationImpl m_assn;

    DataAssociationCursorImpl(Session ssn, DataObject data, Property prop) {
        super(ssn, (PersistentCollection) ssn.getProtoSession().get(data, C.prop(prop)));
    }

    protected final void setAssociation(DataAssociationImpl assn) {
        m_assn = assn;
    }

    public DataAssociation getDataAssociation() {
        return m_assn;
    }

    public DataObject getLink() {
        return (DataObject) get("link");
    }

    public Object getLinkProperty(String prop) {
        return get("link." + prop);
    }

    public void remove() {
        m_assn.remove(getDataObject());
    }
}
