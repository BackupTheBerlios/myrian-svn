package com.redhat.persistence;

/**
 * PersistentCollection
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public interface PersistentCollection {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/PersistentCollection.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    Session getSession();

    DataSet getDataSet();

}
