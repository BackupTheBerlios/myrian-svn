/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util;

import com.arsdigita.domain.DomainQuery;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;


public class SimpleDataQuery extends DomainQuery {

    public SimpleDataQuery(String name) {
        super(getDataQuery(name));
    }

    private static DataQuery getDataQuery(String name) {
        Session session = SessionManager.getSession();
        return session.retrieveQuery(name);
    }
}
