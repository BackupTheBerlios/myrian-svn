package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;

/**
 * Mutation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/04/30 $
 **/

abstract class Mutation extends DML {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Mutation.java#5 $ by $Author: rhs $, $DateTime: 2003/04/30 10:11:14 $";

    private Condition m_condition;

    public Mutation(Table table, Condition condition) {
        super(table);
        m_condition = condition;
    }

    public void setCondition(Condition condition) {
        m_condition = condition;
    }

    public Condition getCondition() {
        return m_condition;
    }

}
