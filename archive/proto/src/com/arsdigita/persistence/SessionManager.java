/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.proto.EventProcessor;
import com.arsdigita.persistence.proto.EventProcessorManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * The SessionManager is a purely static class that allows users to retrieve
 * the current Session. It is in charge of initializing the Session with the
 * appropriate connection information. It currently does not support
 * initializing Sessions with more than one schema. It also holds a reference
 * to the global MetadataRoot. It is the responsibility of the initialization
 * to provide the JDBC information and the MetadataRoot to the SessionManager.
 *
 * @see Initializer
 * @author Archit Shah (ashah@arsdigita.com)
 * @version $Revision: #4 $ $Date: 2003/03/25 $
 */

public class SessionManager {

    public static final String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/SessionManager.java#4 $ by $Author: vadim $, $DateTime: 2003/03/25 17:26:50 $";

    private static String s_url = null;           // the jdbc URL
    private static String s_username = null;      // the database username
    private static String s_password = null;      // the database password
    private static ThreadLocal s_session;  // the session
    private static SQLUtilities s_sqlUtil;
    private static Set s_beforeFlushProcManagers = new HashSet();
    private static Set s_afterFlushProcManagers  = new HashSet();

    private static final Logger s_cat =
        Logger.getLogger(SessionManager.class.getName());


    static {
        resetSchemaConnectionInfo();
    }

    /**
     *  This returns the metadata root
     *
     *  @return The global MetadataRoot.
     */
    public static synchronized MetadataRoot getMetadataRoot() {
        return MetadataRoot.getMetadataRoot();
    }


    /**
     *  @return The Session object for the current thread.
     */
    public static Session getSession() {
        return (Session) s_session.get();
    }

    /**
     * This method provides an indirect way for applications to register {@link
     * com.arsdigita.persistence.proto.EventProcessor event processors} with the
     * {@link Session session} object.
     *
     * <p>This works like so</p>
     *
     * <ul>
     *  <li>You register your {@link EventProcessorManager event processor manager}
     *      with this session manager.</li>
     *
     *  <li>Each {@link Session session} returned by {@link #getSession()} will
     *  have a reference to a single (per thread) instance of the {@link
     *  com.arsdigita.persistence.proto.EventProcessor event processor} managed
     *  the {@link EventProcessorManager event processor manager} that you
     *  registered.</li>
     *
     *  <li>The {@link com.arsdigita.persistence.proto.Session session} will
     *  dispatch events from its {@link
     *  com.arsdigita.persistence.proto.Session#flush()} method to to your event
     *  processor's <code>write(Event)</code> method. </li>
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
     *  This sets the connection info for this session manager
     *
     *  @param schema The schema to use.  Right now, Session only
     *                supports a single connection so this currently
     *                ignores the schema
     *  @param url The JDBC URL
     *  @param username The database username
     *  @param password The database password
     */
    static synchronized void setSchemaConnectionInfo(String schema, String url,
                                                     String username, String password) {
        // Right now Session only supports one connection, so just ignore the
        // schema.
        s_url = url;
        s_username = username;
        s_password = password;
    }

/*    static synchronized void setSessionFactory(SessionFactory factory) {
        s_factory = factory;
        }*/

    /**
     *  This resets the connection info by "forgetting" the schema, url,
     *  username, password, and session
     */
    static synchronized void resetSchemaConnectionInfo() {
        if (s_cat.isDebugEnabled()) {
            s_cat.debug("Resetting schema connection", new Throwable());
        }
        s_session = new ThreadLocal() {
                public Object initialValue() {
                    StringBuffer sb = new StringBuffer();
                    if (s_url == null) {
                        sb.append(Utilities.LINE_BREAK + "  url is null");
                    }
                    if (s_username == null) {
                        sb.append(Utilities.LINE_BREAK + "  username is null");
                    }
                    if (s_password == null) {
                        sb.append(Utilities.LINE_BREAK + "  password is null");
                    }
                    if (sb.length() > 0) {
                        throw new IllegalStateException("SessionManager has " +
                                                        "not been initialized: " +
                                                        sb.toString());
                    }
                    Session s = new Session();
                    for (Iterator ii=s_beforeFlushProcManagers.iterator(); ii.hasNext(); ) {
                        EventProcessorManager mngr = 
                            (EventProcessorManager) ii.next();
                        s.getProtoSession().addBeforeFlush(mngr.getEventProcessor());
                    }
                    for (Iterator ii=s_afterFlushProcManagers.iterator(); ii.hasNext(); ) {
                        EventProcessorManager mngr = 
                            (EventProcessorManager) ii.next();
                        s.getProtoSession().addAfterFlush(mngr.getEventProcessor());
                    }
                    return s;
                }
            };
    }

    /**
     *   - This retrieves the
     *  factory that is used to create the filters for this DataQuery.
     */
    public static SQLUtilities getSQLUtilities() {
        return s_sqlUtil;
    }


    /**
       *  This sets the SQLUtilities for the system.
       */
    public static void setSQLUtilities(SQLUtilities util) {
          s_sqlUtil = util;
    }


}
