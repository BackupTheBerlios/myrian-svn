/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence.oql;

import com.arsdigita.db.*;
import com.arsdigita.util.*;
import com.arsdigita.util.jdbc.*;
import com.arsdigita.runtime.*;
import com.arsdigita.xml.*;
import com.redhat.persistence.*;
import com.redhat.persistence.common.*;
import com.redhat.persistence.engine.rdbms.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.pdl.*;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import junit.framework.*;
import junit.extensions.*;

import java.math.*;
import java.sql.*;
import java.util.*;

/**
 * QuerySuite
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/05/04 $
 **/

public class QuerySuite extends TestSuite {

    public final static String versionId = "$Id: //users/rhs/persistence/test/src/com/redhat/persistence/oql/QuerySuite.java#2 $ by $Author: rhs $, $DateTime: 2004/05/04 14:28:06 $";

    public QuerySuite() {}

    public QuerySuite(Class theClass) {
        super(theClass);
    }

    public QuerySuite(String name) {
        super(name);
    }

    private Root m_root = null;
    private Connection m_conn = null;

    public Root getRoot() {
        return m_root;
    }

    public Connection getConnection() {
        return m_conn;
    }

    private void init() {
        m_root = new Root();
        m_conn = Connections.acquire(RuntimeConfig.getConfig().getJDBCURL());
        DbHelper.setDatabase(DbHelper.getDatabase(m_conn));

        PDL pdl = new PDL();
        pdl.loadResource("com/redhat/persistence/oql/test.pdl");
        pdl.emit(m_root);
    }

    private void setup() throws SQLException {
        if (m_root == null) { init(); }

        try {
            Schema.load(m_root, m_conn);
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage());
        }

        ConnectionSource src = new ConnectionSource() {
            public Connection acquire() { return m_conn; }
            public void release(Connection conn) {}
        };
        Engine engine = new RDBMSEngine(src, new PostgresWriter());
        Session ssn = new Session(m_root, engine, new QuerySource());

