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
 * @version $Revision: #4 $ $Date: 2002/08/14 $
 */

public class SessionManager {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/SessionManager.java#4 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    private static String s_url = null;           // the jdbc URL
    private static String s_username = null;      // the database username
    private static String s_password = null;      // the database password
    private static ThreadLocal s_session;  // the session

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
                    Session s = new Session(getMetadataRoot());
                    s.setSchemaConnectionInfo("", s_url, s_username, s_password);
                    return s;
                }
            };
    }
}
