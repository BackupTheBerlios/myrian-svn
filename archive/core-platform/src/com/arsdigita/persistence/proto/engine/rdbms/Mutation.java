package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;

/**
 * Mutation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

abstract class Mutation extends DML {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/engine/rdbms/Mutation.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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
