/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.redhat.persistence.engine.rdbms;

import java.sql.*;

/**
 * StatementLifecycle
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/03/30 $
 **/

public interface StatementLifecycle {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/StatementLifecycle.java#5 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    void beginPrepare();
    void endPrepare();
    void endPrepare(SQLException e);

    void beginSet(int pos, int type, Object obj);
    void endSet();
    void endSet(SQLException e);

    void beginExecute();
    void endExecute(int updateCount);
    void endExecute(SQLException e);

    void beginNext();
    void endNext(boolean more);
    void endNext(SQLException e);

    void beginGet(String column);
    void endGet(Object result);
    void endGet(SQLException e);

    void beginClose();
    void endClose();
    void endClose(SQLException e);

}
