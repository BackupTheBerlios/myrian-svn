package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.proto.PersistentCollection;

/**
 * DataAssociationCursorImpl
 *
 * @author Archit Shah &lt;ashah@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2003/05/12 $
 **/

class DataAssociationCursorImpl extends DataCollectionImpl
    implements DataAssociationCursor {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataAssociationCursorImpl.java#8 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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


    /**
     *  This returns the Link Property specified by the passed in parameter.
     *  For instance, if there is a sortKey specifying how to sort
     *  the association, calling getLinkProperty("sortKey") would return
     *  the sortKey for the given Association.
     *
     *  @param name The name of the Link Property to return
     *  @return The Link Property specified by the parameter
     */
    public Object getLinkProperty(String name) {
        return get("link." + name);
    }

    public void remove() {
        m_assn.remove(getDataObject());
    }
}
