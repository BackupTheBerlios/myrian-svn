package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.persistence.proto.metadata.Table;
import com.arsdigita.persistence.proto.metadata.Column;

/**
 * Delete
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/28 $
 **/

class Delete extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Delete.java#2 $ by $Author: rhs $, $DateTime: 2003/01/28 19:17:39 $";

    public Delete(Table table, OID oid) {
        super(table, oid);
    }

    public String toString() {
        return "delete from " + getTable() + " where oid = " + getOID();
    }

}
