package com.arsdigita.persistence.proto;

/**
 * PersistentObject
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/01/02 $
 **/

public interface PersistentObject {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PersistentObject.java#2 $ by $Author: rhs $, $DateTime: 2003/01/02 15:38:03 $";

    Session getSession();

    OID getOID();

}
