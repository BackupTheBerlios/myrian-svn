package com.arsdigita.persistence;

/**
 * ObjectDeletedException
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/06/14 $
 **/

public class ObjectDeletedException extends PersistenceException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/ObjectDeletedException.java#1 $ by $Author: rhs $, $DateTime: 2002/06/14 12:22:31 $";

    public ObjectDeletedException(String message) {
        super(message);
    }

}
