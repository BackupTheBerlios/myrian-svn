package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;

/**
 * PersistentObjectSource
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

class PersistentObjectSource {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/PersistentObjectSource.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

    public PersistentObject getPersistentObject(final Session ssn,
                                                final OID oid) {
        return new PersistentObject() {
                public Session getSession() {
                    return ssn;
                }

                public OID getOID() {
                    return oid;
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
