package com.arsdigita.persistence.oql;

/**
 * OQLException
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/06/10 $
 **/

public class OQLException extends RuntimeException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/OQLException.java#1 $ by $Author: rhs $, $DateTime: 2002/06/10 15:35:38 $";

    public OQLException(String msg) {
        super(msg);
    }

}
