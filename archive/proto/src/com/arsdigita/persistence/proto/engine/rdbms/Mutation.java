package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.metadata.*;

/**
 * Mutation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/02/14 $
 **/

abstract class Mutation extends DML {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Mutation.java#3 $ by $Author: rhs $, $DateTime: 2003/02/14 16:46:06 $";

    private Condition m_condition;

    public Mutation(Table table, Condition condition) {
        super(table);
        m_condition = condition;
    }

    public Condition getCondition() {
        return m_condition;
    }

}
