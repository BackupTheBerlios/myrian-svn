/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.Condition;
import com.redhat.persistence.metadata.Table;

/**
 * Mutation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/10/28 $
 **/

abstract class Mutation extends DML {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/Mutation.java#4 $ by $Author: jorris $, $DateTime: 2003/10/28 18:36:21 $";

    private Condition m_condition;

    public Mutation(RDBMSEngine engine, Table table, Condition condition) {
        super(engine, table);
        m_condition = condition;
    }

    public void setCondition(Condition condition) {
        m_condition = condition;
    }

    public Condition getCondition() {
        return m_condition;
    }

}
