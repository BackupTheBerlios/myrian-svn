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
 * @version $Revision: #3 $ $Date: 2003/02/05 $
 **/

class Update extends Mutation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Update.java#3 $ by $Author: rhs $, $DateTime: 2003/02/05 21:09:04 $";

    public Update(Table table, Condition condition) {
        super(table, condition);
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

        result.append("\nwhere ");
        result.append(getCondition());

        return result.toString();
    }

}
