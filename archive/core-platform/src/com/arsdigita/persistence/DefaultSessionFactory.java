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

class DefaultSessionFactory implements SessionFactory {

    /**
     * Creates a new SessionImpl for use by the persistence subsystem.
     *
     * @param schema The name of the schema.
     * @param url The JDBC URL.
     * @param username The db username.
     * @param password The db password.
     *
     * @return A new SessionImpl
     */
    public InternalSession newSession(String schema, String url, String username, String password) {
        return new SessionImpl(schema, url, username, password);
    }

    /**
     * Turns aggressive closing on or off.
     *
     * @param value If true, aggressive closing will be used.
     */
    public void setAggressiveConnectionClose(boolean value) {
        TransactionContextImpl.setAggressiveClose(value);
    }

}
