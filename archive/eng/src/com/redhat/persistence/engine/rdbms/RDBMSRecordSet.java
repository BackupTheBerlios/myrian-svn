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

import com.redhat.persistence.RecordSet;
import com.redhat.persistence.Signature;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * RDBMSRecordSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/30 $
 **/

class RDBMSRecordSet extends RecordSet {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/engine/rdbms/RDBMSRecordSet.java#6 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private static final Logger s_log = Logger.getLogger(RecordSet.class);

    final private ObjectMap m_map;
    final private RDBMSEngine m_engine;
    final private ResultCycle m_rc;

    RDBMSRecordSet(Signature sig, ObjectMap map, RDBMSEngine engine,
                   ResultCycle rc) {
        super(sig);
	if (rc == null) {
	    throw new IllegalArgumentException("null result set");
	}
        m_map = map;
        m_engine = engine;
        m_rc = rc;
    }

    ResultSet getResultSet() {
        return m_rc.getResultSet();
    }

    String getColumn(Path p) {
        return getSignature().getColumn(p);
    }

    public ObjectMap getObjectMap() {
        return m_map;
    }

    public boolean next() {
        return m_rc.next();
    }

    public Object get(Path p) {
        StatementLifecycle cycle = m_rc.getLifecycle();
        try {
            ObjectMap map = p == null ? m_map : m_map.getMapping(p).getMap();
            ObjectType type = map.getObjectType();
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
