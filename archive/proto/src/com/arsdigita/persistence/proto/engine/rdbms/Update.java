package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * Update
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/02/14 $
 **/

class Update extends Mutation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Update.java#6 $ by $Author: rhs $, $DateTime: 2003/02/14 16:46:06 $";

    public Update(Table table, Condition condition) {
        super(table, condition);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
