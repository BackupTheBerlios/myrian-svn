package com.arsdigita.persistence.proto;

/**
 * PersistentCollection
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public interface PersistentCollection {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/PersistentCollection.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    Session getSession();

    DataSet getDataSet();

}
