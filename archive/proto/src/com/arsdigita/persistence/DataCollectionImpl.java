package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.ObjectType;

/**
 * DataCollectionImpl
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/01/09 $
 **/

class DataCollectionImpl extends DataQueryImpl implements DataCollection {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataCollectionImpl.java#3 $ by $Author: rhs $, $DateTime: 2003/01/09 18:21:44 $";

    DataCollectionImpl(Session ssn, ObjectType type) {
        super(ssn, C.type(type));
    }

    public ObjectType getObjectType() {
        throw new Error("not implemented");
    }

    public DataObject getDataObject() {
        throw new Error("not implemented");
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
