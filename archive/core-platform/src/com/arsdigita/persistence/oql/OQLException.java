package com.arsdigita.persistence.oql;

/**
 * OQLException
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/07/18 $
 **/

public class OQLException extends RuntimeException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/OQLException.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    public OQLException(String msg) {
        super(msg);
    }

}
