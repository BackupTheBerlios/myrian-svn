package com.redhat.persistence;

/**
 * PersistentObjectSource
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class PersistentObjectSource {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/PersistentObjectSource.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

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
