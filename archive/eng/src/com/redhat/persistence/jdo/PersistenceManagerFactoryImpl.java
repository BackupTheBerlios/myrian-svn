package com.redhat.persistence.jdo;

import com.arsdigita.db.DbHelper;
import com.redhat.persistence.Engine;
import com.redhat.persistence.QuerySource;
import com.redhat.persistence.Session;
import com.redhat.persistence.engine.rdbms.ConnectionSource;
import com.redhat.persistence.engine.rdbms.PooledConnectionSource;
import com.redhat.persistence.engine.rdbms.OracleWriter;
import com.redhat.persistence.engine.rdbms.PostgresWriter;
import com.redhat.persistence.engine.rdbms.RDBMSEngine;
import com.redhat.persistence.metadata.Root;
import java.util.*;
import java.io.*;
import java.sql.*;
import javax.jdo.*;

public class PersistenceManagerFactoryImpl
    implements PersistenceManagerFactory, Serializable {

    /* See pp. 70-71, Section 8.5 "PersistenceManagerFactory methods":
     *
     * JDO implementations might manage a map of instantiated
     * PersistenceManagerFactory instances based on specified property key
     * values, and return a previously instantiated PersistenceManagerFactory
     * instance. In this case, the properties of the returned instance must
     * exactly match the requested properties.
     */
    private final static Map s_instances = new HashMap();

    private static Collection m_options;
    static {
        Set options = new HashSet();
        options.add("javax.jdo.query.JDOQL");
        // javax.jdo.option.TransientTransactional
        // javax.jdo.option.NontransactionalRead
        // javax.jdo.option.NontransactionalWrite
        // javax.jdo.option.RetainValues
        // javax.jdo.option.RestoreValues
        // javax.jdo.option.Optimistic
        // javax.jdo.option.ApplicationIdentity
        options.add("javax.jdo.query.DatastoreIdentity");
        // javax.jdo.option.NonDatastoreIdentity
        // javax.jdo.option.ArrayList
        // javax.jdo.option.HashMap
        // javax.jdo.option.Hashtable
        // javax.jdo.option.LinkedList
        // javax.jdo.option.TreeMap
        // javax.jdo.option.TreeSet
        // javax.jdo.option.Vector
        // javax.jdo.option.Map
        // javax.jdo.option.List
        // javax.jdo.option.Array
        // javax.jdo.option.NullCollection

        m_options = Collections.unmodifiableCollection(options);
    }

    private String m_user = "";
    private String m_pass = "";
    private String m_url = "";
    private String m_driver = "";

    private int m_minPool = 1;
    private int m_maxPool = 10;
    private int m_mswait = 100;

    private ConnectionSource m_connSrc;

    private transient Root m_root = new Root();

    private static boolean bool(String value) {
        // sec 11.1 of spec
        return value != null && value.toLowerCase().equals("true");
    }

    private PersistenceManagerFactoryImpl() { }

    public static PersistenceManagerFactory getPersistenceManagerFactory(
        Properties props) {


        synchronized(s_instances) {
            PersistenceManagerFactoryImpl result =
                (PersistenceManagerFactoryImpl) s_instances.get(props);
            if (result == null) {
                result = new PersistenceManagerFactoryImpl(props);
                // XXX: the polling interval is currently hardcoded to 0
                result.m_connSrc = new PooledConnectionSource
                    (result.m_url, result.m_maxPool, 0);
                s_instances.put(props, result);

            }
            return result;
        }

    }

    public PersistenceManagerFactoryImpl(Properties p) {
        for (Enumeration e = p.propertyNames(); e.hasMoreElements(); ) {
            String k = (String) e.nextElement();
            String v = p.getProperty(k);

            if ("javax.jdo.option.NontransactionalRead".equals(k)) {
                setNontransactionalRead(bool(v));
            } else if ("javax.jdo.option.NontransactionalWrite".equals(k)) {
                setNontransactionalWrite(bool(v));
            } else if ("javax.jdo.option.RetainValues".equals(k)) {
                setRetainValues(bool(v));
            } else if ("javax.jdo.option.RestoreValues".equals(k)) {
                setRestoreValues(bool(v));
            } else if ("javax.jdo.option.Optimistic".equals(k)) {
                setOptimistic(bool(v));
            } else if ("javax.jdo.option.IgnoreCache".equals(k)) {
                setIgnoreCache(bool(v));
            } else if ("javax.jdo.option.Multithreaded".equals(k)) {
                setMultithreaded(bool(v));
            } else if ("javax.jdo.option.ConnectionFactoryName".equals(k)) {
                setConnectionFactoryName(v);
            } else if ("javax.jdo.option.ConnectionFactory2Name".equals(k)) {
                setConnectionFactory2Name(v);
            } else if ("javax.jdo.option.ConnectionUserName".equals(k)) {
                _setConnectionUserName(v);
            } else if ("javax.jdo.option.ConnectionPassword".equals(k)) {
                _setConnectionPassword(v);
            } else if ("javax.jdo.option.ConnectionURL".equals(k)) {
                _setConnectionURL(v);
            } else if ("javax.jdo.option.ConnectionDriverName".equals(k)) {
                _setConnectionDriverName(v);
            } else {
                // See 11.1 Interface PersistenceManagerFactory, p. 80:
                // Any property not recognized by the implementation must be
                // silently ignored.
                ;
            }
        }
    }

    public Root getMetadataRoot() {
        return m_root;
    }

    public Properties getProperties() {
        Properties p = new Properties();
        p.setProperty("VendorName", "XXX");
        p.setProperty("VendorVersion", "XXX");

        return p;
    }

    public Collection supportedOptions() {
        return m_options;
    }

    public void close() {
        throw new Error("not implemented");
    }

    public PersistenceManager getPersistenceManager() {
        return getPersistenceManager(m_user, m_pass);
    }

    public PersistenceManager getPersistenceManager(String user, String pw) {
        // XXX: this currently ignores "user" and "pw"

        final RDBMSEngine engine;
        switch (DbHelper.getDatabaseFromURL(m_url)) {
        case DbHelper.DB_ORACLE:
            engine = new RDBMSEngine(m_connSrc, new OracleWriter());
            break;
        case DbHelper.DB_POSTGRES:
            engine = new RDBMSEngine(m_connSrc, new PostgresWriter());
            break;
        default:
            DbHelper.unsupportedDatabaseError("persistence");
            throw new IllegalStateException("unreachable stmt to appease javac");
        }

        Session ssn = new Session(m_root, engine, new QuerySource());
        return new PersistenceManagerImpl(ssn) {
                public Connection getConnection() {
                    return engine.getConnection();
                }
            };
    }

    public String getConnectionDriverName() {
        return m_driver;
    }

    /*
     * See pp. 70-71 and p. 84: The returned PersistenceManagerFactory is not
     * configurable (the setXXX methods will throw an exception).
     */
    private void unmodifiable() {
        throw new UnsupportedOperationException("unmodifiable");
    }

    public void setConnectionDriverName(String value) {
        unmodifiable();
    }

    private void _setConnectionDriverName(String value) {
        try {
            Class.forName(value);
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException("class not found " + value);
        }
        m_driver = value;
    }

    public String getConnectionURL() {
        return m_url;
    }

    public void setConnectionURL(String value) {
        throw new UnsupportedOperationException("unmodifiable");
    }

    private void _setConnectionURL(String value) {
        m_url = value;
    }

    public String getConnectionUserName() {
        return m_user;
    }


    public void setConnectionUserName(String value) {
        unmodifiable();

    }

    private void _setConnectionUserName(String value) {
        m_user = value;
    }

    public void setConnectionPassword(String value) {
        unmodifiable();
    }

    private void _setConnectionPassword(String value) {
        m_pass = value;
    }

    public String getConnectionFactoryName() {
        throw new JDOFatalUserException("not implemented");
    }

    public void setConnectionFactoryName(String value) {
        throw new JDOFatalUserException("not implemented");
    }

    public Object getConnectionFactory() {
        throw new JDOFatalUserException("not implemented");
    }

    public void setConnectionFactory(Object value) {
        throw new JDOFatalUserException("not implemented");
    }

    public String getConnectionFactory2Name() {
        throw new JDOFatalUserException("not implemented");
    }

    public void setConnectionFactory2Name(String value) {
        throw new JDOFatalUserException("not implemented");
    }

    public Object getConnectionFactory2() {
        throw new JDOFatalUserException("not implemented");
    }

    public void setConnectionFactory2(Object value) {
        throw new JDOFatalUserException("not implemented");
    }

    public boolean getIgnoreCache() {
        return false;
    }

    public void setIgnoreCache(boolean value) {
        throw new JDOFatalUserException("not implemented");
    }

    public int getMinPool() {
        return m_minPool;
    }


    public void setMinPool(int value) {
        unmodifiable();
    }

    private void _setMinPool(int value) {
        m_minPool = value;
    }

    public int getMaxPool() {
        return m_maxPool;
    }

    public void setMaxPool(int value) {
        unmodifiable();
    }

    private void _setMaxPool(int value) {
        m_maxPool = value;
    }

    public int getMsWait() {
        return m_mswait;
    }

    public void setMsWait(int value) {
        unmodifiable();
    }

    private void _setMsWait(int value) {
        m_mswait = value;
    }

    public boolean getMultithreaded() {
        return false;
    }

    public void setMultithreaded(boolean value) {
        throw new JDOFatalUserException("not supported");
    }

    public boolean getNontransactionalRead() {
        return false;
    }

    public void setNontransactionalRead(boolean value) {
        throw new JDOUnsupportedOptionException("NontransactionalRead");
    }

    public boolean getNontransactionalWrite() {
        return false;
    }

    public void setNontransactionalWrite(boolean value) {
        throw new JDOUnsupportedOptionException("NontransactionalWrite");
    }

    public boolean getOptimistic() {
        return false;
    }

    public void setOptimistic(boolean value) {
        throw new JDOUnsupportedOptionException("Optimistic");
    }

    public boolean getRestoreValues() {
        return false;
    }

    public void setRestoreValues(boolean value) {
        throw new JDOUnsupportedOptionException("RestoreValues");
    }

    public boolean getRetainValues() {
        return false;
    }

    public void setRetainValues(boolean value) {
        throw new JDOUnsupportedOptionException("RetainValues");
    }
}
