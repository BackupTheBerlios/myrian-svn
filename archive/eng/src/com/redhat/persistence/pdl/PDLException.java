package com.redhat.persistence.pdl;

/**
 * PDLException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/09/14 $
 **/

public class PDLException extends RuntimeException {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/PDLException.java#1 $ by $Author: rhs $, $DateTime: 2004/09/14 17:22:30 $";

    public PDLException(String message) {
        super(message);
    }

}
