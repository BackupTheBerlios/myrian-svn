package com.redhat.persistence;

/**
 * PersistentCollection
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public interface PersistentCollection {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/PersistentCollection.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    Session getSession();

    DataSet getDataSet();

}
