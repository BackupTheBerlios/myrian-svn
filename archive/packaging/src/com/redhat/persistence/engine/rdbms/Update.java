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

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;

import java.util.*;

/**
 * Update
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/08/29 $
 **/

class Update extends Mutation {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/engine/rdbms/Update.java#3 $ by $Author: rhs $, $DateTime: 2003/08/29 10:31:35 $";

    public Update(RDBMSEngine engine, Table table, Condition condition) {
        super(engine, table, condition);
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
