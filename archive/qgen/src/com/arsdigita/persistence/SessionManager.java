/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence;

import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.util.UncheckedWrapperException;
import com.redhat.persistence.EventProcessorManager;
import com.arsdigita.versioning.Versions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * The SessionManager is a purely static class that allows users to
 * retrieve the current Session. It is in charge of initializing the
 * Session with the appropriate connection information. It currently
 * does not support initializing Sessions with more than one schema.
 * It also holds a reference to the global MetadataRoot. It is the
 * responsibility of the initialization to provide the JDBC
 * information and the MetadataRoot to the SessionManager.
 *
 * @see Initializer
 * @author Archit Shah 
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 */

public class SessionManager {

    public static final String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/persistence/SessionManager.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    private static final Logger s_log = Logger.getLogger
        (SessionManager.class.getName());

    private static Set s_beforeFlushProcManagers = new HashSet();
    static {
        addBeforeFlushProcManager(Versions.EPM);
    }
    private static Set s_afterFlushProcManagers  = new HashSet();
    private static Map s_configurations = new HashMap();
    private static ThreadLocal s_sessions = new ThreadLocal() {
        public Object initialValue() {
            return new HashMap();
        }
    };

    private static class Config {

        private String m_name;
        private MetadataRoot m_root;
        private ConnectionSource m_source;

        public Config(String name, MetadataRoot root,
                      ConnectionSource source) {
            m_name = name;
            m_root = root;
            m_source = source;
        }

        public String getName() {
            return m_name;
        }

        public MetadataRoot getRoot() {
            return m_root;
        }

        public ConnectionSource getSource() {
            return m_source;
        }

    }

    public static void configure(String name, MetadataRoot root,
                                 ConnectionSource source) {
        synchronized (s_configurations) {
            if (s_configurations.containsKey(name)) {
                throw new IllegalArgumentException
                    ("already configured: " + name);
            }
            s_configurations.put(name, new Config(name, root, source));
        }
    }

    public static Session open(String name, MetadataRoot root,
                               ConnectionSource source) {
        if (hasSession(name)) {
            throw new IllegalStateException("session already open: " + name);
        }
        Connection conn = source.acquire();
        int database;
        try {
            try {
                database =
                    DbHelper.getDatabaseFromURL(conn.getMetaData().getURL());
            } catch (SQLException e) {
                throw new UncheckedWrapperException(e);
            }
        } finally {
            source.release(conn);
        }

        Session result = new Session(root, source, database);
        for (Iterator ii=s_beforeFlushProcManagers.iterator();
             ii.hasNext(); ) {
            EventProcessorManager mngr = (EventProcessorManager) ii.next();
            result.getProtoSession().addBeforeFlush(mngr.getEventProcessor());
        }
        for (Iterator ii=s_afterFlushProcManagers.iterator(); ii.hasNext(); ) {
            EventProcessorManager mngr = (EventProcessorManager) ii.next();
            result.getProtoSession().addAfterFlush(mngr.getEventProcessor());
        }
        setSession(name, result);
        return result;
    }

    /**
     * @return the session named "default"
     **/
    public static Session getSession() {
        return getSession("default");
    }

    private static Map getSessions() {
        return (Map) s_sessions.get();
    }

    private static void setSession(String name, Session ssn) {
        getSessions().put(name, ssn);
    }

    /**
     *  @return The Session object for the current thread.
     **/
    public static Session getSession(String name) {
        Map map = getSessions();
        if (!map.containsKey(name)) {
            synchronized (s_configurations) {
                Config conf = (Config) s_configurations.get(name);
                if (conf == null) {
                    return null;
                } else {
                    return open(name, conf.getRoot(), conf.getSource());
                }
            }
        }
        return (Session) map.get(name);
    }

    /**
     * @return true if a session with the given name has been opened.
     **/
    public static boolean hasSession(String name) {
        return getSessions().containsKey(name);
    }

    /**
     * This method provides an indirect way for applications to
     * register {@link com.redhat.persistence.EventProcessor event
     * processors} with the {@link Session session} object.
     *
     * <p>This works like so</p>
     *
     * <ul>
     *  <li>You register your {@link EventProcessorManager event
     *  processor manager} with this session manager.</li>
     *
     *  <li>Each {@link Session session} returned by {@link
     *  #getSession()} will have a reference to a single (per thread)
     *  instance of the {@link com.redhat.persistence.EventProcessor
     *  event processor} managed the {@link EventProcessorManager
     *  event processor manager} that you registered.</li>
     *
     *  <li>The {@link com.redhat.persistence.Session session} will
     *  dispatch events from its {@link
     *  com.redhat.persistence.Session#flush()} method to to your
     *  event processor's <code>write(Event)</code> method. </li>
     * </ul>
     **/
    public static synchronized void addBeforeFlushProcManager
        (EventProcessorManager manager) {

        s_beforeFlushProcManagers.add(manager);
    }

    /**
     * @see #addBeforeFlushProcManager(EventProcessorManager)
     **/
    public static synchronized void addAfterFlushProcManager
        (EventProcessorManager manager) {

        s_afterFlushProcManagers.add(manager);
    }

    /**
     *  This returns the metadata root
     *
     *  @return The global MetadataRoot.
     */
    public static MetadataRoot getMetadataRoot() {
        return getSession().getMetadataRoot();
    }

}
