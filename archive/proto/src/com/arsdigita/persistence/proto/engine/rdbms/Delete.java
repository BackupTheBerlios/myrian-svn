package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;

/**
 * Delete
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/01/17 $
 **/

class Delete extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Delete.java#1 $ by $Author: rhs $, $DateTime: 2003/01/17 11:07:02 $";

    public Delete(Table table, OID oid) {
        super(table, oid);
    }

    public String toString() {
        return "delete from " + getTable() + " where oid = " + getOID();
    }

}
