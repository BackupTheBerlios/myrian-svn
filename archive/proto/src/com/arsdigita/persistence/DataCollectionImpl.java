package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.proto.PersistentCollection;

/**
 * DataCollectionImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2003/04/04 $
 **/

class DataCollectionImpl extends DataQueryImpl implements DataCollection {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataCollectionImpl.java#7 $ by $Author: rhs $, $DateTime: 2003/04/04 20:45:14 $";

    DataCollectionImpl(Session ssn, PersistentCollection pc) {
        super(ssn, pc);
    }

    public ObjectType getObjectType() {
        return C.fromType(getOriginal().getSignature().getObjectType());
    }

    public DataObject getDataObject() {
        return (DataObject) m_cursor.get();
    }

    /**
     * @deprecated
     **/

    public void setParameter(String p, Object o) {
        super.setParameter(p, o);
    }

    /**
     * @deprecated
     **/

    public Object getParameter(String p) {
        return super.getParameter(p);
    }

    public boolean contains(OID oid) {
        throw new Error("not implemented");
    }

    public boolean contains(DataObject data) {
        throw new Error("not implemented");
    }

}
