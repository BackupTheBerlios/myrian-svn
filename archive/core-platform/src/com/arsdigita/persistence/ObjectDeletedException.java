package com.arsdigita.persistence;

/**
 * ObjectDeletedException
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/07/18 $
 **/

public class ObjectDeletedException extends PersistenceException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/ObjectDeletedException.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    public ObjectDeletedException(String message) {
        super(message);
    }

}
