package com.arsdigita.persistence.oql;

/**
 * NoMetadataException
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/07/19 $
 **/

public class NoMetadataException extends OQLException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/NoMetadataException.java#1 $ by $Author: rhs $, $DateTime: 2002/07/19 16:58:45 $";

    NoMetadataException(String msg) {
        super(msg);
    }

}
