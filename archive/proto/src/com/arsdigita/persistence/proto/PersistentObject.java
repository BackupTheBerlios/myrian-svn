package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;

/**
 * PersistentObject
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/12/02 $
 **/

public interface PersistentObject {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PersistentObject.java#1 $ by $Author: rhs $, $DateTime: 2002/12/02 12:04:21 $";

    Session getSession();

    OID getOID();

}
