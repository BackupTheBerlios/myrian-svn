package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.Property;

/**
 * DataAssociationCursorImpl
 *
 * @author Archit Shah &lt;ashah@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/01/10 $
 **/

class DataAssociationCursorImpl extends DataCollectionImpl
    implements DataAssociationCursor {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataAssociationCursorImpl.java#3 $ by $Author: ashah $, $DateTime: 2003/01/10 18:48:23 $";

    private DataAssociationImpl m_assn;

    DataAssociationCursorImpl(Session ssn, DataObject data, Property prop) {
        super(ssn, data.getObjectType());
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
