package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;

/**
 * Delete
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/02/14 $
 **/

class Delete extends Mutation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Delete.java#6 $ by $Author: rhs $, $DateTime: 2003/02/14 16:46:06 $";

    public Delete(Table table, Condition condition) {
        super(table, condition);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
