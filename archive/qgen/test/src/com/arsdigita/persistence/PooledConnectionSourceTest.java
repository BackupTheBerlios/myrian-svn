package com.arsdigita.persistence;

import com.arsdigita.util.*;
import com.arsdigita.util.jdbc.*;
import com.mockobjects.sql.*;

import junit.framework.TestCase;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * PooledConnectionSourceTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public class PooledConnectionSourceTest extends TestCase {

    public final static String versionId = "$Id: //core-platform/test-qgen/test/src/com/arsdigita/persistence/PooledConnectionSourceTest.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    private static final String JDBC_PREFIX = "jdbc:test:";
    private static final Map CONNECTIONS = new HashMap();

    private static synchronized List getConnections(String url) {
        if (CONNECTIONS.containsKey(url)) {
            return (List) CONNECTIONS.get(url);
        } else {
            List result = new ArrayList();
            CONNECTIONS.put(url, result);
            return result;
        }
    }

    static {
        try {
            DriverManager.registerDriver(new TestDriver());
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    private String m_prefix;
    public PooledConnectionSourceTest(String name) {
        super(name);
        // Get test unique url
        m_prefix = JDBC_PREFIX + System.identityHashCode(this) + ":";
    }

    public void testPooling() {
        String url = m_prefix + "pooling";
        ConnectionSource src = new PooledConnectionSource(url, 10, 5000);
        Connection conn = src.acquire();
        src.release(conn);
        src.acquire();
        // Should only have acquired one connection
        assertEquals
            ("incorrect connection count", 1, getConnections(url).size());
    }

    public void testSize() {
        String url = m_prefix + "size";
        int size = 10;
        final ConnectionSource src =
            new PooledConnectionSource(url, size, 5000);

        assertEquals
            ("incorrect connection count", 0, getConnections(url).size());

        Connection conn = null;
        for (int i = 0; i < size; i++) {
            conn = src.acquire();
        }

        assertEquals
            ("incorrect connection count", size, getConnections(url).size());

        final boolean[] acquired = { false };
        Thread thread = new Thread(new Runnable() {
            public void run() {
                src.acquire();
                acquired[0] = true;
            }
        });

        try {
            thread.start();
            thread.join(1000);
            assertTrue("acquire did not block", !acquired[0]);
            src.release(conn);
            thread.join();
            assertTrue("acquire did not succeed", acquired[0]);
        } catch (InterruptedException e) {
            throw new UncheckedWrapperException(e);
        }

        assertEquals
            ("incorrect connection count", size, getConnections(url).size());
    }

    public void testReconnect() {
        String url = m_prefix + "fail:reconnect";
        int size = 10;
        PooledConnectionSource src =
            new PooledConnectionSource(url, size, 10000);

        Connection[] conns = new Connection[size];
        for (int i = 0; i < size; i++) {
            conns[i] = src.acquire();
        }
        for (int i = 0; i < size; i++) {
            src.release(conns[i]);
        }

        src.testAvailable();

        for (int i = 0; i < size; i++) {
            src.acquire();
        }

        assertEquals("reconnect failed", 2*size, getConnections(url).size());
    }

    public void testFairness() {
        String url = m_prefix + "fairness";
        int size = 10;
        ConnectionSource src = new PooledConnectionSource(url, size, 5000);

        // These threads will compete for the resources in the
        // connection pool in an environment with high thread
        // contention.
        Competitor[] competitors = new Competitor[10];
        for (int i = 0; i < competitors.length; i++) {
            competitors[i] = new Competitor(src, 30000);
        }

        // These threads will create a highly contentious enviornment
        // for the competing threads. We want significantly more of
        // these threads than there are competing threads so that the
        // overall contention remains relatively constant during the
        // time in which the competing threads are only partially
        // started or exited.
        Competitor[] contenders = new Competitor[10*competitors.length];
        for (int i = 0; i < contenders.length; i++) {
            Competitor c = new Competitor(src);
            c.setDaemon(true);
            c.start();
            contenders[i] = c;
        }

        // Start the competing threads.
        for (int i = 0; i < competitors.length; i++) {
            competitors[i].start();
        }

        // Wait for the competing threads.
        try {
            for (int i = 0; i < competitors.length; i++) {
                competitors[i].join();
            }
        } catch (InterruptedException e) {
            throw new UncheckedWrapperException(e);
        }

        // Turn off the contention.
        for (int i = 0; i < contenders.length; i++) {
            contenders[i].exit();
        }

        // Check that we did not exceed the connection pool size limit.
        assertEquals
            ("incorrect connection count", size, getConnections(url).size());

        // Compute our fairness metric and assert that it is within a
        // tolerable range.
        int min = competitors[0].getCount();
        int max = 0;
        int sum = 0;
        for (int i = 0; i < competitors.length; i++) {
            int count = competitors[i].getCount();
            if (count < min) { min = count; }
            if (count > max) { max = count; }
            sum += count;
        }
        double avg = ((double)sum)/competitors.length;

        double minscaled = min/avg;
        double maxscaled = max/avg;

        assertTrue
            ("unfair pooling results:" +
             "\n  min = " + min +
             "\n  max = " + max +
             "\n  avg = " + avg +
             "\n  minscaled = " + minscaled +
             "\n  maxscaled = " + maxscaled,
             (minscaled > 0.1) && (maxscaled < 10));
    }

    private static class Competitor extends Thread {

        private ConnectionSource m_src;
        private long m_length;
        private int m_count;
        private boolean m_exit = false;

        public Competitor(ConnectionSource src, long length) {
            m_src = src;
            m_length = length;
            m_count = 0;
        }

        public Competitor(ConnectionSource src) {
            this(src, 0);
        }

        public int getCount() {
            return m_count;
        }

        public void exit() {
            m_exit = true;
        }

        public void run() {
            long start = System.currentTimeMillis();

            long elapsed;
            do {
                elapsed = System.currentTimeMillis() - start;
                Connection conn = m_src.acquire();
                m_count++;
                m_src.release(conn);
            } while (!m_exit
                     && (m_length == 0 || elapsed < m_length));
        }
    }

    private static class TestDriver extends MockDriver {
        public boolean acceptsURL(String url) {
            return url.startsWith(JDBC_PREFIX);
        }

        public Connection connect(final String url, Properties info) {
            if (!acceptsURL(url)) {
                return null;
            } else {
                Connection result = new MockConnection() {

                    public DatabaseMetaData getMetaData() throws SQLException {
                        if (url.indexOf(":fail:") >= 0) {
                            throw new SQLException("mock fatal error");
                        }

                        return new MockDatabaseMetaData() {
                            public ResultSet getTables
                                (String cat, String scm, String tbl,
                                 String[] types) {
                                return new MockSingleRowResultSet();
                            }
                        };
                    }

                    private boolean m_closed = false;

                    public boolean isClosed() {
                        return m_closed;
                    }

                    public void close() {
                        m_closed = true;
                    }
                };
                List conns = getConnections(url);
                conns.add(result);
                return result;
            }
        }
    }

}
