package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.persistence.proto.metadata.Table;
import com.arsdigita.persistence.proto.metadata.Column;

/**
 * Delete
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/02/05 $
 **/

class Delete extends Mutation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Delete.java#3 $ by $Author: rhs $, $DateTime: 2003/02/05 21:09:04 $";

    public Delete(Table table, Condition condition) {
        super(table, condition);
    }

    public String toString() {
        return "delete from " + getTable() + " where " + getCondition();
    }

}
