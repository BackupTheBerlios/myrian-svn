package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.proto.PersistentObject;
import com.arsdigita.persistence.proto.PersistentCollection;

/**
 * DataCollectionImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/01/11 $
 **/

class DataCollectionImpl extends DataQueryImpl implements DataCollection {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataCollectionImpl.java#4 $ by $Author: rhs $, $DateTime: 2003/01/11 09:31:47 $";

    DataCollectionImpl(Session ssn, PersistentCollection pc) {
        super(ssn, pc);
    }

    public ObjectType getObjectType() {
        return C.fromType(m_pc.getDataSet().getQuery().getSignature()
                          .getObjectType());
    }

    public DataObject getDataObject() {
        return (DataObject) DataObjectImpl.wrap
            (getSession(), (PersistentObject) m_cursor.get());
    }

    public void setParameter(String p, Object o) {
        throw new Error("deprecated");
    }

    public Object getParameter(String p) {
        throw new Error("deprecated");
    }

    public boolean contains(OID oid) {
        throw new Error("not implemented");
    }

    public boolean contains(DataObject data) {
        throw new Error("not implemented");
    }

}
