/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.persistence;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.jdbc.Connections;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * PooledConnectionSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/

public class PooledConnectionSource implements ConnectionSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/PooledConnectionSource.java#5 $ by $Author: dennis $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(PooledConnectionSource.class);

    private String m_url;
    private int m_size;
    private long m_interval;
    private Set m_connections = new HashSet();
    private List m_available = new ArrayList();
    private List m_untested = new ArrayList();

    public PooledConnectionSource(String url, int size, long interval) {
        m_url = url;
        m_size = size;
        m_interval = interval;

        Tester tester = new Tester();
        tester.setDaemon(true);
        tester.start();

        if (m_interval > 0) {
            Poller poller = new Poller();
            poller.setDaemon(true);
            poller.start();
        }
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
