package com.arsdigita.persistence;

import com.arsdigita.persistence.proto.common.Path;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.proto.PersistentCollection;
import com.arsdigita.persistence.proto.Signature;

/**
 * DataAssociationCursorImpl
 *
 * @author Archit Shah &lt;ashah@mit.edu&gt;
 * @version $Revision: #10 $ $Date: 2003/05/15 $
 **/

class DataAssociationCursorImpl extends DataCollectionImpl
    implements DataAssociationCursor {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataAssociationCursorImpl.java#10 $ by $Author: justin $, $DateTime: 2003/05/15 18:33:33 $";

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
        Signature sig = getOriginal().getSignature();
        if (sig.isSource(Path.get("link"))) {
            return (DataObject) get("link");
        } else {
            return null;
        }
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
