package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * Update
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/01/17 $
 **/

class Update extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Update.java#1 $ by $Author: rhs $, $DateTime: 2003/01/17 11:07:02 $";

    public Update(Table table, OID oid) {
        super(table, oid);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("update " + getTable() + "\nset ");
        for (Iterator it = getColumns().iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            result.append(col.getName() + " = " + get(col));
            if (it.hasNext()) {
                result.append("\n    ");
            }
        }

        return result + "\nwhere oid = " + getOID();
    }

}
