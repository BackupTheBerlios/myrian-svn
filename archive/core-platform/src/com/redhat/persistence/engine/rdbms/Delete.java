package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;

/**
 * Delete
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

class Delete extends Mutation {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/Delete.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    public Delete(Table table, Condition condition) {
        super(table, condition);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}