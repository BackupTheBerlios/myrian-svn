package com.arsdigita.persistence;

/**
 * The DataHandler can be used to override the behavior of the persistence
 * layer when it performs certain operations. Currently only delete is
 * supported since the primary use for this class is to override hard deletes
 * and turn them into soft deletes under certain circumstances.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

public abstract class DataHandler {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataHandler.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";


    /**
     * This method is called in order to delete a data object. By default this
     * executes the SQL defined in the object type definition for the given
     * data object. In the common case this does a hard delete.
     **/

    public void doDelete(DataObject data) {
        ((GenericDataObject) data).doDelete();
    }

}
