package com.redhat.persistence.oql;

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
 * @version $Revision: #1 $ $Date: 2004/02/04 $
 **/

public class QuerySuite extends TestSuite {

    public final static String versionId = "$Id: //core-platform/test-qgen/test/src/com/redhat/persistence/oql/QuerySuite.java#1 $ by $Author: rhs $, $DateTime: 2004/02/04 11:20:32 $";

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

    private Collection m_constraints = null;

    private void setup() throws SQLException {
        m_root = new Root();
        m_conn = Connections.acquire(RuntimeConfig.getConfig().getJDBCURL());

        PDL pdl = new PDL();
        pdl.loadResource("com/redhat/persistence/oql/test.pdl");
        pdl.emit(m_root);

        Collection tables = m_root.getTables();
        m_constraints = new ArrayList();

        Statement stmt = m_conn.createStatement();
        for (Iterator it = tables.iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            String sql = table.getSQL();
            stmt.execute(sql);
            for (Iterator iter = table.getConstraints().iterator();
                 iter.hasNext(); ) {
                Constraint con = (Constraint) iter.next();
                if (con.isDeferred()) { m_constraints.add(con); }
            }
        }

        for (Iterator it = m_constraints.iterator(); it.hasNext(); ) {
            Constraint con = (Constraint) it.next();
            stmt.execute("alter table " + con.getTable().getName() +
                         " add " + con.getSQL());
        }
        stmt.close();

        ConnectionSource src = new ConnectionSource() {
            public Connection acquire() { return m_conn; }
            public void release(Connection conn) {}
        };
        Engine engine = new RDBMSEngine(src, new PostgresWriter());
        Session ssn = new Session(m_root, engine,new DynamicQuerySource());

        DataLoader loader = new DataLoader(ssn);
        XML.parseResource("com/redhat/persistence/oql/data.xml", loader);
        ssn.flush();
    }

    private void teardown() throws SQLException {
        Statement stmt = m_conn.createStatement();
        try {
            for (Iterator it = m_constraints.iterator(); it.hasNext(); ) {
                Constraint con = (Constraint) it.next();
                stmt.execute("alter table " + con.getTable().getName() +
                             " drop constraint " + con.getName());
            }

            Collection tables = m_root.getTables();
            for (Iterator it = tables.iterator(); it.hasNext(); ) {
                Table table = (Table) it.next();
                stmt.execute("drop table " + table.getName());
            }
        } finally {
            stmt.close();
            m_conn.rollback();
            m_conn.close();
            m_constraints = null;
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
        private String m_variant = null;
        private boolean m_ordered = false;
        private StringBuffer m_query = null;
        private List m_results = null;

        public TestLoader(QuerySuite suite) {
            m_suite = suite;
        }

        public void startElement(String uri, String name, String qn,
                                 Attributes attrs) {
            if (name.equals("test")) {
                m_tests = new ArrayList();
                m_name = attrs.getValue(uri, "name");
            } else if (name.equals("query")) {
                m_variant = attrs.getValue(uri, "name");
                m_query = new StringBuffer();
                m_ordered = "true".equalsIgnoreCase
                    (attrs.getValue(uri, "ordered"));
            } else if (name.equals("results")) {
                m_results = new ArrayList();
            } else if (name.equals("row")) {
                Map row = new HashMap();
                for (int i = 0; i < attrs.getLength(); i++) {
                    row.put(attrs.getLocalName(i), attrs.getValue(i));
                }
                m_results.add(row);
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
                m_tests.add(test);
            } else if (name.equals("results")) {
                // do nothing
            } else if (name.equals("test")) {
                for (Iterator it = m_tests.iterator(); it.hasNext(); ) {
                    QueryTest test = (QueryTest) it.next();
                    test.setResults(m_results);
                    m_suite.addTest(test);
                }
                m_results = null;
            }
        }

    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

}
