/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
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

    public TestStartup(Set requiredInits) {
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
