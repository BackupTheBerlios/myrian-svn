package com.redhat.persistence;

/**
 * PersistentObjectSource
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class PersistentObjectSource {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/PersistentObjectSource.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

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
