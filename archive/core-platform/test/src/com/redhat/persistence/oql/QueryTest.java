/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.redhat.persistence.oql;

import com.arsdigita.db.DbHelper;
import com.redhat.persistence.metadata.*;

import junit.framework.*;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * QueryTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/03/30 $
 **/

public class QueryTest extends TestCase {

    // We don't really need to extend TestCase, we could just
    // implement Test exception for the reflection magic that the ant
    // junit task does to report the test name depends on the test
    // being an instance of TestCase. Later versions of ant don't
    // suffer from this problem.

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/redhat/persistence/oql/QueryTest.java#2 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    private static final Logger s_log = Logger.getLogger(QueryTest.class);

    private QuerySuite m_suite;
    private String m_name;
    private boolean m_ordered;
    private String m_query;
    private List m_results = null;
    private ExpectedError m_error = null;
    private Integer m_subselectCount = null;
    private Integer m_joinCount = null;
    private Integer m_innerCount = null;
    private Integer m_outerCount = null;
    //private static final String NL = System.getProperty("line.separator");

    public QueryTest(QuerySuite suite, 
                     String name, 
                     String query,
                     boolean ordered) {
        m_suite = suite;
        m_name = name;
        m_query = query;
        m_ordered = ordered;
    }

    void setResults(List results) {
        m_results = results;
    }

    void setError(ExpectedError error) {
        m_error = error;
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
            Connection conn = m_suite.getConnection();
            Root root = m_suite.getRoot();
            Code sql = q.generate
                (root, DbHelper.getDatabase(conn) == DbHelper.DB_ORACLE);
            s_log.info("SQL:\n" + sql);
            PreparedStatement stmt = conn.prepareStatement(sql.getSQL());
            try {
                List bindings = sql.getBindings();
                for (int i = 0; i < bindings.size(); i++) {
                    Code.Binding b = (Code.Binding) bindings.get(i);
                    Object value = b.getValue();
                    Adapter ad = root.getAdapter(value.getClass());
                    ad.bind(stmt, i + 1, value, b.getType());
                }
                stmt.execute();
                ResultSet rs = stmt.getResultSet();
                ResultSetMetaData md = rs.getMetaData();
                int ncols = md.getColumnCount();
                List results = new ArrayList();
                while (rs.next()) {
                    Map row = new HashMap();
                    for (int i = 1; i < ncols + 1; i++) {
                        String name = md.getColumnName(i);
                        if (name.equals("ROWNUM__")) {
                            continue;
                        }
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
                SelectParser parser = new SelectParser(sql.getSQL());
                if (m_subselectCount != null) {
                    assertEquals
                        ("Incorrect subselect count in sql: " + sql,
                         m_subselectCount.intValue(),
                         parser.getSubselectCount());
                }

                if (m_joinCount != null) {
                    assertEquals
                        ("Incorrect join count in sql: " + sql,
                         m_joinCount.intValue(), parser.getJoinCount());
                }

                if (m_innerCount != null) {
                    assertEquals
                        ("Incorrect inner join count in sql: " + sql,
                         m_innerCount.intValue(), parser.getInnerCount());
                }

                if (m_outerCount != null) {
                    assertEquals
                        ("Incorrect outer join count in sql: " + sql,
                         m_outerCount.intValue(), parser.getOuterCount());
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
        if (m_error == null) {
            return true;
        }
        return !m_error.isExpected(t);
    }

    public void setSubselectCount(Integer subselectCount) {
        m_subselectCount = subselectCount;
    }

    public void setJoinCount(Integer joinCount) {
        m_joinCount = joinCount;
    }

    public void setInnerCount(Integer innerCount) {
        m_innerCount = innerCount;
    }

    public void setOuterCount(Integer outerCount) {
        m_outerCount = outerCount;
    }

}
