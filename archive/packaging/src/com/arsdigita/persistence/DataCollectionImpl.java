package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.ObjectType;
import com.redhat.persistence.PersistentCollection;

/**
 * DataCollectionImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

class DataCollectionImpl extends DataQueryImpl implements DataCollection {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/DataCollectionImpl.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

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
