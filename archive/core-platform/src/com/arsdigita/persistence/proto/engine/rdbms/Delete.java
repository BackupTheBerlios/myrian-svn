package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;

/**
 * Delete
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

class Delete extends Mutation {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/engine/rdbms/Delete.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    public Delete(Table table, Condition condition) {
        super(table, condition);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
