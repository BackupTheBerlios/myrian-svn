package com.redhat.persistence;

/**
 * PersistentCollection
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public interface PersistentCollection {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/PersistentCollection.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    Session getSession();

    DataSet getDataSet();

}
