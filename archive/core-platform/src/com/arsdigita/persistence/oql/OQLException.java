package com.arsdigita.persistence.oql;

/**
 * OQLException
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/07/19 $
 **/

public class OQLException extends RuntimeException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/OQLException.java#3 $ by $Author: rhs $, $DateTime: 2002/07/19 16:18:07 $";

    OQLException(String msg) {
        super(msg);
    }

}
