/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.RecordSet;
import com.redhat.persistence.Signature;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.ObjectType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * RDBMSRecordSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/05/02 $
 **/

class RDBMSRecordSet extends RecordSet {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/engine/rdbms/RDBMSRecordSet.java#3 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    private static final Logger s_log = Logger.getLogger(RecordSet.class);

    final private RDBMSEngine m_engine;
    final private ResultCycle m_rc;

    RDBMSRecordSet(Signature sig, RDBMSEngine engine, ResultCycle rc) {
        super(sig);
	if (rc == null) {
	    throw new IllegalArgumentException("null result set");
	}
        m_engine = engine;
        m_rc = rc;
    }

    ResultSet getResultSet() {
        return m_rc.getResultSet();
    }

    String getColumn(Path p) {
        return getSignature().getColumn(p);
    }

    public boolean next() {
        return m_rc.next();
    }

    public Object get(Path p) {
        StatementLifecycle cycle = m_rc.getLifecycle();
        try {
            ObjectType type = getSignature().getProperty(p).getType();
            Adapter ad = type.getRoot().getAdapter(type);

            String column = getColumn(p);
            if (cycle != null) { cycle.beginGet(column); }
            Object result = ad.fetch(m_rc.getResultSet(), column);
            if (cycle != null) { cycle.endGet(result); }
            if (s_log.isDebugEnabled()) {
                s_log.debug(p + "(" + column + ") -> " + result);
            }
            return result;
        } catch (SQLException e) {
            if (cycle != null) { cycle.endGet(e); }
            throw new Error
                ("error fetching path (" + p + "): " + e.getMessage());
        }
    }

    public void close() {
        m_rc.close();
    }

}
