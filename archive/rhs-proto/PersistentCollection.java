package com.arsdigita.persistence.proto;

/**
 * PersistentCollection
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

public interface PersistentCollection {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/PersistentCollection.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

    Session getSession();

    DataSet getDataSet();

}
