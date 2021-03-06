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

import com.arsdigita.db.ConnectionManager;
import com.arsdigita.db.SQLExceptionHandler;
import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.*;

import java.util.*;
import java.sql.*;

import org.apache.log4j.Logger;

/**
 * RDBMSRecordSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/08/27 $
 **/

class RDBMSRecordSet extends RecordSet {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/engine/rdbms/RDBMSRecordSet.java#3 $ by $Author: rhs $, $DateTime: 2003/08/27 19:33:58 $";

    final private RDBMSEngine m_engine;
    final private ResultCycle m_rc;
    final private Map m_mappings;

    RDBMSRecordSet(Signature sig, RDBMSEngine engine, ResultCycle rc,
                   Map mappings) {
        super(sig);
	if (rc == null) {
	    throw new IllegalArgumentException("null result set");
	}
        m_engine = engine;
        m_rc = rc;
        m_mappings = mappings;
    }

    ResultSet getResultSet() {
        return m_rc.getResultSet();
    }

    String getColumn(Path p) {
        return (String) m_mappings.get(p);
    }

    public boolean next() {
        return m_rc.next();
    }

    public Object get(Path p) {
        try {
            ObjectType type = getSignature().getProperty(p).getType();
            Adapter ad = type.getRoot().getAdapter(type);

            StatementLifecycle cycle = m_rc.getLifecycle();
            String column = getColumn(p);
            if (cycle != null) { cycle.beginGet(column); }
            Object result = ad.fetch(m_rc.getResultSet(), column);
            if (cycle != null) { cycle.endGet(result); }
            return result;
        } catch (SQLException e) {
            //robust connection pooling
            m_engine.checkBadConnection(e);
            throw new Error
                ("error fetching path (" + p + "): " + e.getMessage());
        }
    }

    public void close() {
        m_rc.close();
    }

}
