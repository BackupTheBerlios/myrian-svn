/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence.engine.rdbms;

import org.myrian.persistence.RecordSet;
import org.myrian.persistence.Signature;
import org.myrian.persistence.common.Path;
import org.myrian.persistence.metadata.Adapter;
import org.myrian.persistence.metadata.ObjectMap;
import org.myrian.persistence.metadata.ObjectType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * RDBMSRecordSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

class RDBMSRecordSet extends RecordSet {


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
