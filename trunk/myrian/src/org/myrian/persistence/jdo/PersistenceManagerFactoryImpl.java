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
package org.myrian.persistence.jdo;

import org.myrian.db.DbHelper;
import org.myrian.persistence.QuerySource;
import org.myrian.persistence.Session;
import org.myrian.persistence.engine.rdbms.ConnectionSource;
import org.myrian.persistence.engine.rdbms.OracleWriter;
import org.myrian.persistence.engine.rdbms.PooledConnectionSource;
import org.myrian.persistence.engine.rdbms.PostgresWriter;
import org.myrian.persistence.engine.rdbms.RDBMSEngine;
import org.myrian.persistence.metadata.Root;
import org.myrian.persistence.profiler.rdbms.StatementProfiler;
import java.io.*;
import java.sql.*;
import java.util.*;

import javax.jdo.*;
import javax.jdo.spi.JDOImplHelper;

import org.apache.log4j.Logger;

public class PersistenceManagerFactoryImpl
    implements PersistenceManagerFactory, Serializable {

    private final static Logger s_log =
        Logger.getLogger(PersistenceManagerFactoryImpl.class);

    /* See pp. 70-71, Section 8.5 "PersistenceManagerFactory methods":
     *
     * JDO implementations might manage a map of instantiated
     * PersistenceManagerFactory instances based on specified property key
     * values, and return a previously instantiated PersistenceManagerFactory
     * instance. In this case, the properties of the returned instance must
     * exactly match the requested properties.
     */
    private final static Map s_instances = new HashMap();

    private static final PDLGenerator s_generator =
        new PDLGenerator(new Root());
    static {
        JDOImplHelper.getInstance().addRegisterClassListener(s_generator);
    }

    private static final Registrar s_registrar = new Registrar();

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
        options.add("javax.jdo.option.Map");
        options.add("javax.jdo.option.List");
        // javax.jdo.option.Array
        // javax.jdo.option.NullCollection

        m_options = Collections.unmodifiableCollection(options);
    }

    private boolean m_mutable = true;

    private String m_user = "";
    private String m_pass = "";
    private String m_url = "";
    private String m_driver = "";

    private int m_minPool = 0;
    private static final int s_defaultMaxPool = 10;
    private int m_mswait = 100;

    private PooledConnectionSource m_connSrc;

    //private transient Root m_root = new Root();

    private static boolean bool(String value) {
        // sec 11.1 of spec
        return value != null && value.toLowerCase().equals("true");
    }

    public static PersistenceManagerFactory getPersistenceManagerFactory(
        Properties props) {

        synchronized(s_instances) {
            PersistenceManagerFactoryImpl result =
                (PersistenceManagerFactoryImpl) s_instances.get(props);
            if (result == null) {
                result = new PersistenceManagerFactoryImpl(props);
                s_instances.put(props, result);

            }
            return result;
        }

    }

    public PersistenceManagerFactoryImpl() {}

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
                setConnectionUserName(v);
            } else if ("javax.jdo.option.ConnectionPassword".equals(k)) {
                setConnectionPassword(v);
            } else if ("javax.jdo.option.ConnectionURL".equals(k)) {
                setConnectionURL(v);
            } else if ("javax.jdo.option.ConnectionDriverName".equals(k)) {
                setConnectionDriverName(v);
            } else {
                // See 11.1 Interface PersistenceManagerFactory, p. 80:
                // Any property not recognized by the implementation must be
                // silently ignored.
                ;
            }
        }
        m_mutable = false;
    }

    static Root getMetadataRoot() {
        return s_generator.getRoot();
    }

    public Properties getProperties() {
        Properties p = new Properties();
        p.setProperty("VendorName", "myrian");
        p.setProperty("VersionNumber", "XXX");

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
        StatementProfiler prof = new StatementProfiler();
        switch (DbHelper.getDatabaseFromURL(m_url)) {
        case DbHelper.DB_ORACLE:
            engine = new RDBMSEngine(m_connSrc, new OracleWriter(), prof);
            break;
        case DbHelper.DB_POSTGRES:
            engine = new RDBMSEngine(m_connSrc, new PostgresWriter(), prof);
            break;
        default:
            DbHelper.unsupportedDatabaseError("persistence");
            throw new IllegalStateException("unreachable stmt to appease javac");
        }

        Session ssn = new Session(s_generator.getRoot(), engine,
                                  new QuerySource());
        return new PersistenceManagerImpl(this, ssn, prof, s_registrar);
    }

    ConnectionSource getConnectionSource() {
        return m_connSrc;
    }

    public String getConnectionDriverName() {
        return m_driver;
    }

    /*
     * See pp. 70-71 and p. 84: The returned PersistenceManagerFactory is not
     * configurable (the setXXX methods will throw an exception).
     */
    private void checkMutability() {
        if (!m_mutable) {
            throw new JDOFatalUserException
                ("not a mutable PersistenceManagerFactory");
        }
    }

    public void setConnectionDriverName(String value) {
        checkMutability();
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
        checkMutability();
        m_url = value;
        // XXX: the polling interval is currently hardcoded to 0
        m_connSrc = new PooledConnectionSource(m_url, s_defaultMaxPool, 0);
    }

    public String getConnectionUserName() {
        return m_user;
    }


    public void setConnectionUserName(String value) {
        checkMutability();
        m_user = value;
    }

    public void setConnectionPassword(String value) {
        checkMutability();
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
        if (!value) {
            throw new JDOFatalUserException("not implemented");
        }
    }

    public int getMinPool() {
        return m_minPool;
    }


    public void setMinPool(int value) {
        checkMutability();
        m_minPool = value;
    }

    public int getMaxPool() {
        return m_connSrc.getSize();
    }

    public void setMaxPool(int value) {
        checkMutability();
        m_connSrc.setSize(value);
    }

    public int getMsWait() {
        return m_mswait;
    }

    public void setMsWait(int value) {
        checkMutability();
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

    private static class Registrar implements ClassInfo {
        private final Map m_classFields;
        private final Map m_classTypes;
        private final Map m_fieldFlags;

        Registrar() {
            m_classFields = new HashMap();
            m_classTypes  = new HashMap();
            m_fieldFlags  = new HashMap();
        }

        private void registerClass(Class klass) {
            if (m_classFields.containsKey(klass)) { return; }

            m_classFields.put(klass,
                              Collections.unmodifiableList(cacheFields(klass)));
            m_classTypes.put(klass,
                             Collections.unmodifiableList(cacheTypes(klass)));
            m_fieldFlags.put(klass, cacheFieldFlags(klass));
        }

        private static List cacheFields(Class pcClass) {
            List fields = new ArrayList();
            JDOImplHelper helper = JDOImplHelper.getInstance();

            for (Class klass=pcClass;
                 klass != null;
                 klass = helper.getPersistenceCapableSuperclass(klass)) {

                String[] names = helper.getFieldNames(klass);
                if (names.length == 0) { continue; }
                List current = new ArrayList(Arrays.asList(names));
                current.addAll(fields);
                fields = current;
            }
            return fields;
        }

        private static List cacheTypes(Class pcClass) {
            List types = new ArrayList();
            JDOImplHelper helper = JDOImplHelper.getInstance();

            for (Class klass=pcClass;
                 klass != null;
                 klass = helper.getPersistenceCapableSuperclass(klass)) {

                Class[] ftypes = helper.getFieldTypes(klass);
                if (ftypes.length == 0) { continue; }
                List current = new ArrayList(ftypes.length);
                for (int ii=0; ii<ftypes.length; ii++) {
                    current.add(ftypes[ii]);
                }
                current.addAll(types);
                types = current;
            }
            return types;
        }

        private static byte[] cacheFieldFlags(Class pcClass) {
            List flags = new ArrayList();
            JDOImplHelper helper = JDOImplHelper.getInstance();

            for (Class klass=pcClass;
                 klass != null;
                 klass = helper.getPersistenceCapableSuperclass(klass)) {

                byte[] fFlags = helper.getFieldFlags(klass);
                if (fFlags.length == 0) { continue; }
                List current = new ArrayList(fFlags.length);
                for (int ii=0; ii<fFlags.length; ii++) {
                    current.add(new Byte(fFlags[ii]));
                }
                current.addAll(flags);
                flags = current;
            }
            byte[] result = new byte[flags.size()];
            int idx = 0;
            for (Iterator ii=flags.iterator(); ii.hasNext(); ) {
                Byte flag = (Byte) ii.next();
                result[idx++] = flag.byteValue();
            }
            return result;
        }

        private static String superclass(Class superclass) {
            if (superclass == null) { return ""; }
            return " (inherits from " + superclass.getName() + ")";
        }

        // Implementation of ClassInfo

        public synchronized List getAllFields(Class pcClass) {
            registerClass(pcClass);
            return (List) m_classFields.get(pcClass);
        }

        public synchronized List getAllTypes(Class pcClass) {
            registerClass(pcClass);
            return (List) m_classTypes.get(pcClass);
        }

        // XXX: should we worry about returning a modifiable array?
        public synchronized byte[] getAllFieldFlags(Class pcClass) {
            registerClass(pcClass);
            return (byte[]) m_fieldFlags.get(pcClass);
        }

        public String numberToName(Class pcClass, int fieldNumber) {
            return (String) getAllFields(pcClass).get(fieldNumber);
        }

        public Class numberToType(Class pcClass, int fieldNumber) {
            return (Class) getAllTypes(pcClass).get(fieldNumber);
        }
        /**
         * Returns the first occurrence of the specified field in the most derived
         * class.
         **/
        public int nameToNumber(Class pcClass, String fieldName) {
            return getAllFields(pcClass).lastIndexOf(fieldName);
        }
    }
}
