package com.arsdigita.persistence.proto;

/**
 * PersistentCollection
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/12/02 $
 **/

public interface PersistentCollection {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PersistentCollection.java#1 $ by $Author: rhs $, $DateTime: 2002/12/02 12:04:21 $";

    Session getSession();

    DataSet getDataSet();

}
