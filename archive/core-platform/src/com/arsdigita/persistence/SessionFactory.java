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

/**
 * <b><font color="red">Experimental</font></b>  An internal interface that represents a Factory
 * object for creating sessions. The implementation is instantiated in the {@link Initializer} and
 * passed to the {@link SessionManager}. Tests can override the default factory to stub out the persistence
 * implmentation, test error handling, etc.
 *
 * @author <a href="mailto:jorris@redhat.com">jorris@redhat.com</a>
 * @version $Revision: #1 $ $Date: 2002/08/22 $
 */
interface SessionFactory {

    /**
     * Creates a new InternalSession.
     *
     * @param schema The name of the schema.
     * @param url The JDBC URL.
     * @param username The db username.
     * @param password The db password.
     *
     * @return A new InternalSession
     */
    InternalSession newSession(String schema, String url, String username, String password);

    /**
     * Turns aggressive closing on or off.
     *
     * @param value If true, aggressive closing will be used.
     */
    void setAggressiveConnectionClose(boolean value);
}
