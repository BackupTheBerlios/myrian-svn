package com.redhat.persistence;

/**
 * PersistentCollection
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public interface PersistentCollection {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/PersistentCollection.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    Session getSession();

    DataSet getDataSet();

}