        DataLoader loader = new DataLoader(ssn);
        XML.parseResource("com/redhat/persistence/oql/data.xml", loader);
        ssn.flush();
    }

    private boolean commit = false;

    private void teardown() throws SQLException {
        if (m_root == null) { init(); }
        try {
            try {
                Schema.unload(m_root, m_conn);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        } finally {
            if (commit) {
                m_conn.commit();
            } else {
                m_conn.rollback();
            }
            m_conn.close();
            m_conn = null;
            m_root = null;
        }
    }

    public static Test suite() {
        final QuerySuite suite = new QuerySuite();
        TestLoader loader = new TestLoader(suite);
        XML.parseResource("com/redhat/persistence/oql/queries.xml", loader);
        TestSetup wrapper = new TestSetup(suite) {
            protected void setUp() throws SQLException {
                suite.setup();
            }
            protected void tearDown() throws SQLException {
                suite.teardown();
            }
        };
        return wrapper;
    }

    private static class DataLoader extends DefaultHandler {

        private Session m_ssn;
        private ObjectType m_type = null;
        private Object m_obj = null;

        public DataLoader(Session ssn) {
            m_ssn = ssn;
        }

        public void startElement(String uri, String name, String qn,
                                 Attributes attrs) {
            if (name.equals("data")) {
                return;
            } else if (m_type != null) {
                Property prop = m_type.getProperty(name);
                if (prop == null) {
                    throw new IllegalStateException
                        ("no such property: " + name);
                }
                Map values = values(attrs);
                Object value = decode(prop.getType(), null, values);
                m_ssn.add(m_obj, prop, value);
            } else {
                m_type = m_ssn.getRoot().getObjectType(name);
                if (m_type == null) {
                    throw new IllegalStateException("no such type: " + name);
                }

                Map values = values(attrs);

                m_obj = decode(m_type, null, values);
                Collection props = m_type.getProperties();
                for (Iterator it = props.iterator(); it.hasNext(); ) {
                    Property prop = (Property) it.next();
                    if (prop.isCollection()) { continue; }
                    Object value = decode
                        (prop.getType(), Path.get(prop.getName()), values);
                    m_ssn.set(m_obj, prop, value);
                }
            }
        }

        public void endElement(String uri, String name, String qn) {
            if (m_type != null && name.equals(m_type.getQualifiedName())) {
                m_type = null;
                m_obj = null;
            }
        }

        private Map values(Attributes attrs) {
            Map values = new HashMap();
            for (int i = 0; i < attrs.getLength(); i++) {
                Path path = Path.get(attrs.getLocalName(i));
                values.put(path, attrs.getValue(i));
            }
            return values;
        }

        private static Class[] STRING = new Class[] { String.class };

        private Object decode(ObjectType type, Path key, Map values) {
            Class klass = type.getJavaClass();
            Adapter ad = m_ssn.getRoot().getAdapter(klass);

            Collection props = type.getKeyProperties();

            if (props.isEmpty()) {
                Object value = values.get(key);
                if (value == null) {
                    return null;
                } else {
                    return Classes.newInstance
                        (klass, STRING, new Object[] { value });
                }
            } else {
                PropertyMap pmap = new PropertyMap(type);
                for (Iterator it = props.iterator(); it.hasNext(); ) {
                    Property prop = (Property) it.next();
                    Object value = decode
                        (prop.getType(), Path.add(key, prop.getName()),
                         values);
                    pmap.put(prop, value);
                }
                if (pmap.isNull()) {
                    return null;
                } else {
                    Object obj = m_ssn.retrieve(pmap);
                    if (obj == null) {
                        obj = ad.getObject(type, pmap);
                        m_ssn.create(obj);
                    }
                    return obj;
                }
            }
        }

    }

    private static class TestLoader extends DefaultHandler {

        private QuerySuite m_suite;
        private String m_name = null;
        private List m_tests = null;
        private List m_fetched = null;
        private String m_variant = null;
        private boolean m_ordered = false;
        private StringBuffer m_query = null;
        private Integer m_subselectCount = null;
        private Integer m_joinCount = null;
        private Integer m_innerCount = null;
        private Integer m_outerCount = null;
        private List m_results = null;
        private ExpectedError m_error = null;

        public TestLoader(QuerySuite suite) {
            m_suite = suite;
        }

        public void startElement(String uri, String name, String qn,
                                 Attributes attrs) {
            if (name.equals("test")) {
                m_tests = new ArrayList();
                m_fetched = new ArrayList();
                m_name = attrs.getValue(uri, "name");
            } else if (name.equals("query")) {
                m_query = new StringBuffer();
                for (int i = 0; i < attrs.getLength(); i++) {
                    String attr = attrs.getLocalName(i);
                    String value = attrs.getValue(i);
                    if (attr.equals("name")) {
                        m_variant = value;
                    } else if (attr.equals("ordered")) {
                        m_ordered = "true".equalsIgnoreCase(value);
                    } else if (attr.equals("subselects")) {
                        m_subselectCount = new Integer(value);
                    } else if (attr.equals("joins")) {
                        m_joinCount = new Integer(value);
                    } else if (attr.equals("inners")) {
                        m_innerCount = new Integer(value);
                    } else if (attr.equals("outers")) {
                        m_outerCount = new Integer(value);
                    } else if (attr.equals("fetched")) {
                        m_fetched.add(value);
                    } else {
                        throw new IllegalStateException
                            ("unrecognized attribute for query: " + attr);
                    }
                }
            } else if (name.equals("results")) {
                m_results = new ArrayList();
            } else if (name.equals("row")) {
                Map row = new HashMap();
                for (int i = 0; i < attrs.getLength(); i++) {
                    String value = attrs.getValue(i);
                    if (value.equals("null")) { value = null; }
                    row.put(attrs.getLocalName(i), value);
                }
                m_results.add(row);
            } else if (name.equals("exception")) {
                String type = attrs.getValue("type");
                String msg = attrs.getValue("msg");
                m_error = new ExpectedError(type, msg);
            }
        }

        public void characters(char[] ch, int start, int length) {
            if (m_query != null) {
                m_query.append(ch, start, length);
            }
        }

        public void endElement(String uri, String name, String qn) {
            if (name.equals("query")) {
                String query = m_query.toString();
                m_query = null;
                String tname = m_variant == null ? query : m_variant;
                if (m_name != null) {
                    tname = m_name + "[" + tname + "]";
                }
                QueryTest test =
                    new QueryTest(m_suite, tname, query, m_ordered);
                m_ordered = false;
                test.setSubselectCount(m_subselectCount);
                m_subselectCount = null;
                test.setJoinCount(m_joinCount);
                m_joinCount = null;
                test.setInnerCount(m_innerCount);
                m_innerCount = null;
                test.setOuterCount(m_outerCount);
                m_outerCount = null;
                m_tests.add(test);
                if (m_fetched.size() < m_tests.size()) {
                    m_fetched.add(null);
                }
            } else if (name.equals("results")) {
                // do nothing
            } else if (name.equals("test")) {
                for (int i = 0; i < m_tests.size(); i++) {
                    QueryTest test = (QueryTest) m_tests.get(i);
                    String fetched = (String) m_fetched.get(i);
                    test.setResults(filter(m_results, fetched));
                    test.setError(m_error);
                    m_suite.addTest(test);
                }
                m_tests = null;
                m_fetched = null;
                m_results = null;
                m_error = null;
            }
        }

    }

    private static List filter(List rows, String fetched) {
        if (fetched == null) { return rows; }

        Set fetchSet = new HashSet();
        String[] parts = StringUtils.split(fetched, ',');
        for (int i = 0; i < parts.length; i++) {
            fetchSet.add(parts[i]);
        }

        List result = new ArrayList(rows.size());
        for (int i = 0; i < rows.size(); i++) {
            Map row = (Map) rows.get(i);
            Map filtered = new HashMap();
            for (Iterator it = row.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry me = (Map.Entry) it.next();
                if (fetchSet.contains(me.getKey())) {
                    filtered.put(me.getKey(), me.getValue());
                }
            }
            result.add(filtered);
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        QuerySuite suite = new QuerySuite();
        suite.commit = true;
        String cmd = "setup";
        if (args.length > 0) {
            cmd = args[0];
        }
        if (cmd.equals("setup")) {
            suite.setup();
        } else if (cmd.equals("teardown")) {
            suite.teardown();
        } else if (cmd.equals("run")) {
            junit.textui.TestRunner.run(suite());
        } else {
            System.err.println("unknown command: " + cmd);
            return;
        }
        Connection conn = suite.getConnection();
        if (conn != null) { conn.commit(); }
    }

}
