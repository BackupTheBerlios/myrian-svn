package com.arsdigita.persistence.proto;

/**
 * PersistentObjectSource
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class PersistentObjectSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/PersistentObjectSource.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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
