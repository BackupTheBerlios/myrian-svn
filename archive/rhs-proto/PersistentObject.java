package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.OID;

/**
 * PersistentObject
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

public interface PersistentObject {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/PersistentObject.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

    Session getSession();

    OID getOID();

}
