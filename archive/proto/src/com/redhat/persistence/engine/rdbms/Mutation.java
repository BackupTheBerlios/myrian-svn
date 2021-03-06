package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;

/**
 * Mutation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

abstract class Mutation extends DML {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/engine/rdbms/Mutation.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

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
