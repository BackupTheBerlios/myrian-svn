package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;

/**
 * PersistentObjectSource
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/04 $
 **/

class PersistentObjectSource {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PersistentObjectSource.java#2 $ by $Author: rhs $, $DateTime: 2002/12/04 19:18:22 $";

    public PersistentObject getPersistentObject(final Session ssn,
                                                final OID oid) {
        return new PersistentObject() {
                public Session getSession() {
                    return ssn;
                }

                public OID getOID() {
                    return oid;
                }

                public int hashCode() {
                    return oid.hashCode();
                }

                public boolean equals(Object o) {
                    if (o instanceof PersistentObject) {
                        return oid.equals(((PersistentObject) o).getOID());
                    } else if (o instanceof OID) {
                        return oid.equals(o);
                    } else {
                        return false;
                    }
                }
            };
    }

    public PersistentCollection getPersistentCollection(final Session ssn,
                                                        final DataSet set) {
        return new PersistentCollection() {
                public Session getSession() {
                    return ssn;
                }

                public DataSet getDataSet() {
                    return set;
                }
            };
    }

}
