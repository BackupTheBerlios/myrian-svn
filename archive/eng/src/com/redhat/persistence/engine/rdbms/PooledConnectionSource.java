/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
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
package com.redhat.persistence.engine.rdbms;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.jdbc.Connections;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * PooledConnectionSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/09/07 $
 **/
public final class PooledConnectionSource implements ConnectionSource {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/engine/rdbms/PooledConnectionSource.java#4 $ by $Author: dennis $, $DateTime: 2004/09/07 10:26:15 $";

    private static final Logger s_log =
        Logger.getLogger(PooledConnectionSource.class);

    private String m_url;
    private int m_size;
    private long m_interval = 0;
    private Set m_connections = new HashSet();
    private List m_available = new ArrayList();
    private List m_untested = new ArrayList();
    private Thread m_poller = null;

    public PooledConnectionSource(String url, int size, long interval) {
        m_url = url;
        m_size = size;

        m_poller = new Poller();
        m_poller.setDaemon(true);

        setInterval(interval);

        Tester tester = new Tester();
        tester.setDaemon(true);
        tester.start();
    }

    public synchronized void setInterval(long interval) {
        long old = m_interval;
        m_interval = interval;

        if (interval > 0 && old == 0) {
            m_poller.start();
        } else if (interval == 0 && old > 0) {
            m_poller.stop();
        }
    }

    public synchronized void setSize(int size) {
        m_size = size;
    }

    public synchronized int getSize() {
        return m_size;
    }

    public synchronized Connection acquire() {
        while (true) {
            if (!m_available.isEmpty()) {
                return (Connection) m_available.remove(0);
            } else if (m_connections.size() < m_size) {
                Connection result = (Connection) Connections.acquire(m_url);
                m_connections.add(result);
                return result;
            } else {
                try { wait(); }
                catch (InterruptedException e) {
                    throw new UncheckedWrapperException(e);
                }
            }
        }
    }

    public synchronized void release(Connection conn) {
        if (!m_connections.contains(conn)) {
            throw new IllegalArgumentException
                ("connection did come from ths source: " + conn);
        }

        boolean remove;
        try {
            remove = conn.isClosed();
        } catch (SQLException e) {
            s_log.warn("error calling Connection.isClosed()", e);
            remove = true;
        }

        if (remove) {
            remove(conn);
        } else if (m_connections.size() > m_size) {
            remove(conn);
            try {
                conn.close();
            } catch (SQLException e) {
                s_log.warn("error calling Connection.close", e);
            }
        } else {
            m_available.add(conn);
        }

        notify();
    }

    private synchronized void remove(Connection conn) {
        m_connections.remove(conn);
        m_available.remove(conn);
    }

    synchronized void testAvailable() {
        synchronized (m_untested) {
            m_untested.addAll(m_available);
            m_available.clear();
            m_untested.notify();
        }
    }

    private class Poller extends Thread {
        public void run() {
            while (true) {
                try { Thread.sleep(m_interval); }
                catch (InterruptedException e) {
                    throw new UncheckedWrapperException(e);
                }
                testAvailable();
            }
        }
    }

    private class Tester extends Thread {
        public void run() {
            List untested = new ArrayList();
            while (true) {
                untested.clear();
                synchronized (m_untested) {
                    if (m_untested.isEmpty()) {
                        try { m_untested.wait(); }
                        catch (InterruptedException e) {
                            throw new UncheckedWrapperException(e);
                        }
                    }
                    untested.addAll(m_untested);
                    m_untested.clear();
                }

                for (Iterator it = untested.iterator(); it.hasNext(); ) {
                    Connection conn = (Connection) it.next();
                    SQLException e = test(conn);
                    if (e != null) {
                        s_log.warn("connection failed test", e);
                        try {
                            conn.close();
                        } catch (SQLException ex) {
                            s_log.warn
                                ("error while closing bad connection", ex);
                        }
                    }
                    release(conn);
                }
            }
        }
    }

    private static final String[] TYPES = new String[] { "TABLE" };

    private static SQLException test(Connection conn) {
        try {
            // This should guarantee a db roundtrip on any normal JDBC
            // implementation.
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, "dummy", TYPES);
            rs.close();
            return null;
        } catch (SQLException e) {
            return e;
        }
    }

}
