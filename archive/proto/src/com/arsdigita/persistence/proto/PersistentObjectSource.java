package com.arsdigita.persistence.proto;

/**
 * PersistentObjectSource
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2003/01/02 $
 **/

class PersistentObjectSource {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PersistentObjectSource.java#4 $ by $Author: rhs $, $DateTime: 2003/01/02 15:38:03 $";

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

                public String toString() {
                    return oid.toString();
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
