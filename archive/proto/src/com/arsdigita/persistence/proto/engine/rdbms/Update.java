package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.persistence.proto.metadata.Table;
import com.arsdigita.persistence.proto.metadata.Column;

import java.util.*;

/**
 * Update
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/28 $
 **/

class Update extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Update.java#2 $ by $Author: rhs $, $DateTime: 2003/01/28 19:17:39 $";

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
