package com.redhat.persistence.oql;

import junit.framework.*;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * QueryTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/02/11 $
 **/

public class QueryTest extends TestCase {

    // We don't really need to extend TestCase, we could just
    // implement Test exception for the reflection magic that the ant
    // junit task does to report the test name depends on the test
    // being an instance of TestCase. Later versions of ant don't
    // suffer from this problem.

    public final static String versionId = "$Id: //core-platform/test-qgen/test/src/com/redhat/persistence/oql/QueryTest.java#4 $ by $Author: jorris $, $DateTime: 2004/02/11 12:32:30 $";

    private static final Logger s_log = Logger.getLogger(QueryTest.class);

    private QuerySuite m_suite;
    private String m_name;
    private boolean m_ordered;
    private String m_query;
    private List m_results = null;
    private final ExpectedError m_expectedError;
    //private static final String NL = System.getProperty("line.separator");

    public QueryTest(QuerySuite suite, 
                     String name, 
                     String query,
                     boolean ordered,
                     ExpectedError expectedError) {
        m_suite = suite;
        m_name = name;
        m_query = query;
        m_ordered = ordered;
        m_expectedError = expectedError;
    }

    void setResults(List results) {
        m_results = results;
    }

    public int countTestCases() { return 1; }

    public void run(TestResult result) {
        result.startTest(this);
        try {
            s_log.info("Query:\n" + m_query);
            OQLParser p = new OQLParser(new StringReader(m_query));
            Query q = p.query();
            s_log.info("Query(Parsed):\n" + q);
            if (p.query() != null) {
                throw new IllegalArgumentException
                    ("query string includes multiple queries: " + m_query);
            }
            String sql = q.generate(m_suite.getRoot());
            s_log.info("SQL:\n" + sql);
            Connection conn = m_suite.getConnection();
            Statement stmt = conn.createStatement();
            try {
                stmt.execute(sql);
                ResultSet rs = stmt.getResultSet();
                ResultSetMetaData md = rs.getMetaData();
                int ncols = md.getColumnCount();
                List results = new ArrayList();
                while (rs.next()) {
                    Map row = new HashMap();
                    for (int i = 1; i < ncols + 1; i++) {
                        String name = md.getColumnName(i);
                        String value = rs.getString(i);
                        row.put(name, value);
                    }
                    results.add(row);
                }
                if (m_ordered) {
                    assertEquals
                        ("sql:\n\n" + sql + "\n\n", m_results, results);
                } else {
                    MultiSet expected = new MultiSet();
                    expected.addAll(m_results);
                    MultiSet actual = new MultiSet();
                    actual.addAll(results);
                    MultiSet missing = new MultiSet(expected);
                    missing.removeAll(actual);
                    MultiSet extra = new MultiSet(actual);
                    extra.removeAll(expected);
                    assertEquals
                        ("sql:\n\n" + sql + "\n\nmissing: " + missing +
                         "\n\nextra: " + extra + "\n\n", expected, actual);
                }
            } catch (SQLException e) {
                fail("sql:\n\n" + sql + "\n\n" + e.getMessage());
            } finally {
                stmt.close();
            }

        } catch (AssertionFailedError e) {
            if (isUnexpected(e)) {
                result.addFailure(this, e);
            }
        } catch (Throwable t) {
            if (isUnexpected(t)) {
                result.addError(this, t);
            }
        } finally {
            result.endTest(this);
        }
    }

    public String getName() {
        if (m_name == null) {
            return m_query;
        } else {
            return m_name;
        }
    }

    public String toString() {
        return m_query;
    }

    boolean isUnexpected(final Throwable t) {
        if (m_expectedError == null) {
            return true;
        }
        return !m_expectedError.isExpected(t);
    }

}
