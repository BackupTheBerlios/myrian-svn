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

import com.arsdigita.util.WrappedError;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * ResultCycle
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/05/02 $
 **/

class ResultCycle {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/engine/rdbms/ResultCycle.java#3 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    private static final Logger LOG = Logger.getLogger(ResultCycle.class);

    final private RDBMSEngine m_engine;
    private ResultSet m_rs;
    final private StatementLifecycle m_cycle;
    final private Throwable m_trace;

    ResultCycle(RDBMSEngine engine, ResultSet rs, StatementLifecycle cycle) {
        if (rs == null) {
            throw new IllegalArgumentException("null result set");
        }

        m_engine = engine;
        m_rs = rs;
        m_cycle = cycle;
        if (LOG.isInfoEnabled()) {
            m_trace = new Throwable();
        } else {
            m_trace = null;
        }
    }

    protected void finalize() {
        if (m_rs != null) {
            LOG.warn("ResultSet  was not closed.  " +
                     "Turn on INFO logging for " + this.getClass() +
                     " to see the stack trace for this ResultSet.");

            if (m_trace != null) {
                LOG.info("The ResultSet was created at: ", m_trace);
            }

            m_rs = null;
        }
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
            if (m_cycle != null) { m_cycle.endNext(e); }
            throw new WrappedError(e);
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
            if (m_cycle != null) { m_cycle.endClose(e); }
            throw new WrappedError(e);
        }
    }

}
