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
package org.myrian.persistence.oql;

import org.myrian.db.DbHelper;
import org.myrian.persistence.Session;
import org.myrian.persistence.metadata.*;

import junit.framework.*;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * QueryTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

public class QueryTest extends TestCase {


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

    protected void runTest() throws Exception {
        try {
            s_log.info("Query:\n" + m_query);
            OQLParser p = new OQLParser
                (new StringReader(m_query), m_suite.getLiterals());
            Query q = p.query();
            s_log.info("Query(Parsed):\n" + q);
            if (p.query() != null) {
                throw new IllegalArgumentException
                    ("query string includes multiple queries: " + m_query);
            }
            Connection conn = m_suite.getConnection();
            Session ssn = m_suite.getSession();
            Code sql = q.generate
                (ssn, DbHelper.getDatabase(conn) == DbHelper.DB_ORACLE);
            s_log.info("SQL:\n" + sql);
            PreparedStatement stmt = conn.prepareStatement(sql.getSQL());
            try {
                List bindings = sql.getBindings();
                for (int i = 0; i < bindings.size(); i++) {
                    Code.Binding b = (Code.Binding) bindings.get(i);
                    Object value = b.getValue();
                    Adapter ad = ssn.getRoot().getAdapter(value.getClass());
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
        } catch (Exception e) {
            if (isUnexpected(e)) {
                throw e;
            }
        }
    }

    public String getName() {
        if (m_name == null) {
            return m_query;
        } else {
            return m_name;
        }
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