package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;

/**
 * Delete
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/02/13 $
 **/

class Delete extends Mutation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Delete.java#5 $ by $Author: rhs $, $DateTime: 2003/02/13 18:36:15 $";

    public Delete(Table table, Condition condition) {
        super(table, condition);
    }

    public String toString() {
        return "delete from " + getTable() + " where " + getCondition() +
            "\n\nparams = " + getParams();
    }

}
