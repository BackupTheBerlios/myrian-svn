/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence.engine.rdbms;

import java.sql.*;

/**
 * StatementLifecycle
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public interface StatementLifecycle {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/engine/rdbms/StatementLifecycle.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
