package org.myrian.persistence.pdl;

/**
 * PDLException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/10/01 $
 **/

public class PDLException extends RuntimeException {

    public final static String versionId = "$Id: //eng/persistence/dev/src/org/myrian/persistence/pdl/PDLException.java#1 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

    public PDLException(String message) {
        super(message);
    }

}
