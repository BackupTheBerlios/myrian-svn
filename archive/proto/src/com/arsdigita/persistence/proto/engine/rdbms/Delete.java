package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;

/**
 * Delete
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/02/07 $
 **/

class Delete extends Mutation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Delete.java#4 $ by $Author: rhs $, $DateTime: 2003/02/07 12:50:17 $";

    public Delete(Table table, Condition condition) {
        super(table, condition);
    }

    public String toString() {
        return "delete from " + getTable() + " where " + getCondition();
    }

}
