package com.arsdigita.tools.junit.extensions;

import com.arsdigita.runtime.Startup;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;

import java.sql.Connection;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collections;

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


/**
 * TestStartup: Extends Startup to limit the required initializers when tests are run.
 * If a normal Startup were used, ClassNotFound exceptions would occur when it tried
 * to load a cms class during core tests, for example.
 */
public class TestStartup extends Startup {
    private Set m_requiredInits;

    public TestStartup(Connection conn, Set requiredInits) {
        super(conn);
        if (requiredInits.size() == 0) {
            throw new IllegalStateException("Empty set of required initializers!");
        }

        m_requiredInits = requiredInits;
    }

    protected Collection getRuntimeInitializerNames() {

        Set allInits = new HashSet(super.getRuntimeInitializerNames());

        // Verify that the required inits added are valid.
        // If they don't exist in the super set, an error has occured.
        for (Iterator iterator = m_requiredInits.iterator(); iterator.hasNext();) {
            final String initName = (String) iterator.next();
            if (!allInits.contains(initName)) {
                throw new IllegalStateException("Unknown required initializer: " + initName);
            }
        }

        return Collections.unmodifiableCollection(m_requiredInits);
    }


}
