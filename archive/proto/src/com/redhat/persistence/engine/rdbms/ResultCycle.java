package com.redhat.persistence.engine.rdbms;

import java.sql.*;

import org.apache.log4j.Logger;

/**
 * ResultCycle
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

class ResultCycle {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/engine/rdbms/ResultCycle.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private static final Logger LOG = Logger.getLogger(ResultCycle.class);

    final private RDBMSEngine m_engine;
    private ResultSet m_rs;
    final private StatementLifecycle m_cycle;

    ResultCycle(RDBMSEngine engine, ResultSet rs, StatementLifecycle cycle) {
        if (rs == null) {
            throw new IllegalArgumentException("null result set");
        }

        m_engine = engine;
        m_rs = rs;
        m_cycle = cycle;
    }

    public ResultSet getResultSet() {
        return m_rs;
    }

    public StatementLifecycle getLifecycle() {
        return m_cycle;
    }

    public boolean next() {
	if (m_rs == null) {
	    throw new IllegalStateException("result set closed");
	}
        try {
            if (m_cycle != null) { m_cycle.beginNext(); }
            boolean result = m_rs.next();
            if (m_cycle != null) { m_cycle.endNext(result); }
            if (!result) { close(); }
            return result;
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

    public void close() {
        if (m_rs == null) { return; }
        try {
	    if (LOG.isDebugEnabled()) {
		LOG.debug("Closing Statement because resultset was closed.");
	    }
            if (m_cycle != null) { m_cycle.beginClose(); }
	    m_rs.getStatement().close();
            m_rs.close();
            if (m_cycle != null) { m_cycle.endClose(); }
            m_rs = null;
	    m_engine.release();
        } catch (SQLException e) {
            throw new Error(e.getMessage());
        }
    }

}
